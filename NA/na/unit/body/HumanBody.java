package unit.body;

import java.util.HashMap;

import animation.BumpAnimation;
import core.GHQ;
import item.ItemData;
import item.equipment.Equipment;
import item.equipment.weapon.NAWeaponEquipment;
import item.equipment.weapon.gripStyle.GripStyle;
import item.equipment.weapon.gripStyle.KnifeGrip;
import item.equipment.weapon.gripStyle.RifleGrip;
import paint.HasPaint;
import paint.ImageFrame;
import paint.Layer;
import paint.animation.SerialImageFrame;
import paint.dot.DotPaint;
import paint.dot.DotPaintParameter;
import physics.Angle;
import physics.HasAngle;
import physics.Point;
import physics.RelativeAngle;
import physics.RelativePoint;
import unit.Body;
import unit.BodyParts;
import unit.BodyPartsType;
import unit.NAUnit;
import unit.Unit;
import unit.NAUnit.BodyPartsTypeLibrary;
import unit.action.Damaged;
import unit.action.Dash;
import unit.action.KnifeSlash;
import unit.action.Punch;
import unit.action.Rolling;
import unit.action.Walk;

public class HumanBody extends Body {
	//bodyParts
	protected final BodyParts
		head, trunk, frontHand, backHand, legs, foots;
	protected BodyParts mainWeaponSlot;
	protected BodyParts subWeaponSlot;
	protected BodyParts meleeWeaponSlot;
	protected BodyParts shieldSlot;
	protected BodyParts exoskeletonSlot;
	protected BodyParts currentWeaponBodyParts;
	protected BodyParts lastWeaponBodyParts;
	protected double attackAngle;
	
	class WeaponSlot extends BodyParts {
		public WeaponSlot(Body body, BodyPartsType...types) {
			super(body, Point.NULL_POINT, DotPaint.BLANK_SCRIPT, types);
		}
		@Override
		public boolean equip(ItemData equipment) {
			boolean equipped = super.equip(equipment);
			if(!equipped)
				return false;
			//change hand position
			if(currentWeaponBodyParts == this && equipment instanceof NAWeaponEquipment)
				applyGripStyle(((NAWeaponEquipment)equipment).gripStyle());
			return true;
		}
	}
	//doable actions
	public final Walk.ToDirectionWalk directionWalk = new Walk.ToDirectionWalk(this) {
	};
	public final Walk.ToTargetWalk targetWalk = new Walk.ToTargetWalk(this) {
		
	};
	public final Rolling rolling = new Rolling(this) {
		
	};
	public final Dash.ToTargetDash targetDash = new Dash.ToTargetDash(this) {
		
	};
	public final Dash.ToDirectionDash directionDash = new Dash.ToDirectionDash(this) {
		private double totalUp;
		@Override
		public void idle() {
			super.idle();
			final double lastUp = 2*Math.sin(GHQ.nowFrame()*(Math.PI*2/5));
			frontHand.point().addY(lastUp);
			backHand.point().addY(lastUp);
			trunk.point().addY(lastUp);
			head.point().addY(lastUp);
			totalUp += lastUp;
		}
		@Override
		public void stopped() {
			super.stopped();
			frontHand.point().addY(-totalUp);
			backHand.point().addY(-totalUp);
			trunk.point().addY(-totalUp);
			head.point().addY(-totalUp);
			totalUp = 0;
		}
	};
	public final KnifeSlash knifeSlash = new KnifeSlash(this) {
		@Override
		public void setSlash() {
			this.setSlash(backHand, 40, weaponLayerSetting);
			this.circularMotion.setCurrentAngle(armAngleBase.angle().get());
		}
		
	};
	public final Punch punch = new Punch(this) {
		boolean nextPunchIsFrontHand = true;
		protected final BumpAnimation bumpAnimation2 = new BumpAnimation();
		@Override
		public void idle() {
			super.idle();
			bumpAnimation2.idle();
		}
		@Override
		public void stopped() {
			super.stopped();
			bumpAnimation2.resetPosition();
		}
		@Override
		public void setPunch() {
			if(!super.activate())
				return;
			//switch punch animating hands
			if(nextPunchIsFrontHand) {
				bumpAnimation.setAnimation(frontHand, armAngleBase.angle().get(), 90, 10);
				nextPunchIsFrontHand = false;
			}else {
				bumpAnimation2.setAnimation(backHand, armAngleBase.angle().get(), 45, 10);
				nextPunchIsFrontHand = true;
			}
		}
	};
	public final Damaged damaged = new Damaged(this);
	
	//display setting
	private final Layer bodyLayer = new Layer();
	private final HashMap<HasPaint, HasPaint> lrSwapLayers = new HashMap<HasPaint, HasPaint>();
	private final DotPaintParameter weaponLayerSetting = new DotPaintParameter();
	private HasPaint weaponLayer = new HasPaint() {
		@Override
		public void paint() {
			if(((NAUnit)owner()).currentEquipment() != null)
				((NAUnit)owner()).currentEquipment().getDotPaint().dotPaint(weaponLayerSetting);
		}
	};
	public final HasAngle armAngleBase = new HasAngle() {
		final Angle armAngle = new Angle() {
			@Override
			public double get() {
				double angle = owner().angle().get();
				if(Math.cos(angle) > 0)
					return angle;
				else
					return Math.PI - angle;
			}
		};
		@Override
		public Angle angle() {
			return armAngle;
		}
	};
	public HumanBody(Unit owner) {
		super(owner);
		//initialBodyParts
		//TODO: need more graphics
		addBodyParts(head = new BodyParts(this, new RelativePoint(this, new Point.IntPoint(2, 1), false),
				ImageFrame.create("picture/humanbody/head/head1Idle.png"), BodyPartsTypeLibrary.HEAD));
		addBodyParts(trunk = new BodyParts(this, new RelativePoint(this, new Point.IntPoint(0, 0), false),
				ImageFrame.create("picture/humanbody/trunk/trunk1Idle.png"), BodyPartsTypeLibrary.TRUNK));
		addBodyParts(frontHand = new BodyParts(this, new RelativePoint(this, new Point.DoublePoint(0, 0), armAngleBase),
				ImageFrame.create("picture/humanbody/hand/handIdle.png"), BodyPartsTypeLibrary.HAND));
		addBodyParts(backHand = new BodyParts(this, new RelativePoint(this, new Point.DoublePoint(0, 0), armAngleBase),
				ImageFrame.create("picture/humanbody/hand/handIdle.png"), BodyPartsTypeLibrary.HAND));
		addBodyParts(legs = new BodyParts(this, new RelativePoint(this, new Point.IntPoint(0, 0), false),
				ImageFrame.create("picture/humanbody/legs/legs1Idle.png"), BodyPartsTypeLibrary.LEGS));
		addBodyParts(foots = new BodyParts(this, new RelativePoint(this, new Point.IntPoint(0, 0), false),
				DotPaint.BLANK_SCRIPT, BodyPartsTypeLibrary.FOOTS));
		addBodyParts(mainWeaponSlot = new WeaponSlot(this, BodyPartsTypeLibrary.MAIN_WEAPON));
		addBodyParts(subWeaponSlot = new WeaponSlot(this, BodyPartsTypeLibrary.SUB_WEAPON));
		addBodyParts(meleeWeaponSlot = new WeaponSlot(this, BodyPartsTypeLibrary.MELLE_WEAPON));
		addBodyParts(shieldSlot = new WeaponSlot(this, BodyPartsTypeLibrary.SHIELD));
		addBodyParts(exoskeletonSlot = new WeaponSlot(this, BodyPartsTypeLibrary.EXOSKELETON));
		arm(mainWeaponSlot);
		lastWeaponBodyParts = currentWeaponBodyParts;
		//initialDoableActions
		//walking
		final HashMap<BodyParts, DotPaint> walkAnimations = new HashMap<BodyParts, DotPaint>();
		walkAnimations.put(legs, new SerialImageFrame(2, "picture/humanbody/legs/legs1Walk_1.png", "picture/humanbody/legs/legs1Walk_2.png", "picture/humanbody/legs/legs1Walk_1.png", "picture/humanbody/legs/legs1Idle.png"));
		super.addDoableActionAnimations(Walk.class, walkAnimations);
		//dash
		final HashMap<BodyParts, DotPaint> dashAnimations = new HashMap<BodyParts, DotPaint>();
		dashAnimations.put(legs, new SerialImageFrame(1, "picture/humanbody/legs/legsWalk_1.png", "picture/humanbody/legs/legsWalk_2.png", "picture/humanbody/legs/legsWalk_3.png", 
				"picture/humanbody/legs/legs1Idle.png",
				"picture/humanbody/legs/legs1Walk_1.png", "picture/humanbody/legs/legs1Walk_2.png",
				"picture/humanbody/legs/legs1Walk_1.png", "picture/humanbody/legs/legs1Idle.png", "picture/humanbody/legs/legsWalk_3.png", "picture/humanbody/legs/legsWalk_2.png"));
		super.addDoableActionAnimations(Dash.class, dashAnimations);
		//rolling
		final HashMap<BodyParts, DotPaint> rollAnimations = new HashMap<BodyParts, DotPaint>();
		rollAnimations.put(head, DotPaint.BLANK_SCRIPT);
		rollAnimations.put(trunk, new SerialImageFrame(2, "picture/humanbody/rolling_1.png", "picture/humanbody/rolling_2.png"));
		rollAnimations.put(frontHand, DotPaint.BLANK_SCRIPT);
		rollAnimations.put(backHand, DotPaint.BLANK_SCRIPT);
		rollAnimations.put(legs, DotPaint.BLANK_SCRIPT);
		rollAnimations.put(foots, DotPaint.BLANK_SCRIPT);
		super.addDoableActionAnimations(Rolling.class, rollAnimations);
		//punch
		final HashMap<BodyParts, DotPaint> punchAnimations = new HashMap<BodyParts, DotPaint>();
		punchAnimations.put(frontHand, frontHand.getDotPaint());
		punchAnimations.put(backHand, backHand.getDotPaint());
		super.addDoableActionAnimations(Punch.class, punchAnimations);
		//knifeSlash
		final HashMap<BodyParts, DotPaint> knifeSlashAnimations = new HashMap<BodyParts, DotPaint>();
		knifeSlashAnimations.put(frontHand, frontHand.getDotPaint());
		super.addDoableActionAnimations(KnifeSlash.class, knifeSlashAnimations);
		//damaged
		final HashMap<BodyParts, DotPaint> damagedAnimations = new HashMap<BodyParts, DotPaint>();
		damagedAnimations.put(head, head.getDotPaint());
		damagedAnimations.put(trunk, ImageFrame.create("picture/humanbody/trunk/trunkDamaged.png"));
		damagedAnimations.put(frontHand, frontHand.getDotPaint());
		damagedAnimations.put(backHand, backHand.getDotPaint());
		damagedAnimations.put(legs, legs.getDotPaint());
		damagedAnimations.put(foots, foots.getDotPaint());
		super.addDoableActionAnimations(Damaged.class, damagedAnimations);
	}
	/*private boolean lookingRight = true;
	private void doLayerToggle() {
		final Point relativePoint1 = ((RelativePoint)frontHand.point()).relativePoint();
		relativePoint1.setXY(-relativePoint1.doubleX(), -relativePoint1.doubleY());
		final Point relativePoint2 = ((RelativePoint)backHand.point()).relativePoint();
		relativePoint2.setXY(-relativePoint2.doubleX(), -relativePoint2.doubleY());
		for(HasPaint ver : lrSwapLayers.keySet()) {
			int id1 = bodyLayer.layerList().indexOf(ver);
			int id2 = bodyLayer.layerList().indexOf(lrSwapLayers.get(ver));
			if(id1 == -1 || id2 == -1)
				continue;
			System.out.println("toggled");
			bodyLayer.layerList().set(id1, lrSwapLayers.get(ver));
			bodyLayer.layerList().set(id2, ver);
		}
	}*/
	@Override
	public void paint() {
		/*if(angle().directionLR().isRight()) {
			if(!lookingRight) {
				doLayerToggle();
				System.out.println("toggleRight");
				lookingRight = true;
			}
		}else {
			if(lookingRight) {
				doLayerToggle();
				System.out.println("toggleLeft");
				lookingRight = false;
			}
		}*/
		bodyLayer.paint();
		//return hands position
		//((RelativePoint)((RelativePoint)backHand.point()).relativePoint()).relativePoint().approach_rate(0, 0, 0.2);
		//((RelativePoint)((RelativePoint)frontHand.point()).relativePoint()).relativePoint().approach_rate(0, 0, 0.2);
	}
	//control
	class XFlipDotPaint implements HasPaint {
		HasPaint hasPaint;
		public XFlipDotPaint(HasPaint hasPaint) {
			this.hasPaint = hasPaint;
		}
		@Override
		public void paint() {
			if(angle().directionLR().isLeft()) {
				flipXAtBodyCenter();
				hasPaint.paint();
				flipXAtBodyCenter();
			}else
				hasPaint.paint();
		}
	}
	private void flipXAtBodyCenter() {
		final int x = point().intX(), y = point().intY();
		GHQ.getG2D().translate(x, y);
		GHQ.getG2D().scale(-1.0, 1.0);
		GHQ.getG2D().translate(-x, -y);
	}
	protected void applyGripStyle(GripStyle gripStyle) {
		if(gripStyle == null) {
			toNaturalForm();
			return;
		}
		final int[] handXs = gripStyle.handXPositions();
		final int[] handYs = gripStyle.handYPositions();
		if(gripStyle instanceof RifleGrip) { //rifle
			weaponLayerSetting.point = new RelativePoint(frontHand, new Point.IntPoint(15, -7), armAngleBase);
			weaponLayerSetting.angle = new RelativeAngle(armAngleBase);
			weaponLayerSetting.sizeCap = 60;
			((RelativePoint)frontHand.point()).relativePoint().setXY(handXs[0], handYs[0]);
			((RelativePoint)backHand.point()).relativePoint().setXY(handXs[1], handYs[1]);
			bodyLayer.setLayers(new XFlipDotPaint(legs), new XFlipDotPaint(trunk), new XFlipDotPaint(head), new XFlipDotPaint(backHand), new XFlipDotPaint(weaponLayer), new XFlipDotPaint(frontHand));
			lrSwapLayers.clear();
			lrSwapLayers.put(frontHand, backHand);
		}else if(gripStyle instanceof KnifeGrip) { //knife
			//final KnifeGrip rifleGrip = (KnifeGrip)grip;
			//grab knife at -90 degree.
			weaponLayerSetting.point = new RelativePoint(backHand, new Point.IntPoint(0, 8), armAngleBase);
			weaponLayerSetting.angle = new RelativeAngle(armAngleBase);
			weaponLayerSetting.angle.spin(Math.PI/2);
			weaponLayerSetting.sizeCap = 30;
			((RelativePoint)frontHand.point()).relativePoint().setXY(handXs[0], handYs[0]);
			((RelativePoint)backHand.point()).relativePoint().setXY(handXs[1],handYs[1]);
			bodyLayer.setLayers(new XFlipDotPaint(backHand), new XFlipDotPaint(legs), new XFlipDotPaint(trunk), new XFlipDotPaint(head), new XFlipDotPaint(weaponLayer), new XFlipDotPaint(frontHand));
			lrSwapLayers.clear();
			lrSwapLayers.put(frontHand, backHand);
		}else { //natural form
			toNaturalForm();
		}
	}
	public void armLast() {
		arm(lastWeaponBodyParts);
	}
	public void arm(BodyParts itemSlot) {
		if(currentWeaponBodyParts == itemSlot)
			return;
		lastWeaponBodyParts = currentWeaponBodyParts;
		currentWeaponBodyParts = itemSlot;
		final ItemData item = itemSlot.equipment();
		if(item == null) {
			toNaturalForm();
			return;
		}
		this.applyGripStyle(((NAWeaponEquipment)item).gripStyle());
		hands().equip(item);
	}
	public void rearm() {
		final ItemData item = currentWeaponBodyParts.equipment();
		if(item == null) {
			toNaturalForm();
			return;
		}
		this.applyGripStyle(((NAWeaponEquipment)item).gripStyle());
		hands().equip(item);
	}
	public void toNaturalForm() {
		((RelativePoint)frontHand.point()).relativePoint().setXY(-9, 1);
		((RelativePoint)backHand.point()).relativePoint().setXY(11, 4);
		bodyLayer.setLayers(new XFlipDotPaint(backHand), new XFlipDotPaint(legs), new XFlipDotPaint(trunk), new XFlipDotPaint(head), frontHand.equipmentLayer(), new XFlipDotPaint(frontHand));
		lrSwapLayers.clear();
		lrSwapLayers.put(frontHand, backHand);
	}
	public void changeToNextWeapon() {
		lastWeaponBodyParts = currentWeaponBodyParts;
		if(currentWeaponBodyParts == mainEquipSlot()) {
			if(hasSubEquip())
				arm(subEquipSlot());
			else if(hasMelleEquip())
				arm(melleEquipSlot());
		}else if(currentWeaponBodyParts == subEquipSlot()) {
			if(hasMelleEquip())
				arm(melleEquipSlot());
			else if(hasMainEquip())
				arm(mainEquipSlot());
		}else if(currentWeaponBodyParts == melleEquipSlot()) {
			if(hasMainEquip())
				arm(mainEquipSlot());
			else if(hasSubEquip())
				arm(subEquipSlot());
		}
	}
	public void changeToPrevWeapon() {
		lastWeaponBodyParts = currentWeaponBodyParts;
		if(currentWeaponBodyParts == mainEquipSlot()) {
			if(hasMelleEquip())
				arm(melleEquipSlot());
			else if(hasSubEquip())
				arm(subEquipSlot());
		}else if(currentWeaponBodyParts == subEquipSlot()) {
			if(hasMainEquip())
				arm(mainEquipSlot());
			else if(hasMelleEquip())
				arm(melleEquipSlot());
		}else if(currentWeaponBodyParts == melleEquipSlot()) {
			if(hasSubEquip())
				arm(subEquipSlot());
			else if(hasMainEquip())
				arm(mainEquipSlot());
		}
	}
	//information
	public BodyParts head() {
		return head;
	}
	public BodyParts trunk() {
		return trunk;
	}
	public BodyParts hands() {
		return frontHand;
	}
	public BodyParts legs() {
		return legs;
	}
	public BodyParts foots() {
		return foots;
	}
	public BodyParts mainEquipSlot() {
		return mainWeaponSlot;
	}
	public BodyParts subEquipSlot() {
		return subWeaponSlot;
	}
	public BodyParts melleEquipSlot() {
		return meleeWeaponSlot;
	}
	public BodyParts shieldSlot() {
		return shieldSlot;
	}
	public BodyParts exoskeletonSlot() {
		return exoskeletonSlot;
	}
	public BodyParts currentEquipSlot() {
		return currentWeaponBodyParts;
	}
	public Equipment mainEquip() {
		return (Equipment)mainWeaponSlot.equipment();
	}
	public Equipment subEquip() {
		return (Equipment)subWeaponSlot.equipment();
	}
	public Equipment meleeEquip() {
		return (Equipment)meleeWeaponSlot.equipment();
	}
	public Equipment shield() {
		return (Equipment)shieldSlot.equipment();
	}
	public Equipment exoskeleton() {
		return (Equipment)exoskeletonSlot.equipment();
	}
	public Equipment currentEquipment() {
		return (Equipment)currentWeaponBodyParts.equipment();
	}
	public boolean hasMainEquip() {
		return mainEquip() != null;
	}
	public boolean hasSubEquip() {
		return subEquip() != null;
	}
	public boolean hasMelleEquip() {
		return meleeEquip() != null;
	}
	public boolean hasShield() {
		return shield() != null;
	}
	public boolean hasExoskeleton() {
		return exoskeleton() != null;
	}
}