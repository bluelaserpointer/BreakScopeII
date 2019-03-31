package unit;


import action.Action;
import action.ActionInfo;
import core.GHQ;
import geom.Circle;
import geom.Square;
import item.Item;
import paint.DotPaint;
import paint.RectPaint;
import physicis.Dynam;
import physicis.HasDynam;
import storage.ItemStorage;
import storage.Storage;
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
	public final Status status = new Status(PARAMETER_AMOUNT);
	//inventory
	public final ItemStorage inventory;
	
	public BSUnit(int charaSize, int initialGroup) {
		super(new Circle(charaSize), initialGroup);
		inventory = new ItemStorage(new Storage<Item>());
	}
	public BSUnit(int charaSize, int initialGroup, Storage<Item> itemStorageKind) {
		super(new Square(charaSize), initialGroup);
		inventory = new ItemStorage(itemStorageKind);
	}
	@Override
	public void loadImageData() {
	}

	@Override
	public void respawn(int x, int y) {
		super.resetOrder();
		status.reset();
		final Dynam DYNAM = getDynam();
		DYNAM.clear();
		DYNAM.setXY(charaDstX = x, charaDstY = y);
		charaOnLand = false;
		slot_spell = 0;
	}
	@Override
	public void dynam() {
		final Dynam DYNAM = getDynam();
		DYNAM.move();
		DYNAM.accelerate_MUL(0.9);
	}
	@Override
	public void activeCons() {
		// death
		if (!isAlive()) {
			return;
		}
		final int mouseX = GHQ.getMouseX(), mouseY = GHQ.getMouseY();
		Dynam DYNAM = getDynam();
		DYNAM.setAngle(DYNAM.getMouseAngle());
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
		DYNAM.approach(charaDstX, charaDstY, charaSpeed);
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
		final Dynam DYNAM = getDynam();
		final int X = (int) DYNAM.getX(),Y = (int) DYNAM.getY();
		charaPaint.dotPaint(X, Y);
		GHQ.paintHPArc(X, Y, 20,status.get(HP), status.getDefault(HP));
	}
	protected final void paintMode_magicCircle(DotPaint paintScript) {
		final Dynam DYNAM = getDynam();
		final int X = (int) DYNAM.getX(),Y = (int) DYNAM.getY();
		paintScript.dotPaint_turn(X, Y, (double)GHQ.getNowFrame()/35.0);
		charaPaint.dotPaint(X, Y);
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
		getDynam().addXY(x, y);
		charaDstX += x;
		charaDstY += y;
	}
	@Override
	public void teleportTo(int x,int y) {
		getDynam().setXY(charaDstX = x, charaDstY = y);
	}
	private Action actionPlan;
	private int initialFrame;
	@Override
	public void loadActionPlan(Action action) {
		actionPlan = action;
	}
	protected void doActionPlan() {
		int countedFrame = 0;
		final Dynam DYNAM = getDynam();
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
					charaDstX = DYNAM.getX() + X;
					charaDstY = DYNAM.getY() + Y;
					break;
				case ActionInfo.ATTACK:
					setBullet(weaponSlot[slot_weapon],this);
					break;
				case ActionInfo.SPEED:
					DYNAM.addXY(X,Y);
					break;
				}
			}
			
		}
	}
	private final void dodge(double targetX, double targetY) {
		final Dynam DYNAM = getDynam();
		DYNAM.addSpeed_DA(40, DYNAM.getAngle(targetX,targetY));
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
	public abstract void setBullet(int kind,HasDynam source);
	public abstract void setEffect(int kind,HasDynam source);
}