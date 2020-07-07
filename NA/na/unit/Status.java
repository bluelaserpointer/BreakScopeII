package unit;

import calculate.Consumables;

public abstract class Status<StatusIdentifier, StatusValue extends Consumables> {
	public abstract StatusValue status(StatusIdentifier type);
	public final int intStatus(StatusIdentifier type) {
		return this.status(type).intValue();
	}
	public final double doubleStatus(StatusIdentifier type) {
		return this.status(type).doubleValue();
	}
	public final double consume(StatusIdentifier type, Number number) {
		return this.status(type).consume(number);
	}
	public final void setStatus(StatusIdentifier type, Number number) {
		this.status(type).setNumber(number);
	}
}
