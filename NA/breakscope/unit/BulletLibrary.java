package unit;

import bullet.Bullet;
import core.GHQ;
import core.Standpoint;
import geom.Circle;
import paint.DotPaint;
import paint.ImageFrame;
import physicis.HasDynam;
import weapon.Weapon;

public abstract class BulletLibrary extends Bullet{

	public static void loadResource() {
		ACCAR.paint = ImageFrame.createNew("picture/Bullet_7p62.png");
	}
	
	public BulletLibrary(Weapon sourceWeapon, HasDynam shooter, Standpoint standpoint) {
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
		private static DotPaint paint;
		public ACCAR(Weapon sourceWeapon, HasDynam shooter, Standpoint standpoint) {
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
		public boolean idle() {
			if(checkIsOutofLifeSpan()) {
				delete();
				return false;
			}
			boolean alive;
			while(dynam.inStage()) {
				alive = dynamIdle();
				paint();
				if(!alive)
					return false;
			}
			dynam.setXY(SHOOTER);
			dynam.setAngle(SHOOTER);
			return true;
		}
		@Override
		public void paint() {
			super.paint();
		}
		@Override
		public final void hitObject() {
			GHQ.addEffect(new EffectLibrary.SparkHitEF(this));
		}
	}
}
