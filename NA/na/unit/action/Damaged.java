package unit.action;

import unit.Body;

public class Damaged extends NAAction {
	public Damaged(Body body) {
		super(body, 200);
	}
	@Override
	public void idle() {
		super.stopActionIfFramePassed(2);
	}
	public void set() {
		super.activate();
	}
	@Override
	public boolean precondition() {
		return true;
	}
}
