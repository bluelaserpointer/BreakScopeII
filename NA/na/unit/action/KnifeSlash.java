package unit.action;

import animation.CircularMotion;
import core.GHQ;
import effect.Effect;
import paint.animation.SerialImageFrame;
import paint.dot.DotPaint;
import paint.dot.DotPaintParameter;
import physics.Dynam;
import physics.RelativePoint;
import unit.Body;
import unit.BodyParts;

public abstract class KnifeSlash extends NAAction {
	private final DotPaint slashAnimation = new SerialImageFrame(1,
			"picture/animations/KnifeSlash_1.png",
			"picture/animations/KnifeSlash_2.png",
			"picture/animations/KnifeSlash_3.png",
			"picture/animations/KnifeSlash_3.png",
			"picture/animations/KnifeSlash_3.png");
	private BodyParts hand;
	protected CircularMotion circularMotion = new CircularMotion();
	private DotPaintParameter weaponDisplaySetting;
	public KnifeSlash(Body body) {
		super(body, 100);
	}
	@Override
	public void idle() {
		int passedFrame = GHQ.passedFrame(initialFrame);
		animation: {
			if(passedFrame < 8) {
				final double turnAngle = -(Math.PI/4)/8;
				circularMotion.setTurnSpeed(turnAngle);
				hand.angle().spin(turnAngle);
				weaponDisplaySetting.angle.spin(turnAngle);
				break animation;
			}
			passedFrame -= 8;
			if(passedFrame < 4) {
				if(passedFrame == 2) { //generate effect
					GHQ.stage().addEffect(new Effect(owner()) {
						{
							paintScript = slashAnimation;
							limitFrame = 5;
							physics().setPoint(new RelativePoint(owner(), new Dynam(), false));
							point().setSpeed(200);
						}
						@Override
						public void paint() {
							GHQ.setImageAlpha((float)(1.0 - (double)GHQ.passedFrame(initialFrame)/limitFrame));
							paintScript.dotPaint_turn(point(), owner().angle().get());
							GHQ.setImageAlpha();
						}
					});
				}
				final double turnAngle = +(Math.PI*5/12)/4;
				circularMotion.setTurnSpeed(turnAngle);
				if(passedFrame < 2) {
					//hand.angle().spin(0.0);
					//weaponDisplaySetting.angle += 0.0;
				}else {
					hand.angle().set(turnAngle*2);
					weaponDisplaySetting.angle.spin(turnAngle*2);
				}
				break animation;
			}
			passedFrame -= 4;
			if(passedFrame < 8) {
				final double turnAngle = -(Math.PI/6)/8;
				circularMotion.setTurnSpeed(turnAngle);
				hand.angle().spin(turnAngle);
				weaponDisplaySetting.angle.spin(turnAngle);
				break animation;
			}
			passedFrame -= 8;
			super.stopAction();
			return;
		}
		circularMotion.idle();
	}
	@Override
	public void stopped() {
		circularMotion.resetPosition();
	}
	@Override
	public boolean needFixAimAngle() {
		return true;
	}
	public abstract void setSlash();
	public void setSlash(BodyParts hand, double rotateRadius, DotPaintParameter weaponDisplaySetting) {
		if(super.activate()) {
			this.hand = hand;
			this.weaponDisplaySetting = weaponDisplaySetting;
			circularMotion.setMovingObject(hand).setRadius(rotateRadius).setCurrentAngle(hand.owner().angle().get());
		}
	}
}
