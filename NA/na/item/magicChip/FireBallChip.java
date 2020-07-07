package item.magicChip;

import java.awt.Color;

import bullet.Bullet;
import core.GHQ;
import core.GHQObject;
import damage.NADamage;
import engine.NAGame;
import liquid.Flame;
import liquid.NALiquidState;
import paint.ImageFrame;
import paint.dot.DotPaint;
import unit.NAUnit;

public class FireBallChip extends MagicChip {
	public static final int BLUE_BAR_COST = 50;
	private Bullet bullet;
	private final DotPaint CHIP_ICON = new DotPaint() {
		private final ImageFrame CHIP_IF = ImageFrame.create("picture/MagicChip/CHI_BET.png");
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
		if(!hasOwner())
			return;
		if(bullet != null && !bullet.hasDeleteClaimFromStage()) {
			bullet.outOfRange();
			return;
		}
		if(!isReady() || ((NAUnit)owner).BLUE_BAR.intValue() < BLUE_BAR_COST)
			return;
		((NAUnit)owner).BLUE_BAR.consume(BLUE_BAR_COST);
		super.use(); //restart cool process
		bullet = GHQ.stage().addBullet(new Bullet(owner) {
			private final double fireDepth = ((NAUnit)owner).INT_FLOAT.doubleValue()*10;
			{
				name = "FireBall";
				setDamage(NADamage.NULL_DAMAGE);
				point().setSpeed(20);
				point().addXY_allowsMoveAngle(0, owner.width());
				paintScript = BULLET_PAINT;
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
				NAGame.stage().addLiquid(point(), Flame.FIXED_FLAME_TAG, NALiquidState.GAS, fireDepth);
			}
		});
	}
}
