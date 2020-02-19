package bullet;

import bullet.Bullet;
import core.GHQ;
import core.GHQObject;
import damage.DamageMaterialType;
import damage.DamageResourceType;
import damage.NADamage;
import effect.EffectLibrary;
import paint.ImageFrame;
import paint.dot.DotPaint;
import physics.HitGroup;
import physics.hitShape.Circle;
import weapon.Weapon;

public abstract class BulletLibrary extends Bullet{
	
	public BulletLibrary(GHQObject shooter) {
		super(shooter);
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
	public static class ACCAR extends BulletLibrary{
		private static final DotPaint paint = ImageFrame.create("picture/Bullet_7p62.png");
		public ACCAR(Weapon originWeapon, GHQObject shooter, HitGroup hitGroup) {
			super(shooter);
			this.originWeapon = originWeapon;
			physics().setHitGroup(hitGroup);
			physics().setHitShape(new Circle(this, 3));
			name = "ACCAR";
			damage = new NADamage(10, DamageMaterialType.Phy, DamageResourceType.Bullet);
			limitFrame = 1;
			paintScript = paint;
		}
		public ACCAR getOriginal() {
			return new ACCAR(originWeapon, shooter, hitGroup());
		}
		@Override
		public void idle() {
			if(checkIsOutofLifeSpan()) {
				claimDelete();
				return;
			}
			boolean alive;
			while(point().inStage()) {
				alive = dynamIdle();
				paint();
				if(!alive)
					return;
			}
			point().setMoveAngle(shooter.angle().get());
		}
		@Override
		public final void hitObject() {
			GHQ.stage().addEffect(new EffectLibrary.SparkHitEF(this));
		}
	}
}
