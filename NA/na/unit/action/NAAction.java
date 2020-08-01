package unit.action;

import core.GHQ;
import preset.unit.Body;
import preset.unit.UnitAction;

/**
 * Adds overWriteLevel and doAppointment to original UnitAction.
 * @author bluelaserpointer
 *
 */
public abstract class NAAction extends UnitAction {
	private final int overWriteLevel;
	public NAAction(Body body, int overWriteLevel) {
		super(body);
		this.overWriteLevel = overWriteLevel;
	}
	@Override
	public boolean canOverwrite(UnitAction action) {
		return overWriteLevel > ((NAAction)action).overWriteLevel;
	}
	public final boolean stopActionIfFramePassed(int frameCount) {
		if(GHQ.passedFrame(initialFrame) > frameCount) {
			body().stopAction(this);
			return true;
		}
		return false;
	}
	public boolean needFixAimAngle() {
		return false;
	}
}
