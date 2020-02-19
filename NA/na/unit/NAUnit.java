package unit;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;

import buff.Buff;
import buff.NABuff;
import buff.ToughnessBroke;
import bullet.Bullet;
import calculate.Calculation;
import calculate.ConsumableEnergy;
import calculate.Damage;
import core.GHQ;
import core.GHQObject;
import damage.DamageMaterialType;
import damage.NADamage;
import effect.Effect;
import engine.NAGame;
import gui.stageEditor.GHQObjectHashMap;
import item.ItemData;
import item.NAItem;
import item.NAUsable;
import item.ammo.Ammo_9mm;
import paint.ImageFrame;
import paint.dot.DotPaint;
import physics.Angle;
import physics.Dynam;
import physics.HasPoint;
import physics.HitGroup;
import physics.Point;
import physics.Direction.Direction8;
import physics.hitShape.Circle;
import stage.Gridder;
import status.Status;
import storage.ItemStorage;
import storage.TableStorage;
import talent.Talent;
import unit.Unit;
import unit.body.HumanBody;
import weapon.ElectronShield;
import weapon.Equipment;

public abstract class NAUnit extends Unit implements Person {
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
	
	//body&actions
	public enum BodyPartsTypeLibrary implements BodyPartsType {
		HEAD, TRUNK, HAND, LEGS, FOOTS,
		MAIN_WEAPON, SUB_WEAPON, MELLE_WEAPON, SHIELD, EXOSKELETON
	}
	protected HumanBody body = new HumanBody(this);
	protected BodyParts currentWeaponBodyParts = body().mainEquipSlot();
	protected BodyParts lastWeaponBodyParts = currentWeaponBodyParts;
	protected final InputProcesser actionProcesserByShortcutKeys = new InputProcesser(this) {
		@Override
		public void process() {
			for(int i = 0; i < 10; ++i)
				useQuickSlotByInput(i);
		}
	};
	private final void useQuickSlotByInput(int id) {
		final String STR = "SHORTCUT" + id;
		if(gameInputs().consumeIgnoreConsume(STR)) { 
			final int SLOT_ID = id == 0 ? 9 : id - 1;
			final NAUsable itemData = quickSlot.get(SLOT_ID);
			if(itemData != null) {
				itemData.use(gameInputs().hasSecondEvent(STR));
				NAGame.quickSlotViewer().lit(SLOT_ID);
			}
		}
	}
	protected final LinkedList<InputProcesser> actionProcesserByBuff = new LinkedList<InputProcesser>();
	protected final InputProcesser actionProcesserByPlayer = def_ActionProcesser();
	protected InputProcesser def_ActionProcesser() { //define process for actions
		return new InputProcesser(this) {
			@Override
			public void process() {
				//judge rolling
				boolean didLolling = false;
				final double ROLL_STR = SPEED_PPS.doubleValue()/10;
				final Direction8 direction = Direction8.getDirectionByWASD(
						gameInputs().hasEvent("WALK_NORTH"),
						gameInputs().hasEvent("WALK_WEST"),
						gameInputs().hasEvent("WALK_SOUTH"),
						gameInputs().hasEvent("WALK_EAST"));
				if(gameInputs().consume("ROLL") && GREEN_BAR.intValue() > 25) {
					body.rolling.setRolling(ROLL_STR, direction);
				}else if(gameInputs().hasEvent("SPRINT") && !GREEN_BAR.isMin()) {
					body.directionDash.setDirection(direction, 2*GHQ.mulSPF(SPEED_PPS.doubleValue()));
				}else
					body.directionWalk.setDirection(direction, GHQ.mulSPF(SPEED_PPS.doubleValue()));
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
					if(gameInputs().hasEvent("FIRE")) {
						currentEquipment().use(true);
						setMiniTalking("Fire.");
					}
					//reload
					if(gameInputs().hasEvent("RELOAD")) {
						if(currentEquipment() != null)
							currentEquipment().reloadIfEquipment();
						setMiniTalking("Reloading.");
					}
				}else {
					//attack
					if(gameInputs().hasEvent("FIRE")) {
						body.punch.setPunch();
						setMiniTalking("Punch.");
					}
				}
				if(gameInputs().consume("LAST_WEAPON")) {
					final BodyParts tmp = currentWeaponBodyParts;
					currentWeaponBodyParts = lastWeaponBodyParts;
					lastWeaponBodyParts = tmp;
				}
				//itemPick & talk
				final ItemData item = GHQ.stage().items.forIntersects(UNIT);
				if(gameInputs().consume("INTERACT")) {
					if(item != null)
						inventory.items.add(item.pickup(UNIT));
					else {
						final NAUnit npc = (NAUnit)GHQ.stage().getNearstVisibleEnemy(UNIT);
						if(npc.point().inRange(UNIT.point(), 240)) {
							npc.startTalk((NAUnit)UNIT);
						}
					}
				}else if(item != null) { //show item name
					GHQ.getG2D(Color.WHITE);
					GHQ.drawStringGHQ(item.name(), item.point().intX(), item.point().intY() - 20);
				}
			}
		};
	}
	//personal information
	protected String personalName;
	public ImageFrame personalIcon;
	public DotPaint charaPaint = DotPaint.BLANK_SCRIPT;
	
	//battle
	protected NAUnit targetUnit;
	protected int targetFoundFrame;
	protected boolean isBattleStance;
	protected double suspiciousAngle;
	protected int lastDetectedFrame;
	protected boolean invisibled;

	//favor
	protected final int[] personalFavor = new int[UnitGroup.GROUP_AMOUNT];
	//buff
	protected LinkedList<NABuff> buffs = new LinkedList<NABuff>(),
			waitingBuffs = new LinkedList<NABuff>(),
			waitingDeleteBuffs = new LinkedList<NABuff>();
	//talent
	protected LinkedList<Talent> talents = new LinkedList<Talent>();
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
		ENERGY = new ConsumableEnergy().setMin(0).setMax(new Calculation() {
			@Override
			public Number calculate(Number number) {
				return POW_FIXED.doubleValue()*200.0;
			}
		}).setDefaultToMax(),
		SPEED_PPS = new ConsumableEnergy(new Calculation() {
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
	public final Status status = new Status(RED_BAR, BLUE_BAR, GREEN_BAR, POW_FLOAT, INT_FLOAT, AGI_FLOAT, ENERGY
			, SPEED_PPS, TOUGHNESS, TOUGHNESS_REG, CRI, CRI_EFFECT, AVD, REF, SUCK);
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
	public final TableStorage<NAUsable> quickSlot = new TableStorage<NAUsable>(10, 1, NAUsable.NULL_NA_USABLE);
	public final ItemStorage inventory = new ItemStorage(new TableStorage<ItemData>(5, 3, ItemData.BLANK_ITEM));
	
	public NAUnit(int charaSize) {
		physics().setPoint(new Dynam());
		physics().setHitShape(new Circle(this, charaSize));
		physics().setHitGroup(HitGroup.HIT_ALL);
	}
	@Override
	public NAUnit respawn(int x, int y) {
		status.reset();
		//remain previous weapon.
		for(ItemData item : body.equipments())
			((Equipment)item).reset();
		point().stop();
		lastDetectedFrame = 0;
		dstPoint.setXY(point().setXY(x, y));
		angle().set(0.0);
		inventory.items.clear();
		inventory.add_stack(new Ammo_9mm(32));
		return this;
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
			ENERGY.consume(-RED_BAR.consume(-RED_REG.doubleValue()*GHQ.getSPF())*1.0*GHQ.getSPF());
			ENERGY.consume(-BLUE_BAR.consume(-BLUE_REG.doubleValue()*GHQ.getSPF())*0.2*GHQ.getSPF());
			if(GHQ.isExpired_dynamicSeconds(GREEN_BAR.lastDecreasedFrame(), 1.0))
				ENERGY.consume(-GREEN_BAR.consume(-GREEN_REG.doubleValue()*GHQ.getSPF())*0.1);
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
		// item in inventory (includes weapon equipped to body parts)
		////////////
		for(ItemData item : inventory.items)
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
		point().accelerate_MUL(0.9);
		////////////
		//sight & battle
		////////////
		updateTargetUnit();
		if(!isControllingUnit()) {
			attack();
		} else if(this instanceof Boss_1)
			attack();
		////////////
		//aim
		////////////
		if(isControllingUnit())
			angle().set(point().angleToMouse());
		////////////
		// body
		////////////
		//movement
		//must proceed last, or later functions cannot get correct coordinate.
		body.idle();
	}
	protected void attack() {
		if(targetUnit != null) {
			final double targetAngle = point().angleTo(targetUnit);
			final double angleDiff = angle().spinTo_Suddenly(targetAngle, 10);
			final double distance = point().distance(targetUnit);
			if(angle().isDeltaSmaller(targetAngle, Math.PI*10/18)) {
				if(currentEquipment() == null && angleDiff < 0.30 && distance < 50
						|| angleDiff < currentEquipment().effectiveAngleWidth &&  distance < currentEquipment().effectiveRange) {
					if(currentEquipment() != null)
						currentEquipment().use();
					else
						body().punch.setPunch();
				}
			}
		}else if(isBattleStance) {
			if(GHQ.nowFrame() % 80 == 0) {
				suspiciousAngle = Angle.random();
			}
			angle().spinTo_Suddenly(suspiciousAngle, 10);
		}else
			angle().spinTo_Suddenly(point().moveAngle(), 10);
	}
	@Override
	public void paint() {
		////////////
		//enlightVisibleArea
		////////////
		if(/*true || */isControllingUnit() || NAGame.controllingUnit().isVisible(this)) {
			if(this.isControllingUnit())
				GHQ.getG2D(new Color(1F, 1F, 1F, 0.1F), 1F);
			else
				GHQ.getG2D(new Color(1F, 0F, 0F, 0.1F), 1F);
			Gridder gridder = new Gridder(250, 250);
			for(int xPos = 0;xPos < gridder.W_DIV;++xPos) {
				for(int yPos = 0;yPos < gridder.H_DIV;++yPos) {
					if(isVisible(gridder.getPosPoint(xPos, yPos))) {
						gridder.fillGrid(GHQ.getG2D(), xPos, yPos);
					}
				}
			}
		}
		////////////
		//body and weapon
		////////////
		if(!isControllingUnit()) {
			if(NAGame.controllingUnit().isVisible(this)) {
				lastDetectedFrame = GHQ.nowFrame();
				body.paint();
				GHQ.paintHPArc(point(), 20, RED_BAR.intValue(), RED_BAR.defaultValue().intValue());
			}else {
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
		}
		////////////
		//show mark
		////////////
		if(isControllingUnit() || NAGame.controllingUnit().isVisible(this)) {
			if(isBattleStance) {
				if(targetUnit != null && this.isVisible(targetUnit)) {
					battleStanceWhenVisibleIF.dotPaint(point().intX(), point().intY() - charaPaint.height());
				}else {
					battleStanceIF.dotPaint(point().intX(), point().intY() - charaPaint.height());
				}
			}
		}
	}
	protected final void paintMagicCircle(DotPaint paintScript) {
		paintScript.dotPaint_turn(point(), (double)GHQ.nowFrame()/35.0);
	}
	public void killed() {
		for(int i = inventory.items.traverseFirst();i != -1;i = inventory.items.traverseNext(i))
			GHQ.stage().addItem(inventory.items.remove(i).drop((int)(point().doubleX() + GHQ.random2(-50,50)), (int)(point().doubleY() + GHQ.random2(-50,50))));
	}
	
	//tool
	public void updateTargetUnit() {
		NAUnit leastFriendlyUnit = null;
		int leastFavor = 0, favor;
		double leastFriendlyUnitDistance = Double.MAX_VALUE, distance;
		for(Unit unit : GHQ.stage().getVisibleUnit(this)) {
			if(!isVisible(unit)) //TODO distance of sight should change upon status
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
			isBattleStance = true;
			//say something
			this.setMiniTalking("I found you.");
		}else {
			if(targetUnit != null && GHQ.isExpired_frame(targetFoundFrame, (int)(GHQ.getFPS()*5)))
				targetUnit = null;
			if(isBattleStance && GHQ.isExpired_frame(targetFoundFrame, (int)(GHQ.getFPS()*15)))
				isBattleStance = false;
		}
	}
	public boolean isHostile(int favor) {
		return favor <= -30;
	}
	public boolean isHostile(NAUnit target) {
		return isHostile(favorTo(target));
	}
	public boolean isVisible(HasPoint target) {
		final double DISTANCE = this.point().distance(target);
		return isAware(target) || !(target instanceof NAUnit && ((NAUnit)target).isInvisibled()) && DISTANCE < 700 && this.angle().isDeltaSmaller(this.point().angleTo(target), Math.toRadians(60.0)) && this.point().isVisible(target);
	}
	public boolean isVisible(Point point) {
		final double DISTANCE = this.point().distance(point);
		return isAware(point) || DISTANCE < 700 && this.angle().isDeltaSmaller(this.point().angleTo(point), Math.toRadians(60.0)) && this.point().isVisible(point);
	}
	public boolean isAware(HasPoint target) {
		return isAware(target.point());
	}
	public boolean isAware(Point point) {
		return point().distance(point) < SENSE.intValue()*30;
	}
	protected boolean isVisibleByControllingUnit() {
		return this.isControllingUnit() || NAGame.controllingUnit().isVisible(this);
	}
	protected boolean isAwareByControllingUnit() {
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
	public void startTalk(NAUnit unit) {}
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
	//weapon equip & dequip
	public void equip(Equipment item) {
		body.equip(item);
		item.equipped();
	}
	public void dequip(Equipment item) {
		body.dequip(item);
		item.dequipped();
	}
	public void arm(BodyParts itemSlot) {
		currentWeaponBodyParts = itemSlot;
		if(itemSlot.hasEquipment())
			body().hands().equip(itemSlot.equipment());
	}
	public void changeToNextWeapon() {
		lastWeaponBodyParts = currentWeaponBodyParts;
		if(currentWeaponBodyParts == body().mainEquipSlot()) {
			if(body().hasSubEquip())
				arm(body().subEquipSlot());
			else if(body().hasMelleEquip())
				arm(body().melleEquipSlot());
		}else if(currentWeaponBodyParts == body().subEquipSlot()) {
			if(body().hasMelleEquip())
				arm(body().melleEquipSlot());
			else if(body().hasMainEquip())
				arm(body().mainEquipSlot());
		}else if(currentWeaponBodyParts == body().melleEquipSlot()) {
			if(body().hasMainEquip())
				arm(body().mainEquipSlot());
			else if(body().hasSubEquip())
				arm(body().subEquipSlot());
		}
	}
	public void changeToPrevWeapon() {
		lastWeaponBodyParts = currentWeaponBodyParts;
		if(currentWeaponBodyParts == body().mainEquipSlot()) {
			if(body().hasMelleEquip())
				arm(body().melleEquipSlot());
			else if(body().hasSubEquip())
				arm(body().subEquipSlot());
		}else if(currentWeaponBodyParts == body().subEquipSlot()) {
			if(body().hasMainEquip())
				arm(body().mainEquipSlot());
			else if(body().hasMelleEquip())
				arm(body().melleEquipSlot());
		}else if(currentWeaponBodyParts == body().melleEquipSlot()) {
			if(body().hasSubEquip())
				arm(body().subEquipSlot());
			else if(body().hasMainEquip())
				arm(body().mainEquipSlot());
		}
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
		if(damage == NADamage.NULL_DAMAGE || !(damage instanceof NADamage))
			return;
		RED_BAR.clearLastSet();
		lastDamage = (NADamage)damage;
		final GHQObject shooterObject = bullet.shooter();
		if(shooterObject instanceof NAUnit) {
			damage.doDamage(this, ((NAUnit)shooterObject));
			//tell this unit damaged him
			((NAUnit)shooterObject).damagedTarget(this, bullet);
		}else
			damage.doDamage(this, NAUnit.NULL_NAUnit);
		//knockback when it was physical damage
		final double DMG = RED_BAR.lastSetDiff_underZero();
		if(DMG > 0 && lastDamage.materialType() == DamageMaterialType.Phy) {
			point().addSpeed_DA(lastDamage.knockbackRate()*DMG/WEIGHT.doubleValue()*(containsBuff(ToughnessBroke.class) ? 40 : 20), bullet.point().moveAngle());		
			body.damaged.set();
		}//judge alive
		if(!isAlive())
			killed();
	}
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
	public ItemStorage inventory() {
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
	protected BodyParts currentEquipSlot() {
		return currentWeaponBodyParts;
	}
	public Equipment currentEquipment() {
		return (Equipment)currentWeaponBodyParts.equipment();
	}
	public HumanBody body() {
		return body;
	}
	public NADamage lastDamage() {
		return lastDamage;
	}
	public int getShield() {
		if(body().shield() instanceof ElectronShield) {
			return ((ElectronShield)body().shield()).weapon.getMagazineFilledSpace();
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
	public NAUnit targetUnit() {
		return targetUnit;
	}
	public boolean isInvisibled() {
		return invisibled;
	}
	public boolean hasTargetUnit() {
		return targetUnit != null;
	}
}