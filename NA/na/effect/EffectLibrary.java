package effect;

import static java.lang.Math.PI;

import java.awt.Color;
import java.awt.Font;

import core.GHQ;
import core.GHQObject;
import effect.Effect;
import paint.ImageFrame;
import paint.animation.SerialImageFrame;
import paint.dot.DotPaint;
import paint.text.StringPaint;

public abstract class EffectLibrary extends Effect{
	
	public EffectLibrary(GHQObject source) {
		super(source);
	}
	/////////////////
	/*	<Parameters and their default values of Bullet>
	 * 
		name = GHQ.NOT_NAMED;
		limitFrame = GHQ.MAX;
		limitRange = GHQ.MAX;
		accel = 1.0;
		paintScript = DotPaint.BLANK_SCRIPT;
	 */
	/////////////////
	//SparkHitEF
	/////////////////
	public static class SparkHitEF extends EffectLibrary{
		private final DotPaint paint = new SerialImageFrame(this, 1,
				"picture/animations/1_hit.png",
				"picture/animations/2_hit.png",
				"picture/animations/3_hit.png",
				"picture/animations/4_hit.png");
		public SparkHitEF(GHQObject source) {
			super(source);
			name = "SparkHitEF";
			limitFrame = 3;
			paintScript = paint;
			point().stop();
			point().setMoveAngle(GHQ.random2(0, 2*PI));
		}
		@Override
		public SparkHitEF getOriginal() {
			return new SparkHitEF(shooter);
		}
	}
	/////////////////
	//DamageNumberEF
	/////////////////
	public static class DamageNumberEF extends EffectLibrary{
		public DamageNumberEF(GHQObject source, String str, Font font, Color color) {
			super(source);
			name = "DamageNumberEF";
			limitFrame = 25;
			paintScript = new DotPaint() {
				StringPaint stringPaint1 = new StringPaint(str, font, color);
				StringPaint stringPaint2 = new StringPaint(str, font, Color.DARK_GRAY);
				@Override
				public void dotPaint(int x, int y) {
					stringPaint2.dotPaint(-2, -2);
					stringPaint2.dotPaint(2, 2);
					stringPaint1.dotPaint(0, 0);
				}
				@Override
				public int width() {
					return stringPaint1.width() + 1;
				}
				@Override
				public int height() {
					return stringPaint1.height() + 1;
				}
			};
			point().setSpeed_DA(6 + Math.random(), 2.0*(Math.random() - 0.5) - Math.PI/2);
			point().addY(-25);
			point().addX(-20);
		}
		@Override
		public DamageNumberEF getOriginal() {
			return new DamageNumberEF(shooter, ((StringPaint)paintScript).WORDS, ((StringPaint)paintScript).FONT, ((StringPaint)paintScript).COLOR);
		}
		@Override
		public void idle() {
			super.idle();
			point().addSpeed(0, 0.4);
		}
		private static final double SIZE_RATE = 2.5;
		@Override
		public void paint() {
			final double lifePercent = lifePercent();
			paintScript.dotPaint_rate(point(), SIZE_RATE*Math.max(0.1, lifePercent < 0.5 ? lifePercent : 1.0 - lifePercent));
		}
	}
}
