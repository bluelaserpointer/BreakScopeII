package item.equipment.weapon;

import java.util.LinkedList;
import java.util.List;

import bullet.Bullet;
import core.GHQ;
import core.GHQObject;
import damage.NADamage;
import item.equipment.weapon.gripStyle.KnifeGrip;
import paint.ImageFrame;
import paint.dot.DotPaint;
import physics.HitRule;
import physics.hitShape.RectShape;
import unit.NAUnit;
import weapon.Weapon;

public class Knife extends NASubWeapon {
	public Knife() {
		super(ImageFrame.create("picture/knife.png"));
		super.setGripStyle(new KnifeGrip() {
			@Override
			public int[] handXPositions() {
				return new int[] {5, 13};
			}
			@Override
			public int[] handYPositions() {
				return new int[] {6, 5};
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
				setCoolTime(5);
			}
			@Override
			public List<Bullet> setBullets(GHQObject shooter, HitRule standpoint) {
				if(shooter instanceof NAUnit) {
					NAUnit unit = (NAUnit)shooter;
					unit.body().knifeSlash.setSlash();
				}
				final LinkedList<Bullet> firedBullets = new LinkedList<>();
				firedBullets.add(GHQ.stage().addBullet(new Bullet(shooter) {
					{
						name = "KnifeStinger";
						paintScript = DotPaint.BLANK_SCRIPT;
						point().addXY_allowsMoveAngle(0, 50);
						point().stop();
						physics().setHitShape(new RectShape(this, 40, 40));
						setDamage(new NADamage((((NAUnit)owner()).POW_FLOAT.doubleValue() - weight())*15)
								.setKnockbackRate(0.1));
						limitFrame = 2;
					}
				}));
				return firedBullets;
			}
		};
	}
	@Override
	public String name() {
		return weapon.name;
	}
	@Override
	public double weight() {
		return 1;
	}
}
