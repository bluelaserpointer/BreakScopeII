package unit.status;

import calculate.Consumables;
import unit.Status;

public interface HasStatus<StatusIdentifier, StatusValue extends Consumables> {
	public abstract Status<StatusIdentifier, StatusValue> status();
	public default Consumables status(StatusIdentifier type) {
		return status().status(type);
	}
	public default int intStatus(StatusIdentifier type) {
		return status().intStatus(type);
	}
	public default double doubleStatus(StatusIdentifier type) {
		return status().doubleStatus(type);
	}
	public default double consume(StatusIdentifier type, Number number) {
		return status().consume(type, number);
	}
	public default void setStatus(StatusIdentifier type, Number number) {
		status().setStatus(type, number);
	}
}
