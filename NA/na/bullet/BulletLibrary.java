package bullet;

import core.GHQ;
import core.GHQObject;
import damage.NADamage;
import effect.EffectLibrary;
import effect.EffectLibrary.BulletLineEF;
import engine.NAGame;
import item.ammo.enchant.AmmoEnchants;
import paint.ImageFrame;
import physics.HitGroup;
import physics.hitShape.Circle;
import preset.bullet.Bullet;
import preset.unit.Unit;
import preset.unit.UnitAction;
import structure.NATile;
import unit.NAUnit;
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
	public boolean suspendSparkEF = false;
	@Override
	public void hitObject(GHQObject object) {
		enchants.applyHitObjectEffect(this, object);
		if(object instanceof NATile) {
			((NATile)object).attackedLastHitTile((NADamage)this.damage());
		}
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
		public BaseBullet(Weapon originWeapon, GHQObject shooter, HitGroup hitGroup) {
			super(shooter);
			this.originWeapon = originWeapon;
			physics().setHitRule(hitGroup);
			init();
		}
		public BaseBullet(BaseBullet sample) {
			super(sample.shooter);
			this.originWeapon = sample.originWeapon;
			physics().setHitRule(sample.hitGroup());
			init();
			point().setAll(sample);
			setDamage(sample.damage);
			this.setEnchants(sample.enchants.clone());
		}
		private void init() {
			physics().setHitShape(new Circle(this, 12));
			point().setSpeed(2);
			point().setMoveAngle(point().moveAngle() - 0.05 + Math.random()*0.1);
			name = "ACCAR";
			limitFrame = 50;
			paintScript = ImageFrame.create(this, "picture/animations/1_fire.png");
		}
		@Override
		public BaseBullet clone() {
			return new BaseBullet(this);
		}
		@Override
		public void idle() {
			if(checkIsOutofLifeSpan()) {
				claimDeleteFromStage();
				return;
			}
			boolean alive;
			final int LOOP_AMOUNT = 3000;
			int loops = LOOP_AMOUNT;
			int xTmp = point().intX(), yTmp = point().intY();
			while(point().inStage() && --loops > 0) {
				alive = dynamIdle();
//				if(loops > 5) {
//					GHQ.getG2D(Color.RED).fillRect(point().intX(), point().intY(), 1, 1);
//				} else {
//					paint();
//				}
				if(point().inRangeXY(NAGame.controllingUnit().point(), GHQ.fieldScreenW(), GHQ.fieldScreenH())) {
					final int passedLoops = LOOP_AMOUNT - loops;
					if(passedLoops % 25 == 0 || !alive) {
						GHQ.stage().addEffect(new BulletLineEF(this, passedLoops/25, xTmp, yTmp, point().intX(), point().intY()));
						xTmp = point().intX();yTmp = point().intY();
					}
					if(!alive) {
						claimDeleteFromStage();
						return;
					}
				}
			}
			//DebugEffect.setDot(Color.RED, point());
			//paint();
		}
		@Override
		public final void hitObject(GHQObject object) {
			super.hitObject(object);
			if(object instanceof Unit) {
				for(int i = 0; i < 100; ++i) {
					GHQ.stage().addEffect(new EffectLibrary.HitCreatureEF(this, point().moveAngle()));
				}
			} else {
				if(suspendSparkEF)
					suspendSparkEF = false;
				else {
					for(int i = 0; i < 100; ++i) {
						GHQ.stage().addEffect(new EffectLibrary.HitWallEF(this, point().moveAngle() + Math.PI));
					}
				}
			}
			GHQ.stage().addEffect(new EffectLibrary.SparkHitEF(this));
		}
	}
}
