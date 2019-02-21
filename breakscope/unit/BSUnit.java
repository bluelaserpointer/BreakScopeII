package unit;


import action.Action;
import action.ActionInfo;
import bullet.Bullet;
import core.DynamInteractable;
import core.GHQ;
import unit.Status;
import unit.Unit;
import weapon.Weapon;

public abstract class BSUnit extends Unit {

	public double charaDstX, charaDstY, charaSpeed = 30;
	public boolean charaOnLand;

	// Weapon
	public int slot_spell, slot_weapon;
	public final int spellSlot_max = 6, weaponSlot_max = 6, weapon_max = 10;
	public final int[] spellSlot = new int[spellSlot_max], weaponSlot = new int[weaponSlot_max];
	protected final Weapon weaponController[] = new Weapon[10];

	// GUI
	public int faceIID;

	// Resource
	// Images
	public int charaIID;
	public final int bulletIID[] = new int[weapon_max], effectIID[] = new int[10];
	
	//status constants

	public static final int PARAMETER_AMOUNT = 10;
	public static final int HP = 0,SPD = 1,ATK = 2,AGI = 3,CRI = 4,BLO = 5,STUN = 6;
	public static final int TEAM = 7;
	public static final int SIZE = 8;
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
		names[TEAM] = "TEAM";
		names[SIZE] = "SIZE";
		names[MP] = "MP";
	}
	public BSUnit() {
		super(new Status(PARAMETER_AMOUNT) {
			@Override
			public void capCheck(int index) {
				switch(index) {
				case HP:
				case MP:
					resetIfOverDefault(index);
					break;
				}
			}
		});
	}
	@Override
	public void loadImageData() {
	}

	@Override
	public void respawn(int charaTeam, int x, int y) {
		super.resetOrder();
		status.reset();
		status.set(TEAM, charaTeam);
		super.dynam.clear();
		super.dynam.setXY(charaDstX = x, charaDstY = y);
		charaOnLand = false;
		slot_spell = 0;
	}
	@Override
	public void respawn(int charaTeam, int x, int y,int hp) {
		status.setDefault(HP,hp);
		this.respawn(charaTeam, x, y);
	}
	@Override
	public void dynam() {
		if(!isMovable())
			return;
		dynam.move();
		dynam.accelerate_MUL(0.9);
	}
	@Override
	public void activeCons() {
		// death
		if (isAlive()) {
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
		if(isAlive())
			return;
		final int X = (int) dynam.getX(),Y = (int) dynam.getY();
		GHQ.drawImageGHQ_center(charaIID, X, Y);
		GHQ.paintHPArc(X, Y, 20,status.get(HP), status.getDefault(HP));
	}
	protected final void paintMode_magicCircle(int magicCircleIID) {
		final int X = (int) dynam.getX(),Y = (int) dynam.getY();
		GHQ.drawImageGHQ_center(magicCircleIID, X, Y, (double)GHQ.getNowFrame()/35.0);
		GHQ.drawImageGHQ_center(charaIID, X, Y);
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
		return status.isBigger0(HP) && dynam.squreCollision(bullet.dynam,(status.get(SIZE) + bullet.SIZE)/2)
				&& (bullet.team == status.get(TEAM) ^ bullet.atk >= 0);
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

	// decrease
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
	public final int getTeam() {
		return status.get(TEAM);
	}
	@Override
	public final boolean isAlive() {
		return status.get(HP) > 0;
	}
	@Override
	public boolean isMovable() {
		return true;
	}
	public boolean useWeapon(int kind) {
		return true;
	}
	public abstract void setBullet(int kind,DynamInteractable source);
	public abstract void setEffect(int kind,DynamInteractable source);
}