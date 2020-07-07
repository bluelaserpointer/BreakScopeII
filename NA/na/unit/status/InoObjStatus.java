package unit.status;

import calculate.Consumables;
import calculate.Energy;
import calculate.FixedEnergy;

public abstract class InoObjStatus extends NAStatus<Consumables> {
	final Energy redBar;
	public InoObjStatus(int healthPoint) {
		redBar = new Energy(healthPoint);
	}
	public Consumables status(StatusType type) {
		switch(type) {
		case RED_BAR:
			return redBar;
		default:
			return FixedEnergy.ZERO_FIXED_ENERGY;
		}
	}
}
