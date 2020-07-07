package bullet;


import bullet.Bullet;
import core.GHQ;
import core.GHQObject;
import damage.NADamage;
import effect.EffectLibrary;
import item.ammo.enchant.AmmoEnchants;
import paint.ImageFrame;
import physics.HitRule;
import physics.hitShape.Circle;
import unit.NAUnit;
import unit.UnitAction;
import weapon.Weapon;

public abstract class BulletLibrary extends Bullet {
	
	public BulletLibrary(GHQObject shooter) {
		super(shooter);
	}
	protected AmmoEnchants enchants = new AmmoEnchants();
	public void setEnchants(AmmoEnchants enchants) {
		this.enchants = enchants;
		((NADamage)damage).addDamageComposition(enchants.damageAdd());
	}
	@Override
	public void hitObject(GHQObject object) {
		enchants.applyHitObjectEffect(this, object);
	}
	@Override
	public boolean hitObjectDeleteCheck(GHQObject object) {
		if(object.isUnit()) {
			final UnitAction rollAction = ((NAUnit)object).body().rolling;
			if(rollAction.isActivated() && GHQ.passedFrame(rollAction.initialFrame()) <= 12)
				return false;
		}
		return super.hitObjectDeleteCheck(object);
	}
	/////////////////
	/*	<Parameters and their default values of Bullet>
	 * 
	 *	name = GHQ.NOT_NAMED;
	 *	hitShape = new Square(10);
	 *	damage = 0;
	 *	limitFrame = GHQ.MAX;
	 *	limitRange = GHQ.MAX;
	 *	penetration = 1;
	 *	reflection = 0;
	 *	accel = 1.0;
	 *	paintScript = DotPaint.BLANK_SCRIPT;
	 *	isLaser = false;
	 */
	/////////////////
	//ACCAR
	/////////////////
	public static class BaseBullet extends BulletLibrary {
		public BaseBullet(Weapon originWeapon, GHQObject shooter, HitRule hitGroup) {
			super(shooter);
			this.originWeapon = originWeapon;
			physics().setHitRule(hitGroup);
			physics().setHitShape(new Circle(this, 3));
			point().setSpeed(2);
			point().setMoveAngle(point().moveAngle() - 0.05 + Math.random()*0.1);
			name = "ACCAR";
			limitFrame = 50;
			paintScript = ImageFrame.create(this, "picture/animations/1_fire.png");
		}
		public BaseBullet(Weapon originWeapon, GHQObject shooter, HitRule hitGroup, BaseBullet sample) {
			super(shooter);
			this.originWeapon = originWeapon;
			physics().setHitRule(hitGroup);
			physics().setHitShape(new Circle(this, 3));
			point().setSpeed(2);
			point().setMoveAngle(point().moveAngle() - 0.05 + Math.random()*0.1);
			setDamage(sample.damage);
			this.setEnchants(sample.enchants);
			name = "ACCAR";
			limitFrame = 50;
			paintScript = ImageFrame.create(this, "picture/animations/1_fire.png");
		}
		public BaseBullet getOriginal() {
			return new BaseBullet(originWeapon, shooter, hitGroup(), this);
		}
		@Override
		public void idle() {
			if(checkIsOutofLifeSpan()) {
				claimDeleteFromStage();
				return;
			}
			boolean alive;
			int loops = 50;
			while(point().inStage() && --loops > 0) {
				alive = dynamIdle();
//				if(loops > 5) {
//					GHQ.getG2D(Color.RED).fillRect(point().intX(), point().intY(), 1, 1);
//				} else {
//					paint();
//				}
				if(!alive)
					return;
			}
			paint();
		}
		@Override
		public final void hitObject(GHQObject object) {
			super.hitObject(object);
			GHQ.stage().addEffect(new EffectLibrary.SparkHitEF(this));
		}
	}
}
