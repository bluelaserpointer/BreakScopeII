package effect;

import static java.lang.Math.PI;

import java.awt.Color;

import core.GHQ;
import core.GHQObject;
import effect.Effect;
import paint.ImageFrame;
import paint.dot.DotPaint;
import paint.text.StringViewer;

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
		private static DotPaint paint = ImageFrame.create("thhimage/NarrowSpark_HitEffect.png");;
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
		public DamageNumberEF(GHQObject source, int damage) {
			super(source);
			name = "DamageNumberEF";
			limitFrame = 15;
			paintScript = new StringViewer(String.valueOf(damage), Color.RED);
			point().stop();
			point().setMoveAngle(0);
		}
		@Override
		public DamageNumberEF getOriginal() {
			return new DamageNumberEF(shooter, Integer.valueOf(((StringViewer)paintScript).WORDS));
		}
		@Override
		public void paint() {
			fadingPaint();
		}
	}
}
