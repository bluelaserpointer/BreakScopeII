package unit;

import java.util.HashMap;
import java.util.LinkedList;

import buff.Buff;
import buff.ToughnessBroke;
import bullet.Bullet;
import calculate.Calculation;
import calculate.ConsumableEnergy;
import calculate.Damage;
import core.GHQ;
import core.GHQObject;
import damage.DamageMaterialType;
import damage.NADamage;
import gui.stageEditor.GHQObjectHashMap;
import item.ItemData;
import item.NAItem;
import item.ammo.Ammo_9mm;
import item.weapon.ElectronShield;
import item.weapon.Equipment;
import item.weapon.MainSlot;
import item.weapon.MelleSlot;
import item.weapon.SubSlot;
import paint.dot.DotPaint;
import paint.rect.RectPaint;
import physics.Dynam;
import physics.HasPoint;
import physics.HitGroup;
import physics.Point;
import physics.hitShape.Circle;
import status.Status;
import storage.ItemStorage;
import storage.Storage;
import unit.Unit;

public abstract class NAUnit extends Unit {
	private static final long serialVersionUID = -3074084304336765077L;
	public int charaSize;
	public Point.IntPoint dstPoint = new Point.IntPoint();
	public double charaSpeed = 30;
	public boolean charaOnLand;
	
	//effect

	public static final int ACCAR_HIT_EF = 0;
	// Weapon
	public final int weapon_max = 10;
	protected Equipment
		mainSlot = Equipment.NULL_EQUIPMENT,
		subSlot = Equipment.NULL_EQUIPMENT,
		melleSlot = Equipment.NULL_EQUIPMENT;

	// GUI
	public RectPaint iconPaint;

	// Resource
	// Images
	public DotPaint charaPaint = DotPaint.BLANK_SCRIPT;
	//special
	public int favorDegree;
	//buff
	public LinkedList<Buff> buffs = new LinkedList<Buff>(),
			waitingBuffs = new LinkedList<Buff>(),
			waitingDeleteBuffs = new LinkedList<Buff>();
	//status
	private NADamage lastDamage;
	private final HashMap<DamageMaterialType, Double> damageResMap = new HashMap<DamageMaterialType, Double>();
	{
		damageResMap.put(DamageMaterialType.Heat, 0.0);
		damageResMap.put(DamageMaterialType.Ice, 0.0);
		damageResMap.put(DamageMaterialType.Phy, 0.0);
		damageResMap.put(DamageMaterialType.Poi, 0.0);
	}
	public final ConsumableEnergy
		POW_FIXED = new ConsumableEnergy(4).setMin(1),
		POW_FLOAT = new ConsumableEnergy(new Calculation() {
			@Override
			public Number calculate(Number number) {
				return (int)(POW_FIXED.doubleValue()*RED_BAR.getRate());
			}
		}).setMin(1),
		INT_FIXED = new ConsumableEnergy(2).setMin(1),
		INT_FLOAT = new ConsumableEnergy(new Calculation() {
			@Override
			public Number calculate(Number number) {
				return (int)(INT_FIXED.doubleValue()*RED_BAR.getRate());
			}
		}).setMin(1),
		AGI_FIXED = new ConsumableEnergy(5).setMin(1),
		AGI_FLOAT = new ConsumableEnergy(new Calculation() {
			@Override
			public Number calculate(Number number) {
				return (int)(AGI_FIXED.doubleValue()*RED_BAR.getRate());
			}
		}).setMin(1),
		RED_BAR = new ConsumableEnergy().setMin(0).setMax(new Calculation() {
			@Override
			public Number calculate(Number number) {
				return POW_FIXED.doubleValue()*20;
			}
		}).setDefaultToMax(),
		BLUE_BAR = new ConsumableEnergy().setMin(0).setMax(new Calculation() {
			@Override
			public Number calculate(Number number) {
				return INT_FIXED.doubleValue()*20;
			}
		}).setDefaultToMax(),
		GREEN_BAR = new ConsumableEnergy().setMin(0).setMax(new Calculation() {
			@Override
			public Number calculate(Number number) {
				return AGI_FIXED.doubleValue()*20;
			}
		}).setDefaultToMax(),
		RED_REG = new ConsumableEnergy(new Calculation() {
			@Override
			public Number calculate(Number number) {
				return POW_FIXED.doubleValue()*0.02;
			}
		}).setMin(0),
		BLUE_REG = new ConsumableEnergy(new Calculation() {
			@Override
			public Number calculate(Number number) {
				return INT_FIXED.doubleValue()*0.2;
			}
		}).setMin(0),
		GREEN_REG = new ConsumableEnergy(new Calculation() {
			@Override
			public Number calculate(Number number) {
				return AGI_FIXED.doubleValue()*4.0;
			}
		}).setMin(0),
		ENERGY = new ConsumableEnergy().setMin(0).setMax(new Calculation() {
			@Override
			public Number calculate(Number number) {
				return POW_FIXED.doubleValue()*200.0;
			}
		}).setDefaultToMax(),
		SPEED_PPS = new ConsumableEnergy().setMin(0).setDefault(new Calculation() {
			@Override
			public Number calculate(Number number) {
				return AGI_FLOAT.doubleValue()*40.0 + 100;
			}
		}),
		SENSE = new ConsumableEnergy(5).setMin(0).setDefault(5),
		WEIGHT = new ConsumableEnergy(60).setMin(0).setDefault(60),
		TOUGHNESS = new ConsumableEnergy().setMin(0).setMax(new Calculation() {
			@Override
			public Number calculate(Number number) {
				return POW_FIXED.doubleValue()*20.0 + AGI_FIXED.doubleValue()*10.0;
			}
		}).setDefaultToMax(),
		TOUGHNESS_REG = new ConsumableEnergy(new Calculation() {
			@Override
			public Number calculate(Number number) {
				return POW_FIXED.doubleValue()*5.0 + AGI_FIXED.doubleValue()*10.0;
			}
		}),
		CRI = new ConsumableEnergy().setMin(0.00).setDefault(0.05),
		CRI_EFFECT = new ConsumableEnergy().setMin(0.00).setDefault(0.05),
		AVD = new ConsumableEnergy().setDefault(new Calculation() {
			@Override
			public Number calculate(Number number) {
				return AGI_FIXED.doubleValue();
			}
		}),
		REF = new ConsumableEnergy(0.00).setDefault(0.00),
		SUCK = new ConsumableEnergy(0.00).setDefault(0.00);
	public final Status status = new Status(RED_BAR, BLUE_BAR, GREEN_BAR, POW_FLOAT, INT_FLOAT, AGI_FLOAT, ENERGY
			, SPEED_PPS, TOUGHNESS);
	@Override
	public GHQObjectHashMap getKindDataHashMap() {
		return new GHQObjectHashMap()
				.streamPut("POW_FIXED", POW_FIXED)
				.streamPut("POW_FLOAT", POW_FLOAT)
				.streamPut("INT_FIXED", INT_FIXED)
				.streamPut("INT_FLOAT", INT_FLOAT)
				.streamPut("AGI_FIXED", AGI_FIXED)
				.streamPut("AGI_FLOAT", AGI_FLOAT)
				.streamPut("RED_BAR", RED_BAR)
				.streamPut("BLUE_BAR", BLUE_BAR)
				.streamPut("GREEN_BAR", GREEN_BAR)
				.streamPut("RED_REG", RED_REG)
				.streamPut("BLUE_REG", BLUE_REG)
				.streamPut("GREEN_REG", GREEN_REG)
				.streamPut("ENERGY", ENERGY)
				.streamPut("SPEED_PPS", SPEED_PPS)
				.streamPut("SENSE", SENSE)
				.streamPut("WEIGHT", WEIGHT)
				.streamPut("TOUGHNESS", TOUGHNESS)
				.streamPut("TOUGHNESS_REG", TOUGHNESS_REG)
				.streamPut("CRI", CRI)
				.streamPut("AVD", AVD)
				.streamPut("REF", REF)
				.streamPut("SUCK", SUCK)
				;
	}
	public String getCurrentAmmoName() {
		return new Ammo_9mm(0).name();
	}
	//inventory
	public final ItemStorage inventory = def_inventory();
	protected ItemStorage def_inventory() {
		return new ItemStorage(new Storage<ItemData>());
	}
	
	public NAUnit(int charaSize, int hitGroup) {
		physics().setPoint(new Dynam());
		physics().setHitShape(new Circle(this, charaSize));
		physics().setHitGroup(new HitGroup(hitGroup));
	}
	@Override
	public NAUnit respawn(int x, int y) {
		resetOrder();
		status.reset();
		mainSlot.reset();
		subSlot.reset();
		melleSlot.reset();
		point().stop();
		dstPoint.setXY(point().setXY(x, y));
		angle().set(0.0);
		charaOnLand = false;
		inventory.items.clear();
		inventory.add_stack(new Ammo_9mm(32));
		return this;
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
		// status
		////////////
		//relate to energy
		if(!ENERGY.isMin()) {
			//regeneration
			ENERGY.consume(RED_BAR.consume(-RED_REG.doubleValue()*GHQ.getSPF())*1.0*GHQ.getSPF());
			ENERGY.consume(BLUE_BAR.consume(-BLUE_REG.doubleValue()*GHQ.getSPF())*0.2*GHQ.getSPF());
			if(GHQ.isExpired_dynamicSeconds(GREEN_BAR.lastDecreasedFrame(), 1.0))
				ENERGY.consume(GREEN_BAR.consume(-GREEN_REG.doubleValue()*GHQ.getSPF())*0.1*GHQ.getSPF());
			//reduce energy
			if(GHQ.checkSpan_dynamicSeconds(30.0))
				ENERGY.consume(1);
		}else if(GHQ.checkSpan_dynamicSeconds(30.0)) { //reduce hp
			RED_BAR.consume(1);
		}
		//toughness regeneration
		if(GHQ.isExpired_dynamicSeconds(TOUGHNESS.lastDecreasedFrame(), 1.0))
			TOUGHNESS.consume(-TOUGHNESS_REG.doubleValue()*GHQ.getSPF());
		////////////
		// buffs
		////////////
		buffs.addAll(waitingBuffs);
		waitingBuffs.clear();
		buffs.removeAll(waitingDeleteBuffs);
		for(Buff buff : waitingDeleteBuffs)
			buff.removed();
		waitingDeleteBuffs.clear();
		for(Buff buff : buffs)
			buff.idle();
		////////////
		// weapon
		////////////
		mainSlot.idle();
		subSlot.idle();
		melleSlot.idle();
		melleSlot.reloadIfEquipment();
		////////////
		//point
		////////////
		point().moveIfNoObstacles(this);
		point().accelerate_MUL(0.9);
	}
	public int weaponChangeOrder;
	public boolean attackOrder,dodgeOrder,spellOrder;
	@Override
	public void paint() {
		charaPaint.dotPaint_turn(this);
	}
	protected final void paintMagicCircle(DotPaint paintScript) {
		paintScript.dotPaint_turn(point(), (double)GHQ.nowFrame()/35.0);
	}
	public void killed() {
		for(int i = inventory.items.traverseFirst();i != -1;i = inventory.items.traverseNext(i))
			GHQ.stage().addItem(inventory.items.remove(i).drop((int)(point().doubleX() + GHQ.random2(-50,50)), (int)(point().doubleY() + GHQ.random2(-50,50))));
	}
	
	//tool
	public NAUnit getVisibleEnemy() {
		Unit unit = GHQ.stage().getNearstVisibleEnemy(this);
		if(this.point().distance(unit) < 500) //TODO distance of sight
			return (NAUnit)unit;
		return null;
	}
	public boolean isVisible(HasPoint target) {
		return isVisible(target.point());
	}
	public boolean isVisible(Point point) {
		final double DISTANCE = this.point().distance(point);
		return DISTANCE < 150 || DISTANCE < 500 && this.angle().isDeltaSmaller(this.point().angleTo(point), Math.toRadians(60.0)) && this.point().isVisible(point);
	}
	
	// control
	//weapon equip&dequip
	public void equip(Equipment item) {
		if(item instanceof MainSlot) {
			if(mainSlot != null)
				mainSlot.dequipped();
			(mainSlot = item).equipped();
		}else if(item instanceof SubSlot) {
			if(subSlot != null)
				subSlot.dequipped();
			(subSlot = item).equipped();
		}else if(item instanceof MelleSlot) {
			if(melleSlot != null)
				melleSlot.dequipped();
			(melleSlot = item).equipped();
		}
	}
	public void dequip(Equipment item) {
		if(item == mainSlot) {
			mainSlot = Equipment.NULL_EQUIPMENT;
		}else if(item == subSlot) {
			subSlot = Equipment.NULL_EQUIPMENT;
		}else if(item == melleSlot) {
			melleSlot = Equipment.NULL_EQUIPMENT;
		}
		item.dequipped();
	}
	//damage resistance
	public double damageRes(NADamage damage) {
		double res = POW_FIXED.doubleValue()*0.01;
		if(damageResMap.containsKey(damage.materialType()))
			res += damageResMap.get(damage.materialType());
		return res;
	}
	public double addDamageRes(DamageMaterialType materialType, double value) {
		if(damageResMap.containsKey(materialType)) {
			final double NEW_VALUE = damageResMap.get(materialType) + value;
			damageResMap.put(materialType, NEW_VALUE);
			return NEW_VALUE;
		}
		return 0.0;
	}
	// inventory
	public <T extends ItemData>T addItem(T item) {
		inventory.add_stack(item);
		item.setOwner(this);
		return item;
	}
	public final void removeItem(ItemData item) {
		inventory.items.remove(item);
		removedItem(item);
	}
	@Override
	public void removedItem(ItemData item){
		((NAItem)item).removed(inventory);
		if(item instanceof Equipment)
			this.dequip((Equipment)item);
	}
	// move
	protected final void dodge(double targetX, double targetY) {
		point().addSpeed_DA(40, point().angleTo(targetX,targetY));
		charaOnLand = false;
	}
	//stun
	public boolean pullStun() {
		return false;
	}

	// decreases
	@Override
	public void damagedTarget(Unit targetUnit, Bullet bullet) {}
	@Override
	public void damage(Damage damage, Bullet bullet) {
		RED_BAR.clearLastSet();
		final NADamage lastDamage = (NADamage)damage;
		this.lastDamage = lastDamage;
		damage.doDamage(this);
		final double DMG = RED_BAR.lastSetDiff_underZero();
		//tell this unit damaged him
		final GHQObject shooterObject = bullet.shooter();
		if(shooterObject instanceof NAUnit) {
			((NAUnit)shooterObject).damagedTarget(this, bullet);
		}
		//knockback when it was physical damage
		if(lastDamage.materialType() == DamageMaterialType.Phy)
			point().addSpeed_DA(DMG/WEIGHT.doubleValue()*(containsBuff(ToughnessBroke.class) ? 40 : 20), bullet.point().moveAngle());		
		//judge alive
		if(!isAlive())
			killed();
	}
	public final boolean kill(boolean force) {
		RED_BAR.setToMin();
		killed();
		return true;
	}
	public final void removeOneBuff(Buff buff) {
		waitingDeleteBuffs.add(buff);
	}
	public final Buff removeOneBuff(Class<? extends Buff> buffClass) {
		final LinkedList<Buff> BUFF_LIST = removeBuff(buffClass, 1);
		return BUFF_LIST.size() == 0 ? null : BUFF_LIST.getFirst();
	}
	public final LinkedList<Buff> removeAllBuff(Class<? extends Buff> buffClass) {
		return removeBuff(buffClass, Integer.MAX_VALUE);
	}
	public final LinkedList<Buff> removeBuff(Class<? extends Buff> buffClass, int amount) {
		final LinkedList<Buff> removedBuffs = new LinkedList<Buff>();
		if(amount <= 0)
			return removedBuffs;
		for(Buff buff : buffs) {
			if(buff.getClass().equals(buffClass)) {
				removedBuffs.add(buff);
				removeOneBuff(buff);
				if(--amount <= 0)
					return removedBuffs;
			}
		}
		return removedBuffs;
	}
	public final void addBuff(Buff buff) {
		waitingBuffs.add(buff);
	}
	public final boolean containsBuff(Buff buff) {
		return buffs.contains(buff);
	}
	public final boolean containsBuff(Class<? extends Buff> buffClass) {
		for(Buff buff : buffs) {
			if(buff.getClass().equals(buffClass))
				return true;
		}
		return false;
	}
	public final LinkedList<Buff> buffs() {
		return buffs;
	}
	// information
	public Equipment mainEquip() {
		return mainSlot;
	}
	public Equipment subEquip() {
		return subSlot;
	}
	public Equipment melleEquip() {
		return melleSlot;
	}
	public boolean hasMainEquip() {
		return mainSlot != Equipment.NULL_EQUIPMENT;
	}
	public boolean hasSubEquip() {
		return subSlot != Equipment.NULL_EQUIPMENT;
	}
	public boolean hasMelleEquip() {
		return melleSlot != Equipment.NULL_EQUIPMENT;
	}
	public NADamage lastDamage() {
		return lastDamage;
	}
	public int getShield() {
		if(subSlot instanceof ElectronShield) {
			return ((ElectronShield)subSlot).weapon.getMagazineFilledSpace();
		}else
			return 0;
	}
	@Override
	public String name() {
		return GHQ.NOT_NAMED;
	}
	@Override
	public final boolean isAlive() {
		return !RED_BAR.isMin();
	}
}