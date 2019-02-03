package chara;


import action.Action;
import action.ActionInfo;
import bullet.Bullet;
import chara.Chara;
import core.DynamInteractable;
import core.GHQ;
import weapon.Weapon;

public abstract class UserChara extends Chara {

	public int charaTeam, charaHP, charaME, charaBaseHP, charaBaseME, charaSpellCharge,
			charaSize, charaStatus;
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

	@Override
	public void loadImageData() { // �����i���z��
	}

	@Override
	public void respawn(int charaTeam, int x, int y) {
		super.resetOrder();
		this.charaTeam = charaTeam;
		super.dynam.clear();
		super.dynam.setXY(charaDstX = x, charaDstY = y);
		charaStatus = NONE;
		charaOnLand = false;
		slot_spell = 0;
	}
	@Override
	public void respawn(int charaTeam, int x, int y,int hp) {
		charaHP = charaBaseHP = hp;
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
		if (charaHP <= 0) {
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
		if(charaHP <= 0)
			return;
		final int X = (int) dynam.getX(),Y = (int) dynam.getY();
		GHQ.drawImageTHH_center(charaIID, X, Y);
		GHQ.paintHPArc(X, Y, 20,charaHP, charaBaseHP);
	}
	protected final void paintMode_magicCircle(int magicCircleIID) {
		final int X = (int) dynam.getX(),Y = (int) dynam.getY();
		GHQ.drawImageTHH_center(magicCircleIID, X, Y, (double)GHQ.getNowFrame()/35.0);
		GHQ.drawImageTHH_center(charaIID, X, Y);
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
		return charaHP > 0 && GHQ.squreCollision((int) dynam.getX(), (int) dynam.getY(), charaSize, (int) bullet.dynam.getX(),(int)bullet.dynam.getY(), bullet.SIZE)
				&& (bullet.team == charaTeam ^ bullet.atk >= 0);
	}
	// hp
	@Override
	public final void setHP(int hp) {
		charaHP = hp;
	}
	private final void dodge(double targetX, double targetY) {
		dynam.addSpeed_DA(40, dynam.getAngle(targetX,targetY));
		charaOnLand = false;
	}

	// decrease
	@Override
	public final int decreaseME_amount(int amount) {
		charaME -= amount;
		return amount;
	}

	@Override
	public final int decreaseME_rate(double rate) {
		final int value = (int) (charaME * rate);
		charaME -= value;
		return value;
	}

	@Override
	public final int damage_amount(int amount) {
		charaHP -= amount;
		return amount;
	}

	@Override
	public final int damage_rate(double rate) {
		final int damage = (int) (charaHP * rate);
		charaHP -= damage;
		return damage;
	}

	@Override
	public final boolean kill(boolean force) {
		charaHP = 0;
		return true;
	}

	// information
	@Override
	public String getName() {
		return GHQ.NOT_NAMED;
	}
	@Override
	public final int getTeam() {
		return charaTeam;
	}
	@Override
	public final int getHP() {
		return charaHP;
	}

	@Override
	public final double getHPRate() {
		return (double) charaHP / (double) charaBaseHP;
	}

	@Override
	public final int getME() {
		return charaME;
	}

	@Override
	public final double getMERate() {
		return (double) charaME / (double) charaBaseME;
	}

	@Override
	public final int getStatus() {
		return charaStatus;
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