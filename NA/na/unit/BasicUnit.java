package unit;

import core.GHQ;
import hitShape.Circle;
import item.Ammo;
import item.Equipment;
import item.ItemData;
import paint.dot.DotPaint;
import paint.rect.RectPaint;
import physics.Dynam;
import physics.HasAnglePoint;
import physics.Point;
import physics.Standpoint;
import status.StatusWithDefaultValue;
import storage.ItemStorage;
import storage.Storage;
import unit.Unit;
import weapon.Weapon;
import weapon.WeaponInfo;

public abstract class BasicUnit extends Unit {
	private static final long serialVersionUID = -3074084304336765077L;
	public int charaSize;
	public Point.IntPoint dstPoint = new Point.IntPoint();
	public double charaSpeed = 30;
	public boolean charaOnLand;

	//effect

	public static final int ACCAR_HIT_EF = 0;
	// Weapon
	public final int weapon_max = 10;
	public Weapon
		mainWeapon = Weapon.NULL_WEAPON,
		subWeapon = Weapon.NULL_WEAPON,
		meleeWeapon = Weapon.NULL_WEAPON,
		spellWeapon = Weapon.NULL_WEAPON;

	// GUI
	public RectPaint iconPaint;

	// Resource
	// Images
	public DotPaint charaPaint;
	//special
	public int favorDegree;
	//status constants

	public static final int PARAMETER_AMOUNT = 9;
	public static final int RED_BAR = 0,BLUE_BAR = 1,GREEN_BAR = 2,
			POW_FIXED = 3,POW_FLOAT = 4,
			INT_FIXED = 5,INT_FLOAT = 6,
			AGI_FIXED = 7,AGI_FLOAT = 8;
	private static final String names[] = new String[PARAMETER_AMOUNT];
	static {
		names[RED_BAR] = "RED_BAR";
		names[BLUE_BAR] = "BLUE_BAR";
		names[GREEN_BAR] = "GREEN_BAR";
		names[POW_FIXED] = "POW_FIXED";
		names[POW_FLOAT] = "POW_FLOAT";
		names[INT_FIXED] = "INT_FIXED";
		names[INT_FLOAT] = "INT_FLOAT";
		names[AGI_FIXED] = "AGI_FIXED";
		names[AGI_FLOAT] = "AGI_FLOAT";
	}
	public final StatusWithDefaultValue status = new StatusWithDefaultValue(PARAMETER_AMOUNT) {
		private static final long serialVersionUID = 6570141041982027510L;
		{
			parameterDefaults[POW_FIXED] = 5;
			parameterDefaults[INT_FIXED] = 5;
			parameterDefaults[AGI_FIXED] = 5;
			parameterDefaults[RED_BAR] = 20;
			parameterDefaults[BLUE_BAR] = 20;
			parameterDefaults[GREEN_BAR] = 20;
		}
		@Override
		public int capCheck(int index) {
			final int VALUE = parameters[index];
			switch(index) {
			case RED_BAR:
			case BLUE_BAR:
			case GREEN_BAR:
				return GHQ.arrangeIn(VALUE, 0, getDefault(index));
			case POW_FIXED:
			case INT_FIXED:
			case AGI_FIXED:
				return VALUE > 0 ? VALUE : 0;
			default:
				return VALUE;
			}
		}
		@Override
		public int getDefault(int index) {
			switch(index) {
			case RED_BAR:
				return parameterDefaults[RED_BAR]*POW_FIXED;
			case BLUE_BAR:
				return parameterDefaults[BLUE_BAR]*INT_FIXED;
			case GREEN_BAR:
				return parameterDefaults[GREEN_BAR]*AGI_FIXED;
			default:
				return isLegalIndex(index) ? parameterDefaults[index] : GHQ.NONE;
			}
		}
	};
	//weapons
	public final Weapon getWeapon(Equipment equipment) {
		WeaponInfo.clear();
		switch(equipment.EQUIPMENT_ID) {
		case Equipment.ACCAR:
			WeaponInfo.name = Equipment.eqiupmentNames[Equipment.ACCAR];
			WeaponInfo.coolTime = 50;
			WeaponInfo.magazineSize = 10;
			WeaponInfo.reloadTime = 150;
			return new Weapon() {
				private static final long serialVersionUID = -1692674505068462831L;
				@Override
				public void setBullets(HasAnglePoint shooter, Standpoint standpoint) {
					final Dynam BULLET_DYNAM = GHQ.stage().addBullet(new BulletLibrary.ACCAR(this, shooter, standpoint)).dynam;
					BULLET_DYNAM.setSpeed(10);
					BULLET_DYNAM.addXY_allowsMoveAngle(0, 18);
				}
				@Override
				public int getLeftAmmo() {
					return inventory.countItemByName(getCurrentAmmoName());
				}
				@Override
				public void consumeAmmo(int value) {
					ItemData.removeInInventory(inventory.items, new Ammo(Ammo.AMMO_9MM, value));
				}
			};
		case Equipment.ELECTRON_SHIELD:
			WeaponInfo.name = Equipment.eqiupmentNames[Equipment.ELECTRON_SHIELD];
			WeaponInfo.magazineSize = 50;
			return new Weapon() {
				private static final long serialVersionUID = -3255234899242748103L;
				@Override
				public int getLeftAmmo() {
					return status.get(BLUE_BAR);
				}
				@Override
				public void consumeAmmo(int value) {
					status.add(BLUE_BAR, -value);
				}
			};
		default:
			return Weapon.NULL_WEAPON;
		}
	}
	public String getCurrentAmmoName() {
		return Ammo.ammoNames[Ammo.AMMO_9MM];
	}
	//inventory
	public final ItemStorage inventory = def_inventory();
	protected ItemStorage def_inventory() {
		return new ItemStorage(new Storage<ItemData>());
	}
	
	public BasicUnit(int charaSize, int initialGroup) {
		super(new Circle(charaSize), initialGroup);
	}
	@Override
	public void respawn(int x, int y) {
		resetOrder();
		status.reset();
		mainWeapon.reset();
		subWeapon.reset();
		meleeWeapon.reset();
		dynam.clear();
		dstPoint.setXY(dynam.setXY(x, y));
		baseAngle.set(0.0);
		charaOnLand = false;
		inventory.items.clear();
		inventory.add_stack(new Ammo(Ammo.AMMO_9MM, 32));
	}
	public void resetOrder() {
		weaponChangeOrder = 0;
		attackOrder = dodgeOrder = spellOrder = false;
	}
	public void resetSingleOrder() {
		weaponChangeOrder = 0;
		spellOrder = dodgeOrder = false;
	}
	@Override
	public void idle() {
		super.idle();
		////////////
		// weapon
		////////////
		mainWeapon.idle();
		subWeapon.idle();
		meleeWeapon.idle();
		subWeapon.startReloadIfNotDoing();
		meleeWeapon.startReloadIfNotDoing();
		////////////
		//dynam
		////////////
		dynam.moveIfNoObstacles(this);
		dynam.accelerate_MUL(0.9);
	}
	public int weaponChangeOrder;
	public boolean attackOrder,dodgeOrder,spellOrder;
	@Override
	public void paint(boolean doAnimation) {
		charaPaint.dotPaint_turn(this);
	}
	protected final void paintMagicCircle(DotPaint paintScript) {
		paintScript.dotPaint_turn(dynam, (double)GHQ.nowFrame()/35.0);
	}
	public void killed() {
		for(int i = inventory.items.traverseFirst();i != -1;i = inventory.items.traverseNext(i))
			GHQ.stage().addVegetation(inventory.items.remove(i).drop((int)(dynam.doubleX() + GHQ.random2(-50,50)), (int)(dynam.doubleY() + GHQ.random2(-50,50))));
	}
	
	// control
	// move
	protected final void dodge(double targetX, double targetY) {
		final Dynam DYNAM = dynam();
		DYNAM.addSpeed_DA(40, DYNAM.angleTo(targetX,targetY));
		charaOnLand = false;
	}
	//stun
	public boolean pullStun() {
		return false;
	}

	// decreases
	@Override
	public int damage_amount(int amount) {
		if(subWeapon.NAME.equals(Equipment.eqiupmentNames[Equipment.ELECTRON_SHIELD])) {
			if(subWeapon.unload(amount) == amount) //shield success
				return amount;
			else
				subWeapon = Weapon.NULL_WEAPON; //destroy shield and take damage
		}
		final int DMG = status.add(RED_BAR, -amount);
		GHQ.stage().addEffect(new EffectLibrary.DamageNumberEF(this, DMG));
		if(!isAlive())
			killed();
		return DMG;
	}
	public final boolean kill(boolean force) {
		status.set(RED_BAR, 0);
		killed();
		return true;
	}

	// information
	@Override
	public String getName() {
		return GHQ.NOT_NAMED;
	}
	@Override
	public final boolean isAlive() {
		return status.get(RED_BAR) > 0;
	}
}