package unit.action;

import core.GHQ;
import engine.NAGame;
import physics.Point;
import physics.direction.Direction8;
import preset.unit.Body;
import unit.NAUnit;

public abstract class Dash extends NAAction {
	protected double moveSpeed;
	public Dash(Body body) {
		super(body, 105);
	}
	public static class ToTargetDash extends Dash {
		Point targetPoint;
		public ToTargetDash(Body body) {
			super(body);
		}
		@Override
		public void idle() {
			final double RATE = moveSpeed/owner().point().distance(targetPoint);
			final double X = owner().point().doubleDX(targetPoint)*RATE, Y = owner().point().doubleDY(targetPoint)*RATE;
			owner().point().addXY(X, Y);
			NAGame.addViewStack(X, Y);
			if(owner().point().distance(targetPoint) < 0.5)
				body().stopAction(this);
		}
		public void setTargetPoint(Point point, double moveSpeed) {
			targetPoint = point;
			super.moveSpeed = moveSpeed;
			if(!super.isActivated())
				body().action(this);
		}
	}
	public static class ToDirectionDash extends Dash {
		Direction8 direction = Direction8.O;
		public ToDirectionDash(Body body) {
			super(body);
		}
		@Override
		public void idle() {
			final double X = direction.x()*moveSpeed, Y = direction.y()*moveSpeed;
			if(!GHQ.stage().hitObstacle_atNewPoint(owner(), X, Y)) {
				owner().point().addXY(X, Y);
				NAGame.addViewStack(X, Y);
			} else if(!GHQ.stage().hitObstacle_atNewPoint(owner(), X, 0)) {
				owner().point().addXY(X, 0);
				NAGame.addViewStack(X, 0);
			} else if(!GHQ.stage().hitObstacle_atNewPoint(owner(), 0, Y)) {
				owner().point().addXY(0, Y);
				NAGame.addViewStack(0, Y);
			}

			((NAUnit)owner()).GREEN_BAR.consume(GHQ.getSPF()*15.0);
		}
		public void setDirection(Direction8 direction, double moveSpeed) {
			this.direction = direction;
			super.moveSpeed = moveSpeed;
			if(direction == Direction8.O) { //stop
				body().stopAction(this);
			} else { //move
				super.activate();
			}
		}
	}
}
