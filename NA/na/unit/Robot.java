package unit;

import physics.Angle;
import physics.Point;
import talent.AllUp;

public class Robot extends NAUnit {
	public Robot() {
		super(25);
		POW_FIXED.setMax(15).setToMax();
		this.addTalent(new AllUp(this));
		this.addTalent(new AllUp(this));
	}
	@Override
	public Point point() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Angle angle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UnitGroup unitGroup() {
		// TODO Auto-generated method stub
		return null;
	}

}
