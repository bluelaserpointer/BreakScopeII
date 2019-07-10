package unit;

import bullet.Bullet;
import core.GHQ;
import hitShape.Circle;
import paint.ImageFrame;
import paint.dot.DotPaint;
import physics.HasAnglePoint;
import physics.Standpoint;
import weapon.Weapon;

public abstract class BulletLibrary extends Bullet{
	
	public BulletLibrary(Weapon sourceWeapon, HasAnglePoint shooter, Standpoint standpoint) {
		super(sourceWeapon, shooter, standpoint);
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
		private static final DotPaint paint = ImageFrame.createNew("picture/Bullet_7p62.png");
		public ACCAR(Weapon sourceWeapon, HasAnglePoint shooter, Standpoint standpoint) {
			super(sourceWeapon, shooter, standpoint);
			name = "ACCAR";
			hitShape = new Circle(3);
			damage = 8;
			limitFrame = 2;
			paintScript = paint;
		}
		public ACCAR getOriginal() {
			return new ACCAR(ORIGIN_WEAPON, SHOOTER, STANDPOINT);
		}
		@Override
		public void idle() {
			if(checkIsOutofLifeSpan()) {
				claimDelete();
				return;
			}
			boolean alive;
			while(dynam.inStage()) {
				alive = dynamIdle();
				paint();
				if(!alive)
					return;
			}
			dynam.setXY(SHOOTER);
			dynam.setMoveAngle(SHOOTER.angle().angle());
		}
		@Override
		public final void hitObject() {
			GHQ.stage().addEffect(new EffectLibrary.SparkHitEF(this));
		}
	}
}
