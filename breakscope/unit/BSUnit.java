package unit;


import action.Action;
import action.ActionInfo;
import bullet.Bullet;
import core.GHQ;
import paint.DotPaint;
import paint.RectPaint;
import physicis.DynamInteractable;
import unit.Status;
import unit.Unit;
import weapon.Weapon;

public abstract class BSUnit extends Unit {
	private static final long serialVersionUID = -3074084304336765077L;
	public int charaSize;
	public double charaDstX, charaDstY, charaSpeed = 30;
	public boolean charaOnLand;

	// Weapon
	public int slot_spell, slot_weapon;
	public final int spellSlot_max = 6, weaponSlot_max = 6, weapon_max = 10;
	public final int[] spellSlot = new int[spellSlot_max], weaponSlot = new int[weaponSlot_max];
	protected final Weapon weaponController[] = new Weapon[10];

	// GUI
	public RectPaint iconPaint;

	// Resource
	// Images
	public DotPaint charaPaint;
	public final DotPaint bulletPaint[] = new DotPaint[weapon_max], effectPaint[] = new DotPaint[10];
	
	//special
	public int favorDegree;
	//status constants

	public static final int PARAMETER_AMOUNT = 10;
	public static final int HP = 0,SPD = 1,ATK = 2,AGI = 3,CRI = 4,BLO = 5,STUN = 6;
	public static final int MP = 9;
	private static final String names[] = new String[PARAMETER_AMOUNT];
	static {
		names[HP] = "HP";
		names[SPD] = "SPD";
		names[ATK] = "ATK";
		names[AGI] = "AGI";
		names[CRI] = "CRI";
		names[BLO] = "BLO";
		names[STUN] = "STUN";
		names[MP] = "MP";
	}
	public BSUnit(int initialGroup) {
		super(new Status(PARAMETER_AMOUNT) {
			private static final long serialVersionUID = -8559880661969472776L;
			@Override
			public void capCheck(int index) {
				switch(index) {
				case HP:
				case MP:
					resetIfOverDefault(index);
					break;
				}
			}
		},initialGroup);
	}
	@Override
	public void loadImageData() {
	}

	@Override
	public void respawn(int x, int y) {
		super.resetOrder();
		status.reset();
		super.dynam.clear();
		super.dynam.setXY(charaDstX = x, charaDstY = y);
		charaOnLand = false;
		slot_spell = 0;
	}
	@Override
	public void dynam() {
		dynam.move();
		dynam.accelerate_MUL(0.9);
	}
	@Override
	public void activeCons() {
		// death
		if (!isAlive()) {
			return;
		}
		final int mouseX = GHQ.getMouseX(), mouseY = GHQ.getMouseY();
		dynam.setAngle(dynam.getMouseAngle());
		// dodge
		if (super.dodgeOrder)
			dodge(mouseX, mouseY);
		// attack
		if (super.attackOrder) {
			final int weapon = weaponSlot[slot_weapon];
			if (weapon != NONE && useWeapon(weapon))
				setBullet(weapon,this);
		}
		// spell
		if (super.spellOrder) {
			final int spell = spellSlot[slot_spell];
			if (spell != NONE && useWeapon(spell))
				setBullet(spell,this);
		}
		// move
		if (super.moveOrder) {
			//under edit
		}
		dynam.approach(charaDstX, charaDstY, charaSpeed);
		// weaponChange
		int roll = super.weaponChangeOrder;
		if (roll != 0) {
			int target = slot_spell;
			if (roll > 0) {
				while (target < spellSlot_max - 1) {
					if (spellSlot[++target] != NONE) {
						if (--roll == 0)
							break;
					}
				}
			} else {
				while (target > 0) {
					if (spellSlot[--target] != NONE) {
						if (++roll == 0)
							break;
					}
				}
			}
			slot_spell = target;
		}
	}
	@Override
	public void paint(boolean doAnimation) {
		if(status.get(HP) <= 0)
			return;
		final int X = (int) dynam.getX(),Y = (int) dynam.getY();
		charaPaint.paint(X, Y);
		GHQ.paintHPArc(X, Y, 20,status.get(HP), status.getDefault(HP));
	}
	protected final void paintMode_magicCircle(DotPaint paintScript) {
		final int X = (int) dynam.getX(),Y = (int) dynam.getY();
		paintScript.paint(X, Y, (double)GHQ.getNowFrame()/35.0);
		charaPaint.paint(X, Y);
	}
	
	// control
	// move
	@Override
	public void moveRel(int x,int y) {
		charaDstX += x;
		charaDstY += y;
	}
	@Override
	public void moveTo(int x,int y) {
		charaDstX = x;
		charaDstY = y;
	}
	@Override
	public void teleportRel(int x,int y) {
		dynam.addXY(x, y);
		charaDstX += x;
		charaDstY += y;
	}
	@Override
	public void teleportTo(int x,int y) {
		dynam.setXY(charaDstX = x, charaDstY = y);
	}
	private Action actionPlan;
	private int initialFrame;
	@Override
	public void loadActionPlan(Action action) {
		actionPlan = action;
	}
	protected void doActionPlan() {
		int countedFrame = 0;
		for(int i = 0;i < actionPlan.frame.length;i++) {
			final int FRAME = actionPlan.frame[i];
			if((countedFrame += FRAME) == initialFrame) { //reach planned timing
				final double X = actionPlan.x[i],
					Y = actionPlan.y[i];
				switch(actionPlan.meaning[i]) {
				case ActionInfo.DST:
					charaDstX = X;
					charaDstY = Y;
					break;
				case ActionInfo.MOVE:
					charaDstX = dynam.getX() + X;
					charaDstY = dynam.getY() + Y;
					break;
				case ActionInfo.ATTACK:
					setBullet(weaponSlot[slot_weapon],this);
					break;
				case ActionInfo.SPEED:
					dynam.addXY(X,Y);
					break;
				}
			}
			
		}
	}
	// judge
	@Override
	public final boolean bulletEngage(Bullet bullet) {
		return status.isBigger0(HP) && dynam.squreCollision(bullet.dynam,(charaSize + bullet.SIZE)/2)
				&& (bullet.isFriendly(this) ^ bullet.atk >= 0);
	}
	private final void dodge(double targetX, double targetY) {
		dynam.addSpeed_DA(40, dynam.getAngle(targetX,targetY));
		charaOnLand = false;
	}
	//stun
	public boolean pullStun() {
		if(status.get(STUN) <= 0)
			return false;
		status.add(STUN,-1);
		return true;
	}

	// decreases
	@Override
	public final int damage_amount(int amount) {
		return status.add(HP, -amount);
	}

	@Override
	public final int damage_rate(double rate) {
		return status.reduce_rate(HP, rate);
	}
	@Override
	public final boolean kill(boolean force) {
		status.set0(HP);
		return true;
	}

	// information
	@Override
	public String getName() {
		return GHQ.NOT_NAMED;
	}
	@Override
	public final boolean isAlive() {
		return status.get(HP) > 0;
	}
	public boolean useWeapon(int kind) {
		return true;
	}
	public abstract void setBullet(int kind,DynamInteractable source);
	public abstract void setEffect(int kind,DynamInteractable source);
}