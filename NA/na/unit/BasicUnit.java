package unit;

import java.util.LinkedList;

import buff.Buff;
import buff.ToughnessBroke;
import calculate.ConsumableEnergy;
import calculate.Setter;
import core.GHQ;
import effect.EffectLibrary;
import hitShape.Circle;
import item.BSItem;
import item.ItemData;
import item.ammo.Ammo_9mm;
import item.weapon.ElectronShield;
import item.weapon.MainSlot;
import item.weapon.SubSlot;
import paint.dot.DotPaint;
import paint.rect.RectPaint;
import physics.Dynam;
import physics.Point;
import status.Status;
import storage.ItemStorage;
import storage.Storage;
import unit.Unit;

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
	public BSItem
		mainSlot = BSItem.BLANK_ITEM,
		subSlot = BSItem.BLANK_ITEM,
		meleeSlot = BSItem.BLANK_ITEM,
		spellWeapon = BSItem.BLANK_ITEM;

	// GUI
	public RectPaint iconPaint;

	// Resource
	// Images
	public DotPaint charaPaint = DotPaint.BLANK_SCRIPT;
	//special
	public int favorDegree;
	//buff
	public LinkedList<Buff> buffs = new LinkedList<Buff>();
	//status
	@SuppressWarnings("serial")
	public final ConsumableEnergy
		POW_FIXED = new ConsumableEnergy(5).setMin(1),
		POW_FLOAT = new ConsumableEnergy(new Setter() {
			private static final long serialVersionUID = 1L;
			public Number set() {
				return (int)(POW_FIXED.doubleValue()*RED_BAR.getRate());
			}
		}).setMin(1),
		INT_FIXED = new ConsumableEnergy(5).setMin(1),
		INT_FLOAT = new ConsumableEnergy(new Setter() {
			private static final long serialVersionUID = 1L;
			public Number set() {
				return (int)(INT_FIXED.doubleValue()*RED_BAR.getRate());
			}
		}).setMin(1),
		AGI_FIXED = new ConsumableEnergy(5).setMin(1),
		AGI_FLOAT = new ConsumableEnergy(new Setter() {
			private static final long serialVersionUID = 1L;
			public Number set() {
				return (int)(AGI_FIXED.doubleValue()*RED_BAR.getRate());
			}
		}).setMin(1),
		RED_BAR = new ConsumableEnergy().setMin(0).setMax(new Setter() {
			private static final long serialVersionUID = 1702800523004611744L;
			@Override
			public Number set() {
				return POW_FIXED.doubleValue()*20;
			}
		}).setDefaultToMax(),
		BLUE_BAR = new ConsumableEnergy().setMin(0).setMax(new Setter() {
			private static final long serialVersionUID = 1702800523004611744L;
			@Override
			public Number set() {
				return INT_FIXED.doubleValue()*20;
			}
		}).setDefaultToMax(),
		GREEN_BAR = new ConsumableEnergy().setMin(0).setMax(new Setter() {
			private static final long serialVersionUID = 1702800523004611744L;
			@Override
			public Number set() {
				return AGI_FIXED.doubleValue()*20;
			}
		}).setDefaultToMax(),
		RED_REG = new ConsumableEnergy(new Setter() {
			private static final long serialVersionUID = 590642405739512738L;
			@Override
			public Number set() {
				return POW_FIXED.doubleValue()*0.02;
			}
		}).setMin(0),
		BLUE_REG = new ConsumableEnergy(new Setter() {
			private static final long serialVersionUID = 590642405739512738L;
			@Override
			public Number set() {
				return INT_FIXED.doubleValue()*0.2;
			}
		}).setMin(0),
		GREEN_REG = new ConsumableEnergy(new Setter() {
			private static final long serialVersionUID = 590642405739512738L;
			@Override
			public Number set() {
				return AGI_FIXED.doubleValue()*4.0;
			}
		}).setMin(0),
		ENERGY = new ConsumableEnergy().setMin(0).setMax(new Setter() {
			private static final long serialVersionUID = 590642405739512738L;
			@Override
			public Number set() {
				return POW_FIXED.doubleValue()*200.0;
			}
		}).setDefaultToMax(),
		SPEED_PPS = new ConsumableEnergy().setMin(0).setDefault(new Setter() {
			private static final long serialVersionUID = 590642405739512738L;
			@Override
			public Number set() {
				return AGI_FLOAT.doubleValue()*40.0 + 100;
			}
		}),
		SENSE = new ConsumableEnergy(5).setMin(0).setDefault(5),
		WEIGHT = new ConsumableEnergy(60).setMin(0).setDefault(60),
		TOUGHNESS = new ConsumableEnergy().setMin(0).setMax(new Setter() {
			@Override
			public Number set() {
				return POW_FIXED.doubleValue()*20.0 + AGI_FIXED.doubleValue()*10.0;
			}
		}).setDefaultToMax(),
		TOUGHNESS_REG = new ConsumableEnergy(new Setter() {
			@Override
			public Number set() {
				return POW_FIXED.doubleValue()*5.0 + AGI_FIXED.doubleValue()*10.0;
			}
		}),
		CRI = new ConsumableEnergy(0.05).setMin(0.00).setDefault(0.05),
		AVD = new ConsumableEnergy(0.05).setMin(0.00).setDefault(0.05),
		REF = new ConsumableEnergy(0.00).setDefault(0.00),
		SUCK = new ConsumableEnergy(0.00).setDefault(0.00);
	public final Status status = new Status(RED_BAR, BLUE_BAR, GREEN_BAR, POW_FLOAT, INT_FLOAT, AGI_FLOAT, ENERGY
			, SPEED_PPS, TOUGHNESS);
	public String getCurrentAmmoName() {
		return new Ammo_9mm(0).getName();
	}
	//inventory
	public final ItemStorage inventory = def_inventory();
	protected ItemStorage def_inventory() {
		return new ItemStorage(new Storage<ItemData>());
	}
	
	public BasicUnit(int charaSize, int initialGroup) {
		super(new Circle(new Dynam(), charaSize), initialGroup);
	}
	@Override
	public BasicUnit respawn(int x, int y) {
		resetOrder();
		status.reset();
		mainSlot.reset();
		subSlot.reset();
		meleeSlot.reset();
		dynam.clear();
		dstPoint.setXY(dynam.setXY(x, y));
		baseAngle.set(0.0);
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
			ENERGY.consume(RED_BAR.consume_getEffect(-RED_REG.doubleValue()*GHQ.getSPF()).doubleValue()*1.0*GHQ.getSPF());
			ENERGY.consume(BLUE_BAR.consume_getEffect(-BLUE_REG.doubleValue()*GHQ.getSPF()).doubleValue()*0.2*GHQ.getSPF());
			if(GHQ.isExpired_dynamicSeconds(GREEN_BAR.lastDecreasedFrame(), 1.0))
				ENERGY.consume(GREEN_BAR.consume_getEffect(-GREEN_REG.doubleValue()*GHQ.getSPF()).doubleValue()*0.1*GHQ.getSPF());
			//reduce energy
			if(GHQ.checkSpan_dynamicSeconds(30.0))
				ENERGY.consume(1);
		}else if(GHQ.checkSpan_dynamicSeconds(30.0)) { //reduce hp
			RED_BAR.consume(1);
		}
		//relate to toughness
		if(GHQ.isExpired_dynamicSeconds(TOUGHNESS.lastDecreasedFrame(), 1.0))
			TOUGHNESS.consume(-TOUGHNESS_REG.doubleValue()*GHQ.getSPF());
		////////////
		// buffs
		////////////
		for(Buff buff : buffs)
			buff.idle();
		////////////
		// weapon
		////////////
		mainSlot.idle();
		subSlot.idle();
		meleeSlot.idle();
		meleeSlot.reloadIfEquipment();
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
			GHQ.stage().addItem(inventory.items.remove(i).drop((int)(dynam.doubleX() + GHQ.random2(-50,50)), (int)(dynam.doubleY() + GHQ.random2(-50,50))));
	}
	
	// control
	// inventory
	public <T extends ItemData>T addItem(T item) {
		inventory.add_stack(item);
		item.setOwner(this);
		return item;
	}
	public void removedItem(ItemData item){
		if(item instanceof MainSlot)
			mainSlot = BSItem.BLANK_ITEM;
		else if(item instanceof SubSlot)
			subSlot = BSItem.BLANK_ITEM;
		inventory.items.remove(item);
		item.setOwner(null);
	}
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
	public int damage_amount(int amount, Dynam harmerDynam) {
		final int REAL_DMG = damage_amount(amount);
		dynam.addSpeed_DA(-REAL_DMG/WEIGHT.doubleValue()*(containsBuff(ToughnessBroke.class) ? 40 : 20), harmerDynam.moveAngle());
		return REAL_DMG;
	}
	@Override
	public int damage_amount(int amount) {
		//reduce toughness
		TOUGHNESS.consume(amount);
		//gain toughnessBroke when zero
		if(TOUGHNESS.isMin() && !containsBuff(ToughnessBroke.class)) {
			addBuff(new ToughnessBroke(this));
		}
		//try shield
		if(subSlot instanceof ElectronShield) {
			if(((ElectronShield)subSlot).weapon.unload(amount) == amount) //shield success
				((ElectronShield)subSlot).damaged();
			else
				removedItem(subSlot); //destroy shield
			return -amount;
		}else {
			final int DMG = RED_BAR.consume_getEffect(amount).intValue();
			//show dmg
			GHQ.stage().addEffect(new EffectLibrary.DamageNumberEF(this, DMG));
			//judge alive
			if(!isAlive())
				killed();
			return DMG;
		}
	}
	public final boolean kill(boolean force) {
		RED_BAR.setToMin();
		killed();
		return true;
	}
	public final void removeBuff(Buff buff) {
		buffs.remove(buff);
	}
	public final LinkedList<Buff> removeBuff(String className, int amount) {
		final LinkedList<Buff> removedBuffs = new LinkedList<Buff>();
		if(amount <= 0)
			return removedBuffs;
		for(Buff buff : buffs) {
			if(buff.getClass().getName().equals(className)) {
				removedBuffs.add(buff);
				buffs.remove(buff);
				if(--amount <= 0)
					return removedBuffs;
			}
		}
		return removedBuffs;
	}
	public final void addBuff(Buff buff) {
		buffs.add(buff);
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

	// information
	public int getShield() {
		if(subSlot instanceof ElectronShield) {
			return ((ElectronShield)subSlot).weapon.getMagazineFilledSpace();
		}else
			return 0;
	}
	@Override
	public String getName() {
		return GHQ.NOT_NAMED;
	}
	@Override
	public final boolean isAlive() {
		return !RED_BAR.isMin();
	}
}