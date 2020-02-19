package weapon;

import bullet.Bullet;
import core.GHQ;
import core.GHQObject;
import damage.NADamage;
import paint.ImageFrame;
import paint.dot.DotPaint;
import physics.HitGroup;
import physics.hitShape.RectShape;
import unit.NAUnit;
import weapon.Weapon;
import weapon.gripStyle.KnifeGrip;

public class Knife extends Equipment {
	public Knife() {
		super(ImageFrame.create("picture/knife.png"), NAUnit.BodyPartsTypeLibrary.MELLE_WEAPON);
		super.setFocusPaint(ImageFrame.create("picture/sword.png"));
		super.setGripStyle(new KnifeGrip() {
			@Override
			public int[] handXPositions() {
				return new int[] {-6, -5};
			}
			@Override
			public int[] handYPositions() {
				return new int[] {-5, -13};
			}
		});
		super.effectiveRange = 50;
		super.effectiveAngleWidth = 0.6;
	}
	@Override
	protected Weapon def_weapon() {
		return new Weapon() {
			{
				name = "Knife";
				coolTime = 5;
			}
			@Override
			public void setBullets(GHQObject shooter, HitGroup standpoint) {
				System.out.println("nowFrame: " + GHQ.nowFrame());
				//((NAUnit)shooter).body().punchFrontHand(20, shooter.angle().get());
				if(shooter instanceof NAUnit) {
					NAUnit unit = (NAUnit)shooter;
					unit.body().knifeSlash.setSlash();
				}
				GHQ.stage().addBullet(new Bullet(shooter) {
					{
						name = "KnifeStinger";
						paintScript = DotPaint.BLANK_SCRIPT;
						point().addXY_allowsMoveAngle(0, 50);
						point().stop();
						physics().setHitShape(new RectShape(this, 40, 40));
						this.damage = new NADamage((((NAUnit)owner()).POW_FLOAT.doubleValue() - 1)*15)
								.setKnockbackRate(0.1);
						limitFrame = 2;
					}
				});
			}
		};
	}
	@Override
	public String name() {
		return weapon.name;
	}
}
