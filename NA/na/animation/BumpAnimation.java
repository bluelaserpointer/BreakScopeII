package animation;

import core.GHQ;
import physics.HasPoint;

public class BumpAnimation extends Animater {
	private HasPoint movingObject;
	private int limitFrame;
	private int generatedFrame;
	private double xSpd;
	private double ySpd;
	private double movedX, movedY;
	@Override
	public void idle() {
		final int passedFrame = GHQ.passedFrame(generatedFrame);
		if(passedFrame > limitFrame) {
			return;
		}else if(passedFrame*2 > limitFrame) {
			movingObject.point().addXY(-xSpd, -ySpd);
			movedX -= xSpd;
			movedY -= ySpd;
		}else {
			movingObject.point().addXY(xSpd, ySpd);
			movedX += xSpd;
			movedY += ySpd;
		}
	}
	@Override
	public void resetPosition() {
		if(movingObject != null) {
			movingObject.point().addXY(-movedX, -movedY);
			movedX = movedY = 0.0;
		}
	}
	public void setAnimation(HasPoint targetObject, double angle, double distance, int halfFrame) {
		resetPosition();
		movingObject = targetObject;
		limitFrame = halfFrame*2;
		generatedFrame = GHQ.nowFrame();
		final double speed = distance/halfFrame;
		xSpd = speed*Math.cos(angle);
		ySpd = speed*Math.sin(angle);
	}
}