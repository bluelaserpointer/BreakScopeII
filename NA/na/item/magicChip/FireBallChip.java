package item.magicChip;

import java.awt.Color;

import bullet.Bullet;
import core.GHQ;
import damage.DamageMaterialType;
import damage.DamageResourceType;
import damage.NADamage;
import effect.Effect;
import paint.ImageFrame;
import paint.dot.DotPaint;
import unit.NAUnit;
import unit.Unit;

public class FireBallChip extends MagicChip {
	private final DotPaint CHIP_ICON = new DotPaint() {
		private static final long serialVersionUID = 4834375862689090114L;
		private final ImageFrame CHIP_IF = ImageFrame.create("picture/MagicChip/CHI_BET.png");
		@Override
		public void dotPaint(int x, int y) {
			if(hasOwner()) {
				GHQ.getG2D(new Color(0, 0, 0, (int)(255*(1.0 - coolRate())))).fillRect(x - width()/2, y - height()/2, width(), height());
			}
			CHIP_IF.dotPaint(x, y);
		}
		@Override
		public int width() {
			return CHIP_IF.width();
		}
		@Override
		public int height() {
			return CHIP_IF.height();
		}
	};
	private static final ImageFrame BULLET_PAINT = ImageFrame.create("picture/FireScatt.png");
	public FireBallChip() {
		super(DotPaint.BLANK_SCRIPT, 10, 1);
		super.setDotPaint(CHIP_ICON);
	}
	@Override
	public String name() {
		return "FireBallChip";
	}
	@Override
	public void use() {
		if(!isReady() || ((NAUnit)owner).BLUE_BAR.intValue() < 50)
			return;
		((NAUnit)owner).BLUE_BAR.consume(50);
		GHQ.stage().addBullet(new Bullet(owner) {
			{
				name = "FireBall";
				damage = NADamage.NULL_DAMAGE;
				point().setSpeed(20);
				point().addXY_allowsMoveAngle(0, owner.width());
				paintScript = BULLET_PAINT;
				limitRange = owner.point().distance(GHQ.mouseX(), GHQ.mouseY());
			}
			@Override
			public void hitObject() {
				super.hitObject();
				explode();
			}
			@Override
			public boolean outOfRange() {
				super.outOfRange();
				explode();
				return true;
			}
			private void explode() {
				final Effect EFFECT = GHQ.stage().addEffect(new Effect(this) {
					@Override
					public void paint() {
						paintScript.dotPaint_rate(point(), 1.0 + GHQ.passedFrame(initialFrame)*1.5);
					}
				});
				EFFECT.setName("explosion");
				EFFECT.paintScript = ImageFrame.create("picture/RedExplosion2.png");
				EFFECT.limitFrame = 6;
				for(Unit unit : GHQ.stage().units) {
					if(unit.point().inRange(this, 300))
						unit.damage(new NADamage(((NAUnit)owner).INT_FLOAT.doubleValue()*60, DamageMaterialType.Heat, DamageResourceType.Bullet));
				}
			}
		});
	}
}
