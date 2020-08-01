package unit.action;

import physics.direction.Direction8;
import preset.unit.Body;
import unit.NAUnit;

public class Rolling extends NAAction {
	double strength; //roll distance
	double angle; //angle
	public Rolling(Body body) {
		super(body, 200);
	}
	@Override
	public void idle() {
		super.stopActionIfFramePassed(15);
	}
	public void setRolling(double strength, double angle) {
		if(super.isActivated())
			return;
		this.strength = strength;
		this.angle = angle;
		super.activate();
	}
	public void setRolling(double strength, Direction8 direction) {
		if(direction != Direction8.O)
			setRolling(strength, direction.angle());
	}
	@Override
	public void activated() {
		super.activated();
		owner().point().addSpeed(strength*Math.cos(angle), strength*Math.sin(angle));
		((NAUnit)owner()).GREEN_BAR.consume(25.0);
	}
}
