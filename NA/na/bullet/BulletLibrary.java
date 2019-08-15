package bullet;

import bullet.Bullet;
import core.GHQ;
import effect.EffectLibrary;
import hitShape.Circle;
import paint.ImageFrame;
import paint.dot.DotPaint;
import physics.HasAnglePoint;
import physics.Standpoint;
import unit.BasicUnit;
import unit.Unit;
import weapon.Weapon;

public abstract class BulletLibrary extends Bullet{
	
	public BulletLibrary(Weapon sourceWeapon, HasAnglePoint shooter, Standpoint standpoint) {
		super(sourceWeapon, shooter, standpoint);
	}
	@Override
	public boolean hitUnitDeleteCheck(Unit unit) {
		((BasicUnit)unit).damage_amount(damage, this.dynam);
		hitObject();
		if(penetration > 0) {
			if(penetration != GHQ.MAX)
				penetration--;
		}else {
			return outOfPenetration();
		}
		return false;
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
		public ACCAR(Weapon sourceWeapon, HasAnglePoint shooter, Standpoint standpoint) {
			super(sourceWeapon, shooter, standpoint);
			name = "ACCAR";
			hitShape = new Circle(dynam, 3);
			damage = 8;
			limitFrame = 2;
			paintScript = paint;
		}
		public ACCAR getOriginal() {
			return new ACCAR(ORIGIN_WEAPON, SHOOTER, standpoint());
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
