package unit;

import static java.lang.Math.PI;

import java.awt.Color;

import core.GHQ;
import effect.Effect;
import paint.DotPaint;
import paint.ImageFrame;
import paint.StringViewer;
import physics.HasDynam;

public abstract class EffectLibrary extends Effect{
	public static void loadResource() {
		SparkHitEF.paint = ImageFrame.createNew("thhimage/NarrowSpark_HitEffect.png");
	}
	
	public EffectLibrary(HasDynam source) {
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
		private static DotPaint paint;
		public SparkHitEF(HasDynam source) {
			super(source);
			name = "SparkHitEF";
			limitFrame = 3;
			paintScript = paint;
			dynam.stop();
			dynam.setMoveAngle(GHQ.random2(0, 2*PI));
		}
		@Override
		public SparkHitEF getOriginal() {
			return new SparkHitEF(SHOOTER);
		}
	}
	/////////////////
	//DamageNumberEF
	/////////////////
	public static class DamageNumberEF extends EffectLibrary{
		public DamageNumberEF(HasDynam source, int damage) {
			super(source);
			name = "DamageNumberEF";
			limitFrame = 15;
			paintScript = new StringViewer(String.valueOf(damage), Color.RED);
			dynam.stop();
			dynam.setMoveAngle(0);
		}
		@Override
		public DamageNumberEF getOriginal() {
			return new DamageNumberEF(SHOOTER, Integer.valueOf(((StringViewer)paintScript).WORDS));
		}
		@Override
		public void paint() {
			fadingPaint();
		}
	}
}
