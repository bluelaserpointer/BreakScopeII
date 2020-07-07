package unit;

import calculate.Consumables;
import unit.status.HasStatus;
import unit.status.StatusType;

public interface HasNAStatus<StatusClass extends Consumables> extends HasStatus<StatusType, StatusClass>{

}
