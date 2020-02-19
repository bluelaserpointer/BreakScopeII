package effect;

import static java.lang.Math.PI;

import java.awt.Color;
import java.awt.Font;

import core.GHQ;
import core.GHQObject;
import effect.Effect;
import paint.ImageFrame;
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
		private static DotPaint paint = ImageFrame.create("thhimage/NarrowSpark_HitEffect.png");
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
			paintScript = new StringPaint(str, font, color).setOwner(this);
			point().stop();
			point().addY(-25);
			point().addX(-20);
		}
		@Override
		public DamageNumberEF getOriginal() {
			return new DamageNumberEF(shooter, ((StringPaint)paintScript).WORDS, ((StringPaint)paintScript).FONT, ((StringPaint)paintScript).COLOR);
		}
		@Override
		public void paint() {
			fadingPaint();
		}
	}
}
