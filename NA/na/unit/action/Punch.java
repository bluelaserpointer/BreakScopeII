package unit.action;

import core.GHQ;
import damage.DamageMaterialType;
import damage.NADamage;
import physics.Point;
import unit.Body;
import unit.NAUnit;
import unit.Unit;

public abstract class Punch extends NAAction {
	public Punch(Body body) {
		super(body, 100);
	}
	@Override
	public void idle() {
		super.stopActionIfFramePassed(10);
	}
	////////////////////
	//generator function
	////////////////////
	public void setPunch() {
		super.activate();
	}
	@Override
	public boolean precondition() {
		return GHQ.passedFrame(initialFrame) > 5 && ((NAUnit)owner()).GREEN_BAR.doubleValue() >= 20.0;
	}
	@Override
	public void activated() {
		super.activated();
		//dmg: (POW_FLOAT - 3)*3
		//stamina: -20p
		final Point punchPoint = new Point.IntPoint(point());
		punchPoint.addXY_DA(20, angle().get());
		for(Unit unit : GHQ.stage().units) {
			if(unit != owner() && unit.intersectsRect(punchPoint.intX(), punchPoint.intY(), 15, 15))
				unit.damage(new NADamage((((NAUnit)owner()).POW_FLOAT.doubleValue() - 3)*3, DamageMaterialType.Phy));
		}
		((NAUnit)owner()).GREEN_BAR.consume(20);
	}
	@Override
	public void overwriteFailed() {
		body().setActionAppointment(this);
	}
}
