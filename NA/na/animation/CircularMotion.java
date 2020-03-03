package animation;

import physics.HasPoint;
import physics.Point;

public class CircularMotion extends Animater {
	private HasPoint movingObject;
	private double radius, currentAngle;
	private double turnSpeed;
	private double movedXCount, movedYCount;
	public CircularMotion() {
		movingObject = HasPoint.NULL_HAS_POINT;
	}
	public CircularMotion(HasPoint movingObject) {
		this.movingObject = movingObject;
	}
	public CircularMotion(Point basePoint, HasPoint movingObject) {
		this.movingObject = movingObject;
		setBasePoint(basePoint);
	}
	public CircularMotion(int basePointX, int basePointY, HasPoint movingObject) {
		this.movingObject = movingObject;
		setBasePoint(basePointX, basePointY);
	}
	//main role
	@Override
	public void idle() {
		final double newAngle = currentAngle + turnSpeed;
		final double oldDX = radius*Math.cos(currentAngle), oldDY = radius*Math.sin(currentAngle);
		final double newDX = radius*Math.cos(newAngle), newDY = radius*Math.sin(newAngle);
		final double moveX = newDX - oldDX, moveY = newDY - oldDY;
		movingObject.point().addXY(moveX, moveY);
		movedXCount += moveX;
		movedYCount += moveY;
		currentAngle = newAngle;
	}
	//control
	public void resetMoveCount() {
		movedXCount = movedYCount = 0.0;
	}
	@Override
	public void resetPosition() {
		//System.out.println("movedXCount: " + movedXCount + ",movedYCount: " + movedYCount);
		movingObject.point().addXY(-movedXCount, -movedYCount);
		resetMoveCount();
	}
	public CircularMotion setMovingObject(HasPoint object) {
		movingObject = object;
		return this;
	}
	public CircularMotion setBasePoint(HasPoint object) {
		return setBasePoint(object.point());
	}
	public CircularMotion setBasePoint(Point point) {
		setCurrentAngle(point.angleTo(movingObject));
		setRadius(point.distance(movingObject));
		return this;
	}
	public CircularMotion setBasePoint(int x, int y) {
		return setBasePoint(new Point.IntPoint(x, y));
	}
	public CircularMotion setCurrentAngle(double angle) {
		currentAngle = angle;
		return this;
	}
	public CircularMotion setRadius(double radius) {
		this.radius = radius;
		return this;
	}
	public CircularMotion setTurnSpeed(double speed) {
		turnSpeed = speed;
		return this;
	}
	//information
	public HasPoint movingObject() {
		return movingObject;
	}
	public double currentAngle() {
		return currentAngle;
	}
	public double radius() {
		return radius;
	}
	public double turnSpeed() {
		return turnSpeed;
	}
	public double movedXCount() {
		return movedXCount;
	}
	public double movedYCount() {
		return movedYCount;
	}
}