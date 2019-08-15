package buff;

import calculate.FixedSetter;
import unit.BasicUnit;
import unit.Unit;

public class ToughnessBroke extends Buff{
	private static final long serialVersionUID = -3341059350704317027L;
	private final FixedSetter zeroSetter = new FixedSetter(0);
	public ToughnessBroke(Unit owner) {
		super(owner);
		((BasicUnit)owner).SPEED_PPS.getValueFormula().append(zeroSetter);
	}

	@Override
	public void idle() {
		final BasicUnit UNIT = (BasicUnit)owner;
		if(UNIT.TOUGHNESS.isMax()) {
			removed();
		}
	}
	
	@Override
	public void removed() {
		((BasicUnit)owner).removeBuff(this);
		((BasicUnit)owner).SPEED_PPS.getValueFormula().delete(zeroSetter);
	}
}
