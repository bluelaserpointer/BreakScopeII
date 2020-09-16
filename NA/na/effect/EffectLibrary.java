package effect;

import static java.lang.Math.PI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import core.GHQ;
import core.GHQObject;
import engine.NAGame;
import liquid.Blood;
import liquid.NALiquidState;
import paint.animation.SerialImageFrame;
import paint.dot.DotPaint;
import paint.text.StringPaint;
import preset.effect.Effect;

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
	//BulletLineEF
	/////////////////
	public static class BulletLineEF extends EffectLibrary {
		final int delay, x1, y1, x2, y2;
		private static final int ANIMATION_LENGTH = 10, STROKE = 3;
		final Color alphaWhite = new Color(255, 255, 255, 100);
		public BulletLineEF(GHQObject shooter, int delay, int x1, int y1, int x2, int y2) {
			super(shooter);
			point().stop();
			point().setXY(x1, y1);
			this.delay = delay;
			this.limitFrame = delay + ANIMATION_LENGTH;
			this.x1 = x1;this.y1 = y1;
			this.x2 = x2;this.y2 = y2;
		}
		@Override
		public void paint() {
			if(super.passedFrame() < delay) {
				GHQ.getG2D(Color.WHITE, new BasicStroke(STROKE)).drawLine(x1, y1, x2, y2);
			} else {
				GHQ.getG2D(Color.WHITE, new BasicStroke((int)(STROKE*(1.0 - (double)(super.passedFrame() - delay)/(double)ANIMATION_LENGTH)))).drawLine(x1, y1, x2, y2);
			}
		}
	}
	/////////////////
	//FireEF
	/////////////////
	public static class FireEF extends EffectLibrary {
		private double angleSpeed;
		public FireEF(GHQObject source, double angleSpeed, double initialSpeed) {
			super(source);
			point().addXY_allowsAngle(0, 40, source.angle().get());
			angle().set(source.angle().get() + 0.1*(Math.random() - 0.5));
			this.angleSpeed = angleSpeed;
			point().setSpeed(initialSpeed);
			this.limitFrame = 20;
		}
		@Override
		public void paint() {
			GHQ.getG2D(Color.WHITE).fillOval(point().intX() - 2, point().intY() - 2, 4, 4);
			point().mulSpeed(0.76);
			angle().set(angle().get() + angleSpeed);
			angleSpeed *= 1.1;
			point().setMoveAngle(angle());
		}
	}
	/////////////////
	//FiredSmokeEF
	/////////////////
	public static class FiredSmokeEF extends EffectLibrary {
		public FiredSmokeEF(GHQObject source) {
			super(source);
			point().addXY_allowsAngle(0, 40, source.angle().get());
			angle().set(source.angle().get() + 0.1*(Math.random() - 0.5));
			point().setSpeed(5.0*(Math.random() - 0.5));
			this.limitFrame = 20;
		}
		@Override
		public void paint() {
			GHQ.getG2D(Color.WHITE).drawOval(point().intX() - 1, point().intY() - 1, 2, 2);
			point().mulSpeed(0.76);
		}
	}
	/////////////////
	//HitWallEF
	/////////////////
	public static class HitWallEF extends EffectLibrary {
		private double angleSpeed;
		public HitWallEF(GHQObject source, double targetAngle) {
			super(source);
			angle().set(targetAngle + 0.1*(Math.random() - 0.5));
			this.angleSpeed = Math.toRadians(10.0)*(Math.random() - 0.5);
			point().setSpeed(10 + Math.random()*15);
			this.limitFrame = 20;
		}
		@Override
		public void paint() {
			GHQ.getG2D(Color.YELLOW).fillOval(point().intX() - 1, point().intY() - 1, 2, 2);
			point().mulSpeed(0.76);
			angle().set(angle().get() + angleSpeed);
			angleSpeed *= 1.1;
			point().setMoveAngle(angle());
		}
	}
	/////////////////
	//HitCreatureEF
	/////////////////
	public static class HitCreatureEF extends EffectLibrary {
		private double angleSpeed;
		public HitCreatureEF(GHQObject source, double targetAngle) {
			super(source);
			angle().set(targetAngle + 0.1*(Math.random() - 0.5));
			this.angleSpeed = Math.toRadians(10.0)*(Math.random() - 0.5);
			point().setSpeed(10 + Math.random()*15);
			this.limitFrame = 20;
		}
		@Override
		public void paint() {
			GHQ.getG2D(Color.RED).fillOval(point().intX() - 1, point().intY() - 1, 2, 2);
			point().mulSpeed(0.76);
			angle().set(angle().get() + angleSpeed);
			angleSpeed *= 1.1;
			point().setMoveAngle(angle());
		}
		@Override
		public boolean outOfLifeSpanProcess() {
			if(Math.random() < 0.005)
				NAGame.stage().addLiquid(point(), Blood.FIXED_BLOOD_TAG, NALiquidState.WATER_SOLUABLE, 0.1);
			return super.outOfLifeSpanProcess();
		}
	}
	/////////////////
	//PenetratedEF
	/////////////////
	public static class PenetratedEF extends EffectLibrary {
		public PenetratedEF(GHQObject source) {
			super(source);
			this.limitFrame = 15;
			point().setSpeed_DA(15.0 + 5.0*Math.random(), source.point().moveAngle() - Math.PI + 0.5*(Math.random() - 0.5));
		}
		@Override
		public void paint() {
			final double rate = 1.0 - (double)super.passedFrame()/limitFrame;
			GHQ.getG2D(new Color(255, 255, 0, (int)(255*rate)), GHQ.stroke1).drawLine(point().intX(), point().intY(),
					point().intX() + (int)(point().xSpeed()*3*rate), point().intY() + (int)(point().ySpeed()*3*rate));
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
			return new DamageNumberEF(shooter, ((StringPaint)paintScript).words, ((StringPaint)paintScript).font, ((StringPaint)paintScript).color);
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
