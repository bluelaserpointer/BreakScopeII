package unit.action;

import java.util.List;

import animation.BumpAnimation;
import core.GHQ;
import core.GHQObject;
import damage.NADamage;
import physics.Point;
import preset.unit.Body;
import preset.unit.UnitAction;
import unit.NAUnit;
import unit.body.HumanBody;

public abstract class Punch extends NAAction {
	protected final BumpAnimation bumpAnimation = new BumpAnimation();
	public Punch(Body body) {
		super(body, 100);
	}
	@Override
	public void idle() {
		bumpAnimation.idle();
		super.stopActionIfFramePassed(15);
	}
	////////////////////
	//generator function
	////////////////////
	public void setPunch() {
		if(super.activate()) {
			bumpAnimation.setAnimation(((HumanBody)body()).hands(), ((HumanBody)body()).armAngleBase.angle().get(), 20, 15);
		}
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
		final List<GHQObject> collisionGroup = (List<GHQObject>)GHQ.stage().bulletCollisionGroup.clone();
		for(GHQObject object : collisionGroup) {
			if(object != owner() && object.intersectsRect(object.hitGroup(), punchPoint.intX(), punchPoint.intY(), 15, 15))
				object.damage(new NADamage((((NAUnit)owner()).POW_FLOAT.doubleValue() - 3)*3));
		}
		((NAUnit)owner()).GREEN_BAR.consume(20);
	}
	@Override
	public void stopped() {
		bumpAnimation.resetPosition();
	}
	@Override
	public void overwriteFailed() {
		body().setActionAppointment(this);
	}
	@Override
	public boolean needFixAimAngle() {
		return true;
	}
	@Override
	public boolean canOverwrite(UnitAction action) {
		return action == this || super.canOverwrite(action);
	}
}
