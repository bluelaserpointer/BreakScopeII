package unit.body;

import java.util.HashMap;

import item.ItemData;
import paint.HasPaint;
import paint.ImageFrame;
import paint.Layer;
import paint.animation.SerialImageFrame;
import paint.dot.DotPaint;
import paint.dot.DotPaintParameter;
import physics.Angle;
import physics.HasAngle;
import physics.Point;
import physics.RelativePoint;
import unit.Body;
import unit.BodyParts;
import unit.NAUnit;
import unit.Unit;
import unit.NAUnit.BodyPartsTypeLibrary;
import unit.action.Damaged;
import unit.action.Dash;
import unit.action.KnifeSlash;
import unit.action.Punch;
import unit.action.Rolling;
import unit.action.Walk;
import weapon.Equipment;
import weapon.gripStyle.GripStyle;
import weapon.gripStyle.KnifeGrip;
import weapon.gripStyle.RifleGrip;

public class HumanBody extends Body {
	//bodyParts
	protected final BodyParts
		head, trunk, frontHand, backHand, legs, foots;
	protected BodyParts mainWeaponSlot;
	protected BodyParts subWeaponSlot;
	protected BodyParts meleeWeaponSlot;
	protected BodyParts shieldSlot;
	protected BodyParts exoskeletonSlot;
	protected double attackAngle;
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
		
	};
	public final KnifeSlash knifeSlash = new KnifeSlash(this) {
		@Override
		public void setSlash() {
			this.setSlash(backHand, 40, weaponLayerSetting);
		}
		
	};
	public final Punch punch = new Punch(this) {
		boolean nextPunchIsFrontHand = true;
		@Override
		public void activated() {
			super.activated();
			//switch punch animating hands
			if(nextPunchIsFrontHand) {
				punchFrontHand(20, point().angleToMouse());
				nextPunchIsFrontHand = false;
			}else {
				punchBackHand(20, point().angleToMouse());
				nextPunchIsFrontHand = true;
			}
		}
	};
	public final Damaged damaged = new Damaged(this);
	
	//display setting
	private final Layer bodyLayer = new Layer();
	private final DotPaintParameter weaponLayerSetting = new DotPaintParameter();
	private HasPaint weaponLayer = new HasPaint() {
		@Override
		public void paint() {
			if(((NAUnit)owner()).currentEquipment() != null)
				((NAUnit)owner()).currentEquipment().getDotPaint().dotPaint(weaponLayerSetting);
		}
	};
	public HumanBody(Unit owner) {
		super(owner);
		//initialBodyParts
		//TODO: need more graphics
		addBodyParts(head = new BodyParts(this, new RelativePoint(this, new Point.IntPoint(0, 0), false),
				ImageFrame.create("picture/humanbody/head/headIdle.png"), BodyPartsTypeLibrary.HEAD));
		addBodyParts(trunk = new BodyParts(this, new RelativePoint(this, new Point.IntPoint(0, 0), false),
				ImageFrame.create("picture/humanbody/trunk/trunkIdle.png"), BodyPartsTypeLibrary.TRUNK));
		addBodyParts(frontHand = new BodyParts(this, new RelativePoint(this, new Point.DoublePoint(0, 0), owner()),
				ImageFrame.create("picture/humanbody/hand/handIdle.png"), BodyPartsTypeLibrary.HAND));
		addBodyParts(backHand = new BodyParts(this, new RelativePoint(this, new Point.DoublePoint(0, 0), owner()),
				ImageFrame.create("picture/humanbody/hand/handIdle.png"), BodyPartsTypeLibrary.HAND));
		addBodyParts(legs = new BodyParts(this, new RelativePoint(this, new Point.IntPoint(0, 0), false),
				ImageFrame.create("picture/humanbody/legs/legsIdle.png"), BodyPartsTypeLibrary.LEGS));
		addBodyParts(foots = new BodyParts(this, new RelativePoint(this, new Point.IntPoint(0, 0), false),
				DotPaint.BLANK_SCRIPT, BodyPartsTypeLibrary.FOOTS));
		addBodyParts(mainWeaponSlot = new BodyParts(this, Point.NULL_POINT, DotPaint.BLANK_SCRIPT, BodyPartsTypeLibrary.MAIN_WEAPON));
		addBodyParts(subWeaponSlot = new BodyParts(this, Point.NULL_POINT, DotPaint.BLANK_SCRIPT, BodyPartsTypeLibrary.SUB_WEAPON));
		addBodyParts(meleeWeaponSlot = new BodyParts(this, Point.NULL_POINT, DotPaint.BLANK_SCRIPT, BodyPartsTypeLibrary.MELLE_WEAPON));
		addBodyParts(shieldSlot = new BodyParts(this, Point.NULL_POINT, DotPaint.BLANK_SCRIPT, BodyPartsTypeLibrary.SHIELD));
		addBodyParts(exoskeletonSlot = new BodyParts(this, Point.NULL_POINT, DotPaint.BLANK_SCRIPT, BodyPartsTypeLibrary.EXOSKELETON));
		//setHandPointAndLayerOrder
		this.dequipped(null);
		//initialDoableActions
		//walking
		final HashMap<BodyParts, DotPaint> walkAnimations = new HashMap<BodyParts, DotPaint>();
		walkAnimations.put(legs, new SerialImageFrame(2, "picture/humanbody/legs/legsWalk_1.png", "picture/humanbody/legs/legsWalk_2.png", "picture/humanbody/legs/legsWalk_3.png", "picture/humanbody/legs/legsIdle.png"));
		super.addDoableActionAnimations(Walk.class, walkAnimations);
		//dash
		final HashMap<BodyParts, DotPaint> dashAnimations = new HashMap<BodyParts, DotPaint>();
		dashAnimations.put(legs, new SerialImageFrame(2, "picture/humanbody/legs/legsWalk_1.png", "picture/humanbody/legs/legsWalk_2.png"));
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
	@Override
	public void paint() {
		bodyLayer.paint();
		//return hands position
		//((RelativePoint)((RelativePoint)backHand.point()).relativePoint()).relativePoint().approach_rate(0, 0, 0.2);
		//((RelativePoint)((RelativePoint)frontHand.point()).relativePoint()).relativePoint().approach_rate(0, 0, 0.2);
	}
	@Override
	public void equipped(ItemData equipment) {
		final double angle = owner().angle().get();
		final double cos = Math.cos(angle), sin = Math.sin(angle);
		if(!(equipment instanceof Equipment)) {
			return;
		}
		int x = point().intX();
		int y = point().intY();
		final GripStyle grip = ((Equipment)equipment).gripStyle();
		if(grip == null)
			return;
		final int[] handXs = grip.handXPositions();
		final int[] handYs = grip.handYPositions();
		if(grip instanceof RifleGrip) { //rifle
			//final RifleGrip rifleGrip = (RifleGrip)grip;
			x += (int)(30*cos);
			y += (int)(30*sin);
			((RelativePoint)frontHand.point()).relativePoint().setXY(handXs[0]*sin + handYs[0]*cos, -handXs[0]*cos + handYs[0]*sin);
			weaponLayerSetting.point = new RelativePoint(frontHand, new Point.IntPoint(), false);
			//weaponLayerSetting.point.setXY(x, y);
			weaponLayerSetting.angle = angle;
			weaponLayerSetting.sizeCap = 60;
			((RelativePoint)backHand.point()).relativePoint().setXY(handXs[1]*sin + handYs[1]*cos, -handXs[1]*cos + handYs[1]*sin);
			bodyLayer.setLayers(backHand, legs, trunk, head, weaponLayer, frontHand);
		}else if(grip instanceof KnifeGrip) { //knife
			//final KnifeGrip rifleGrip = (KnifeGrip)grip;
			x += (int)(30*cos);
			y += (int)(30*sin);
			//grab knife at -90 degree.
			weaponLayerSetting.point = new RelativePoint(backHand, new Point.IntPoint(), false);
			//weaponLayerSetting.point.addXY(-16*Math.sin(angle), 16*Math.cos(angle));
			weaponLayerSetting.angle = 0.0;
			weaponLayerSetting.sizeCap = 30;
			((RelativePoint)frontHand.point()).relativePoint().setXY(handXs[0]*sin + handYs[0]*cos, -handXs[0]*cos + handYs[0]*sin);
			((RelativePoint)backHand.point()).relativePoint().setXY(handXs[1]*sin + handYs[1]*cos, -handXs[1]*cos + handYs[1]*sin);
			bodyLayer.setLayers(backHand, legs, trunk, head, weaponLayer, frontHand);
		}
	}
	@Override
	public void dequipped(ItemData equipment) {
		((RelativePoint)frontHand.point()).relativePoint().setXY(-9, 100);
		((RelativePoint)backHand.point()).relativePoint().setXY(11, 4);
		bodyLayer.setLayers(backHand, legs, trunk, head, frontHand.equipmentLayer(), frontHand);
	}
	//control
	public void punchFrontHand(double strength, double angle) {
		//frontHand.point().addXY_DA(strength, angle);
		frontHand.point().setX(frontHand.point().intX() + 100);
	}
	public void punchBackHand(double strength, double angle) {
		//backHand.point().addXY_DA(strength, angle);
		frontHand.point().setX(frontHand.point().intX() + 100);
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
