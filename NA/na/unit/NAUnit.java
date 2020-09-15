package unit;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;

import buff.Buff;
import buff.NABuff;
import calculate.Calculation;
import calculate.ConsumableEnergy;
import calculate.Damage;
import core.GHQ;
import damage.DamageMaterial;
import damage.NADamage;
import engine.NAGame;
import item.NAItem;
import item.NAUsable;
import item.ammo.Ammo;
import item.ammo.AmmoType;
import item.ammo.storage.AmmoStorage;
import item.equipment.Equipment;
import item.equipment.weapon.ElectronShield;
import item.equipment.weapon.NAFirearms;
import item.equipment.weapon.NAWeaponEquipment;
import item.equipment.weapon.reloadRule.ReloadRuleSelecter;
import object.HasWeight;
import paint.ImageFrame;
import paint.dot.DotPaint;
import physics.Angle;
import physics.Dynam;
import physics.HasPoint;
import physics.HitGroup;
import physics.Point;
import physics.direction.Direction8;
import physics.hitShape.Circle;
import physics.hitShape.Square;
import preset.effect.Effect;
import preset.item.ItemData;
import preset.unit.BodyParts;
import preset.unit.BodyPartsType;
import preset.unit.GameInputList;
import preset.unit.InputProcesser;
import preset.unit.Unit;
import preset.unit.UnitAction;
import status.Status;
import storage.TableStorage;
import talent.Talent;
import unit.action.NAAction;
import unit.body.HumanBody;
import weapon.Weapon;

public abstract class NAUnit extends Unit implements Person, HasWeight {
	public static final NAUnit NULL_NAUnit = new NAUnit(0) {
		{
			status.reset();
		}
		@Override
		public UnitGroup unitGroup() {
			return UnitGroup.INVALID;
		}
	};
	//resources
	protected ImageFrame battleStanceIF = 
			ImageFrame.create("picture/mark/battleStance.png");
	protected ImageFrame battleStanceWhenVisibleIF = 
			ImageFrame.create("picture/mark/battleStanceWithEye.png");
	//physics
	public int charaSize;
	public Point.IntPoint dstPoint = new Point.IntPoint();
	
	//status
	private boolean openReloadRule;
	private NADamage lastDamage;
	private final HashMap<DamageMaterial, Double> damageResMap = new HashMap<DamageMaterial, Double>();
	{
		damageResMap.put(DamageMaterial.Heat, 0.0);
		damageResMap.put(DamageMaterial.Cold, 0.0);
		damageResMap.put(DamageMaterial.Phy, 0.0);
		damageResMap.put(DamageMaterial.Poi, 0.0);
	}
	public final ConsumableEnergy
	POW_FIXED = new ConsumableEnergy(5).setMin(1),
	POW_FLOAT = new ConsumableEnergy(new Calculation() {
		@Override
		public Number calculate(Number number) {
			return (int)(POW_FIXED.doubleValue()*RED_BAR.getRate());
		}
	}).setMin(1),
	INT_FIXED = new ConsumableEnergy(5).setMin(1),
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
	WHITE_BAR = new ConsumableEnergy().setMin(0).setMax(new Calculation() {
		@Override
		public Number calculate(Number number) {
			return POW_FIXED.doubleValue()*200.0;
		}
	}).setDefaultToMax(),
	SPEED = new ConsumableEnergy(new Calculation() {
		@Override
		public Number calculate(Number number) {
			return AGI_FLOAT.doubleValue()*40.0 + 100;
		}
	}).setMin(0),
	SENSE = new ConsumableEnergy(5).setMin(0).setDefault(5),
	WEIGHT = new ConsumableEnergy(60).setMin(0),
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
	CRI_EFFECT = new ConsumableEnergy().setMin(0.00).setDefault(2.0),
	AVD = new ConsumableEnergy().setDefault(new Calculation() {
		@Override
		public Number calculate(Number number) {
			return AGI_FIXED.doubleValue();
		}
	}),
	REF = new ConsumableEnergy(0.00).setDefault(0.00),
	SUCK = new ConsumableEnergy(0.00).setDefault(0.00);
	protected final Status status = new Status(
			POW_FIXED, POW_FLOAT, INT_FIXED, INT_FLOAT, AGI_FIXED, AGI_FLOAT,
			RED_BAR, BLUE_BAR, GREEN_BAR, WHITE_BAR,
			RED_REG, BLUE_REG, GREEN_REG,
			SPEED, SENSE, WEIGHT, TOUGHNESS, TOUGHNESS_REG);
	//body&actions
	public enum BodyPartsTypeLibrary implements BodyPartsType {
		HEAD, TRUNK, HAND, LEGS, FOOTS,
		MAIN_WEAPON, SUB_WEAPON, MELLE_WEAPON, SHIELD, EXOSKELETON
	}
	protected HumanBody body = new HumanBody(this);
	protected final InputProcesser actionProcesserByShortcutKeys = new InputProcesser(this) {
		@Override
		public void process() {
			for(int i = 0; i < 10; ++i)
				useQuickSlotByInput(i);
		}
	};
	private final void useQuickSlotByInput(int id) {
		final String STR = "SHORTCUT" + id;
		final int SLOT_ID = id == 0 ? 9 : id - 1;
		final NAUsable itemData = quickSlot.get(SLOT_ID);
		if(itemData != null && gameInputs().hasEvent(STR, !itemData.supportSerialUse())) {
			itemData.use();
			NAGame.quickSlotViewer().lit(SLOT_ID);
		}
	}
	protected final LinkedList<InputProcesser> actionProcesserByBuff = new LinkedList<InputProcesser>();
	protected final InputProcesser actionProcesserByPlayer = def_ActionProcesser();
	protected InputProcesser def_ActionProcesser() { //define process for actions
		return new InputProcesser(this) {
			@Override
			public void process() {
				if(NAGame.controllingUnitActionLocked())
					return;
				//judge rolling
				boolean didLolling = false;
				final double ROLL_STR = SPEED.doubleValue()/10;
				final Direction8 direction = Direction8.getDirectionByWASD(
						gameInputs().hasEvent("WALK_NORTH"),
						gameInputs().hasEvent("WALK_WEST"),
						gameInputs().hasEvent("WALK_SOUTH"),
						gameInputs().hasEvent("WALK_EAST"));
				if(gameInputs().consume("ROLL") && GREEN_BAR.doubleValue() > 25) {
					body.rolling.setRolling(ROLL_STR, direction);
				}else if(gameInputs().hasEvent("SPRINT") && !GREEN_BAR.isMin()) {
					body.directionDash.setDirection(direction, 2*GHQ.getSPF()*SPEED.doubleValue());
				}else
					body.directionWalk.setDirection(direction, GHQ.getSPF()*SPEED.doubleValue());
				//reduce green bar when rolling
				if(didLolling) {
					//GREEN_BAR.consume(25.0);
					//suspend any action during rolling
					actionProcesserByBuff.add(new InputProcesser(UNIT) {
						private final int INITIAL_FRAME = GHQ.nowFrame();
						@Override
						public void process() {
							if(GHQ.passedFrame(INITIAL_FRAME) > 15) {
								actionProcesserByBuff.remove(this);
								return;
							}
							gameInputs().consume("FIRE");
							gameInputs().consume("RELOAD");
						}
					});
				}
				//weapon action
				if(currentEquipment() != null) { //has weapon
					//attack
					if(isBattleStance && gameInputs().hasEvent("FIRE", !currentEquipment().supportSerialUse())) {
						currentEquipment().use();
						setMiniTalking("Fire.");
					}
					//reload
					if(gameInputs().hasEvent("RELOAD")) { //pressed R key
						if(!openReloadRule) {
							openReloadRule = true;
						}
						final int rolling = NAGame.pullMouseWheelRotation();
						if(rolling != 0) {
							reloadRuleSelecter.roll(rolling);
						}
					} else if(openReloadRule) { //released R key
						openReloadRule = false;
						if(currentEquipment() != null && currentEquipment() instanceof NAFirearms) {
							final NAFirearms firearm = (NAFirearms)currentEquipment();
							firearm.setReloadRule(reloadRuleSelecter.getCurrentSelection());
							firearm.reloadWeapon();
							setMiniTalking("Reloading.");
						}
					}
				}else {
					//attack
					if(isBattleStance && gameInputs().hasEvent("FIRE")) {
						body.punch.setPunch();
						setMiniTalking("Punch.");
					}
				}
				if(gameInputs().consume("LAST_WEAPON")) {
					body().armLast();
					setBattleStance(true);
				}
				final int rotation = NAGame.pullMouseWheelRotation();
				if(rotation > 0) {
					body().changeToNextWeapon();
				} else if(rotation < 0){
					body().changeToPrevWeapon();
				}
				//itemPick & talk
				boolean interactHappened = gameInputs().consume("INTERACT");
				final ItemData item = GHQ.stage().items.forShapeIntersects(UNIT);
//				for(Vegetation structure : GHQ.stage().vegetations) {
//					if(structure instanceof DownStair) {
//						if(point().inRangeXY(structure.point(), (width() + structure.width())/2, (height() + structure.height())/2)) {
//							GHQ.getG2D(Color.WHITE);
//							GHQ.drawStringGHQ("\"E:\" go down", structure.point().intX(), structure.point().intY() - 20);
//							if(interactHappened) {
//								NAGame.downFloor();
//								interactHappened = false;
//							}
//							break;
//						}
//					}
//				}
				if(interactHappened) {
					interact: {
						final NAUnit npc = (NAUnit)GHQ.stage().units.getClosestVisible(UNIT);
						if(npc != null && npc.point().inRange(UNIT.point(), 240)) {
							if(npc.interact((NAUnit)UNIT)) {
								interactHappened = false;
								break interact;
							}
						}
						if(item != null) {
							((NAItem)item).interact(NAUnit.this);
							interactHappened = false;
							break interact;
						}
					}
				}
				if(gameInputs().consume(NAGame.GameInputEnum.SWITCH_BATTLE_STANCE.name())) {
					setBattleStance(!isBattleStance);
				}
			}
		};
	}
	//personal information
	protected String personalName;
	public ImageFrame personalIcon;
	public DotPaint charaPaint = new DotPaint() {
		@Override
		public void dotPaint(int x, int y) {
			
		}
		@Override
		public int width() {
			return 0;
		}
		@Override
		public int height() {
			return 0;
		}
	};
	
	//battle
	protected NAUnit targetUnit;
	protected int targetFoundFrame;
	protected boolean isBattleStance;
	protected double suspiciousAngle;
	protected int lastDetectedFrame;
	protected boolean invisibled;
	
	protected final ReloadRuleSelecter reloadRuleSelecter = new ReloadRuleSelecter(this);

	//favor
	protected final int[] personalFavor = new int[UnitGroup.GROUP_AMOUNT];
	//buff
	protected LinkedList<NABuff> buffs = new LinkedList<NABuff>(),
			waitingBuffs = new LinkedList<NABuff>(),
			waitingDeleteBuffs = new LinkedList<NABuff>();
	//talent
	protected LinkedList<Talent> talents = new LinkedList<Talent>();
	//inventory
	public TableStorage<NAUsable> quickSlot = new TableStorage<NAUsable>(10, 1, NAUsable.NULL_NA_USABLE);
	public TableStorage<ItemData> inventory = new TableStorage<ItemData>(5, 3, ItemData.BLANK_ITEM);
	public AmmoStorage ammoStorage = new AmmoStorage(AmmoType.values());
	
	public NAUnit(int charaSize) {
		physics().setPoint(new Dynam());
		physics().setHitShape(new Square(this, charaSize));
		physics().setHitRule(HitGroup.HIT_ALL);
	}
	@Override
	public NAUnit respawn(int x, int y) {
		status.reset();
		//remain previous weapon.
		for(ItemData item : body.equipments())
			((Equipment)item).reset();
		point().stop();
		lastDetectedFrame = 0;
		openReloadRule = false;
		dstPoint.setXY(point().setXY(x, y));
		angle().set(0.0);
		inventory.clear();
		return this;
	}
	@Override
	public void idle() {
		super.idle();
		////////////
		// status
		////////////
		//relate to energy
		if(!WHITE_BAR.isMin()) {
			//regeneration
			WHITE_BAR.consume(-RED_BAR.consume(-RED_REG.doubleValue()*GHQ.getSPF())*1.0*GHQ.getSPF());
			WHITE_BAR.consume(-BLUE_BAR.consume(-BLUE_REG.doubleValue()*GHQ.getSPF())*0.2*GHQ.getSPF());
			if(GHQ.isExpired_dynamicSeconds(GREEN_BAR.lastDecreasedFrame(), 1.0))
				WHITE_BAR.consume(-GREEN_BAR.consume(-GREEN_REG.doubleValue()*GHQ.getSPF())*0.1);
			//reduce energy
			if(GHQ.checkSpan_dynamicSeconds(30.0))
				WHITE_BAR.consume(1);
		}else if(GHQ.checkSpan_dynamicSeconds(30.0)) { //reduce hp
			this.damage(DamageMaterial.Rea.makeDamage(1));
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
		// item in inventory (includes weapon equipped to body parts)
		////////////
		for(ItemData item : inventory)
			item.idle();
		////////////
		//input
		////////////
		if(isControllingUnit()) {
			actionProcesserByShortcutKeys.process();
			for(InputProcesser processer : actionProcesserByBuff)
				processer.process();
			actionProcesserByPlayer.process();
		}
		////////////
		//point
		////////////
		point().moveIfNoObstacles(this);
		point().mulSpeed(0.9);
		////////////
		//sight & battle
		////////////
		if(!isControllingUnit()) {
			updateTargetUnit();
			attack();
		}
		////////////
		//aim
		////////////
		if(isControllingUnit()) {
			fixAimAngle: {
				for(UnitAction action : body().doingActions()) {
					if(((NAAction)action).needFixAimAngle()) {
						break fixAimAngle;
					}
				}
				angle().set(point().angleToMouse());
			}
		}
		////////////
		// body
		////////////
		//movement
		//must proceed last, or later functions cannot get correct coordinate.
		body.idle(true, false);
	}
	protected void attack() {
		final NAWeaponEquipment equipment = currentEquipment();
		if(targetUnit != null) {
			final double angleDiff = angle().spinTo_Suddenly(point().angleTo(targetUnit), 10);
			final double distance = point().distance(targetUnit);
			if(equipment == null) { //bear hand
				if(angleDiff < 0.30 && distance < 50)
					body().punch.setPunch();
			} else { //armed
				if(equipment.weapon().magazine() == 0) {
					equipment.weapon().startReloadIfNotDoing();
				} else if(equipment.effectiveTarget(distance, angleDiff))
					equipment.use();
			}
		} else {
			if(equipment != null)
				equipment.reloadWeapon();
			if(isBattleStance) {
				if(GHQ.nowFrame() % 80 == 0) {
					suspiciousAngle = Angle.random();
				}
				angle().spinTo_Suddenly(suspiciousAngle, 10);
			} else
				angle().spinTo_Suddenly(point().moveAngle(), 10);
		}
	}
	@Override
	public void paint() {
		////////////
		//body and weapon
		////////////
		if(!isControllingUnit()) {
			if(NAGame.controllingUnit().isVisible(this)) {
				lastDetectedFrame = GHQ.nowFrame();
				body.paint();
				if(this.getShield() > 0) {
					GHQ.getG2D(Color.CYAN, GHQ.stroke5).drawLine(cx() - 25, cy() + 25, cx() - 25 + 50*(getShield()/getShieldSize()), cy() + 25);
				} else {
					GHQ.getG2D(Color.RED, GHQ.stroke5).drawLine(cx() - 25, cy() + 25, cx() - 25 + (int)(50*RED_BAR.getRate()), cy() + 25);
				}
			} else {
				final double passedTime = GHQ.passedFrame(lastDetectedFrame)*GHQ.getSPF();
				final double KEEP_TIME = 3.0;
				if(passedTime < KEEP_TIME) {
					GHQ.setImageAlpha((float)(1F - passedTime/KEEP_TIME));
					body.paint();
					GHQ.setImageAlpha();
				}
			}
		}else {
			body.paint();
			GHQ.getG2D(Color.CYAN, GHQ.stroke5).drawLine(cx() - 25, cy() + 25, cx() + 25, cy() + 25);
		}

		super.drawBoundingBox(Color.RED, GHQ.stroke1);
	}
	protected final void paintMagicCircle(DotPaint paintScript) {
		paintScript.dotPaint_turn(point(), (double)GHQ.nowFrame()/35.0);
	}
	public void killed() {
		for(int i = inventory.nextNonspaceIndex(); i != -1; i = inventory.nextNonspaceIndex(i)) {
			GHQ.stage().addItem(inventory.remove(i).drop((int)(point().doubleX() + GHQ.random2(-50,50)), (int)(point().doubleY() + GHQ.random2(-50,50))));
		}
	}
	
	//tool
	public void updateTargetUnit() {
		NAUnit leastFriendlyUnit = null;
		int leastFavor = 0, favor;
		double leastFriendlyUnitDistance = Double.MAX_VALUE, distance;
		for(Unit unit : GHQ.stage().getVisibleUnit(this)) {
			if(!isVisible(unit))
				continue;
			favor = favorTo((NAUnit)unit);
			distance = point().distance(leastFriendlyUnit);
			if(favor < leastFavor || favor == leastFavor && distance < leastFriendlyUnitDistance) {
				leastFriendlyUnit = (NAUnit)unit;
				leastFavor = favor;
				leastFriendlyUnitDistance = distance;
			}
		}
		if(isHostile(leastFavor)) { //hostile
			//record this target and attack it
			targetUnit = leastFriendlyUnit;
			targetFoundFrame = GHQ.nowFrame();
			setBattleStance(true);
			body().rearm();
			//say something
			this.setMiniTalking("I found you.");
		} else {
			if(targetUnit != null && GHQ.isExpired_frame(targetFoundFrame, (int)(GHQ.getFPS()*5)))
				targetUnit = null;
			if(isBattleStance && GHQ.isExpired_frame(targetFoundFrame, (int)(GHQ.getFPS()*15))) {
				setBattleStance(false);
				body().toNaturalForm();
			}
		}
	}
	public boolean isHostile(int favor) {
		return favor <= -30;
	}
	public boolean isHostile(NAUnit target) {
		return isHostile(favorTo(target));
	}
	public boolean isHostileToControllingUnit() {
		return !isControllingUnit() && isHostile(NAGame.controllingUnit());
	}
	public boolean isVisible(HasPoint target) {
		return isAware(target) || !(target instanceof NAUnit && ((NAUnit)target).isInvisibled()) && inWidthOfFieldView_degree(target, 60) && point().isVisible(target, 800);
	}
	public boolean isVisible(Point point) {
		return isAware(point) || inWidthOfFieldView_degree(point, 60) && point().isVisible(point, 800);
	}
	public boolean isAware(HasPoint target) {
		return isAware(target.point());
	}
	public boolean isAware(Point point) {
		return point().distance(point) < SENSE.intValue()*15;
	}
	public boolean isVisibleByControllingUnit() {
		return this.isControllingUnit() || NAGame.controllingUnit().isVisible(this);
	}
	public boolean isAwareByControllingUnit() {
		return this.isControllingUnit() || NAGame.controllingUnit().isAware(this);
	}
	//control
	//invisible
	public void setInvisible(boolean b) {
		if(b) {
			if(invisibled)
				return;
			for(Unit unit : GHQ.stage().units) {
				if(isHostile((NAUnit)unit) && this.isVisible(unit)) {
					//cannot activate invisible mode when a hostile unit is watching him.
					return;
				}
			}
			invisibled = true;
		}else
			invisibled = false;
	}
	//talk
	/**
	 * Start chat or open its inventory.
	 * @param unit
	 * @return false if interaction rejected
	 */
	public boolean interact(NAUnit unit) {
		return false;
	}
	public void setMiniTalking(String text) {
		GHQ.stage().addEffect(new Effect(this) {
			{
				this.limitFrame = 30;
				point().stop();
				point().setXY(shooter.point().intX(), shooter.point().intY() - 50);
			}
			@Override
			public void paint() {
				super.paint();
				GHQ.getG2D(new Color(0, 0, 0, GHQ.getFadingAlpha(initialFrame, limitFrame))).setFont(GHQ.commentFont);
				GHQ.drawStringGHQ(text, point().intX(), point().intY());
			}
		});
	}
	//battle stance
	public void setBattleStance(boolean b) {
		if(isBattleStance == b)
			return;
		isBattleStance = b;
//		if(b) {
//			body().rearm();
//			if(this.isControllingUnit())
//				GHQ.showCursor(false);
//		}else {
//			body().toNaturalForm();
//			if(this.isControllingUnit())
//				GHQ.showCursor(true);
//		}
	}
	//weapon equip & dequip
	public void equip(Equipment item) {
		body.equip(item);
		if(!item.hasOwner())
			item.setOwner(this);
		item.equipped();
	}
	public void dequip(Equipment item) {
		body.dequip(item);
		item.dequipped();
	}
	//damage resistance
	public double damageRes(DamageMaterial material) {
		double res = POW_FIXED.doubleValue()*0.01;
		if(damageResMap.containsKey(material))
			res += damageResMap.get(material);
		return res;
	}
	public double addDamageRes(DamageMaterial materialType, double value) {
		if(damageResMap.containsKey(materialType)) {
			final double NEW_VALUE = damageResMap.get(materialType) + value;
			damageResMap.put(materialType, NEW_VALUE);
			return NEW_VALUE;
		}
		return 0.0;
	}
	// inventory
	public <T extends ItemData>T addItemToStorage(T item) {
		return addItemToStorage(item, true);
	}
	public <T extends ItemData>T addItemToStorage(T item, boolean doStack) {
		item.pickup(this);
		if(item instanceof Ammo) {
			ammoStorage.add((Ammo)item);
		} else {
			if(doStack)
				ItemData.addInInventory(inventory, item);
			else
				inventory.add(item);
			if(quickSlot().hasSpace() && item instanceof NAUsable)
				this.quickSlot().add((NAUsable)item);
		}
		return item;
	}
	public final void removeItem(ItemData item) {
		if(item instanceof Ammo) {
			ammoStorage.removeStackable((Ammo)item);
		} else {
			inventory.remove(item);
			System.out.println("removed: " + item.getClass().getName());
		}
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
	}
	//stun
	public boolean pullStun() {
		return false;
	}

	// decreases
	@Override
	public void damagedTarget(Unit targetUnit, Damage damage) {}
	public final boolean kill(boolean force) {
		RED_BAR.setToMin();
		killed();
		return true;
	}
	public final void removeOneBuff(NABuff buff) {
		waitingDeleteBuffs.add(buff);
	}
	public final Buff removeOneBuff(Class<? extends NABuff> buffClass) {
		final LinkedList<Buff> BUFF_LIST = removeBuff(buffClass, 1);
		return BUFF_LIST.size() == 0 ? null : BUFF_LIST.getFirst();
	}
	public final LinkedList<Buff> removeAllBuff(Class<? extends NABuff> buffClass) {
		return removeBuff(buffClass, Integer.MAX_VALUE);
	}
	public final LinkedList<Buff> removeBuff(Class<? extends NABuff> buffClass, int amount) {
		final LinkedList<Buff> removedBuffs = new LinkedList<Buff>();
		if(amount <= 0)
			return removedBuffs;
		for(NABuff buff : buffs) {
			if(buff.getClass().equals(buffClass)) {
				removedBuffs.add(buff);
				removeOneBuff(buff);
				if(--amount <= 0)
					return removedBuffs;
			}
		}
		return removedBuffs;
	}
	public final void addBuff(NABuff buff) {
		waitingBuffs.add(buff);
	}
	public final boolean containsBuff(NABuff buff) {
		return buffs.contains(buff);
	}
	public final boolean containsBuff(Class<? extends NABuff> buffClass) {
		return getBuff(buffClass) != null;
	}
	public final NABuff getBuff(Class<? extends NABuff> buffClass) {
		for(NABuff buff : buffs) {
			if(buff.getClass().equals(buffClass))
				return buff;
		}
		return null;
	}
	public final LinkedList<NABuff> buffs() {
		return buffs;
	}
	public final void addTalent(Talent talent) {
		talents.add(talent);
	}
	public final LinkedList<Talent> talents() {
		return talents;
	}
	public void addFavor(UnitGroup targetGroup, int value) {
		personalFavor[targetGroup.ordinal()] += value;
	}
	public void setPersonalName(String name) {
		personalName = name;
	}
	public void setPersonalIcon(ImageFrame dotPaint) {
		personalIcon = dotPaint;
	}
	// information
	public final boolean isControllingUnit() {
		return this == NAGame.controllingUnit();
	}
	//battle
	public boolean isBattleStance() {
		return isBattleStance;
	}
	// input action
	public static GameInputList gameInputs() {
		return NAGame.gameInputs();
	}
	public InputProcesser actionProcesserByShortcutKeys() {
		return actionProcesserByShortcutKeys;
	}
	public LinkedList<InputProcesser> actionProcesserByBuff() {
		return actionProcesserByBuff;
	}
	public InputProcesser actionProcesserByPlayer() {
		return actionProcesserByPlayer;
	}
	// storage
	public TableStorage<ItemData> inventory() {
		return inventory;
	}
	public TableStorage<NAUsable> quickSlot() {
		return quickSlot;
	}
	// personal information
	@Override
	public String personalName() {
		return personalName;
	}
	@Override
	public ImageFrame personalIcon() {
		return personalIcon;
	}
	public abstract UnitGroup unitGroup();
	public int favorTo(NAUnit unit) {
		return unitGroup().groupFavorTo(unit) + personalFavor[unit.unitGroup().ordinal()];
	}
	protected BodyParts currentEquipmentSlot() {
		return body().currentEquipSlot();
	}
	public NAWeaponEquipment currentEquipment() {
		return (NAWeaponEquipment)body().currentEquipment();
	}
	public Weapon currentWeapon() {
		final NAWeaponEquipment equip = currentEquipment();
		return equip == null ? null : equip.weapon();
	}
	public HumanBody body() {
		return body;
	}
	public NADamage lastDamage() {
		return lastDamage;
	}
	public int getShield() {
		if(body().shield() instanceof ElectronShield) {
			return ((ElectronShield)body().shield()).getShieldValue();
		}else
			return 0;
	}
	public int getShieldSize() {
		if(body().shield() instanceof ElectronShield) {
			return ((ElectronShield)body().shield()).getShieldSize();
		}else
			return 0;
	}
	@Override
	public DotPaint getDotPaint() {
		return charaPaint;
	}
	@Override
	public String name() {
		return GHQ.NOT_NAMED;
	}
	@Override
	public final boolean isAlive() {
		return !RED_BAR.isMin();
	}
	public NAUnit targetUnit() {
		return targetUnit;
	}
	public boolean isInvisibled() {
		return invisibled;
	}
	public boolean hasTargetUnit() {
		return targetUnit != null;
	}
	@Override
	public double weight() {
		return WEIGHT.doubleValue();
	}
	public boolean openReloadRule() {
		return openReloadRule;
	}
	public NAItem changeToItem() {
		if(!hasDeleteClaimFromStage()) {
			claimDeleteFromStage();
			return itemUnit;
		}
		return null;
	}
	public NAUnit changeToUnit() {
		if(!itemUnit.hasDeleteClaimFromStage()) {
			itemUnit.claimDeleteFromStage();
			return this;
		}
		return null;
	}
	protected NAItem itemUnit = def_itemUnit();
	protected NAItem def_itemUnit() {
		return new NAItem(charaPaint) {
			@Override
			public double weight() {
				return NAUnit.this.weight();
			}
			@Override
			public NAUnit substantialize(int x, int y) {
				if(hasDeleteClaimFromStage()) {
					claimDeleteFromStage();
					return NAUnit.this;
				}
				return null;
			}
		};
	}
	public NAItem itemUnit() {
		return itemUnit;
	}
	public void setLastDamage(NADamage damage) {
		lastDamage = damage;
	}
	public ReloadRuleSelecter reloadRuleSelecter() {
		return reloadRuleSelecter;
	}
}