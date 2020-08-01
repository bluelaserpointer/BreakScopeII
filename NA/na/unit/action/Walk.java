package unit.action;

import core.GHQ;
import engine.NAGame;
import physics.Point;
import physics.direction.Direction8;
import preset.unit.Body;

public abstract class Walk extends NAAction {
	protected double moveSpeed;
	public Walk(Body body) {
		super(body, 100);
	}
	public static class ToTargetWalk extends Walk {
		Point targetPoint;
		public ToTargetWalk(Body body) {
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
	public static class ToDirectionWalk extends Walk {
		Direction8 direction = Direction8.O;
		public ToDirectionWalk(Body body) {
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
		}
		public void setDirection(Direction8 direction, double moveSpeed) {
			this.direction = direction;
			super.moveSpeed = moveSpeed;
			body().stopAction(Dash.class);
			if(direction == Direction8.O) { //stop
				body().stopAction(this);
			} else { //move
				super.activate();
			}
		}
	}
}
