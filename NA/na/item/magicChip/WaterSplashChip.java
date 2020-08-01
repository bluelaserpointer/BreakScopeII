package item.magicChip;

import java.awt.Color;

import core.GHQ;
import core.GHQObject;
import damage.NADamage;
import engine.NAGame;
import liquid.NALiquidState;
import liquid.Water;
import paint.ImageFrame;
import paint.dot.DotPaint;
import preset.bullet.Bullet;
import unit.NAUnit;

public class WaterSplashChip extends MagicChip {
	public static final int BLUE_BAR_COST = 1;
	private final DotPaint CHIP_ICON = new DotPaint() {
		private final ImageFrame CHIP_IF = ImageFrame.create("picture/MagicChip/ENG_BET.png");
		@Override
		public void dotPaint(int x, int y) {
			final boolean hasOwner = hasOwner();
			if(hasOwner)
				GHQ.getG2D(new Color(0, 0, 0, (int)(255*(1.0 - coolRate())))).fillRect(x - width()/2, y - height()/2, width(), height());
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
	private static final ImageFrame BULLET_PAINT = ImageFrame.create("picture/bullet/WaterSplash.png");
	public WaterSplashChip() {
		super(DotPaint.BLANK_SCRIPT, 0, 0);
		super.setDotPaint(CHIP_ICON);
	}
	@Override
	public String name() {
		return "WaterSplashChip";
	}
	@Override
	public void start() {
		if(((NAUnit)owner).BLUE_BAR.intValue() < BLUE_BAR_COST)
			return;
		((NAUnit)owner).BLUE_BAR.consume(BLUE_BAR_COST);
		super.use(); //restart cool process
		final double waterDepth = 2.0;
		final int splashAmount = (int)(((NAUnit)owner).INT_FLOAT.doubleValue()*2.0);
		for(int i = 0; i < splashAmount; ++i) {
			GHQ.stage().addBullet(new Bullet(owner) {
				{
					name = "WaterSplash";
					setDamage(NADamage.NULL_DAMAGE);
					point().setSpeed_DA(15 + Math.random()*10, point().moveAngle() + Math.random()*1.0);
					point().addXY_allowsMoveAngle(0, owner.width());
					this.limitRange = 300 + Math.random()*300;
					paintScript = BULLET_PAINT;
				}
				@Override
				public boolean hitObjectDeleteCheck(GHQObject object) {
					if(object.isUnit()) {
						final double weight = 20*((NAUnit)object).WEIGHT.doubleValue();
						object.point().addSpeed(point().xSpeed()/weight, point().ySpeed()/weight);
					}
					return super.hitObjectDeleteCheck(object);
				}
				@Override
				public void hitObject(GHQObject object) {
					super.hitObject(object);
					explode();
				}
				@Override
				public boolean outOfRange() {
					super.outOfRange();
					explode();
					return true;
				}
				private void explode() {
					point().addXY(point().xSpeed()*(1.0 + Math.random())*3, point().ySpeed()*(1.0 + Math.random())*3);
					NAGame.stage().addLiquid(point(), Water.FIXED_WATER_TAG, NALiquidState.WATER_SOLUABLE, waterDepth);
				}
			});
		}
	}
	@Override
	public boolean supportSerialUse() {
		return true;
	}
}