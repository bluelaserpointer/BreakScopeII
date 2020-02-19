package unit.action;

import animation.CircularMotion;
import core.GHQ;
import effect.Effect;
import paint.animation.SerialImageFrame;
import paint.dot.DotPaint;
import paint.dot.DotPaintParameter;
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
	private CircularMotion circularMotion = new CircularMotion();
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
				weaponDisplaySetting.angle += turnAngle;
				break animation;
			}
			passedFrame -= 8;
			if(passedFrame < 4) {
				final double turnAngle = +(Math.PI*5/12)/4;
				circularMotion.setTurnSpeed(turnAngle);
				if(passedFrame < 2) {
					//hand.angle().spin(0.0);
					//weaponDisplaySetting.angle += 0.0;
				}else {
					hand.angle().set(turnAngle*2);
					weaponDisplaySetting.angle += turnAngle*2;
				}
				break animation;
			}
			passedFrame -= 4;
			if(passedFrame < 8) {
				final double turnAngle = -(Math.PI/6)/8;
				circularMotion.setTurnSpeed(turnAngle);
				hand.angle().spin(turnAngle);
				weaponDisplaySetting.angle += turnAngle;
				break animation;
			}
			passedFrame -= 8;
			super.stopAction();
		}
		circularMotion.idle();
	}
	@Override
	public void activated() {
		super.activated();
		GHQ.stage().addEffect(new Effect(owner()) {
			{
				paintScript = slashAnimation;
				limitFrame = 5;
				point().stop();
			}
		});
	}
	@Override
	public void stopped() {
		circularMotion.resetPosition();
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
