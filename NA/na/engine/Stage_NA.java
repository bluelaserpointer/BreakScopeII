package engine;

import java.util.LinkedList;

import stage.StageSaveData;
import structure.Structure;
import unit.Unit;

public class Stage_NA extends StageSaveData{
	
	private static final long serialVersionUID = -7046067326609502254L;

	public final LinkedList<Unit> UNITS;
	public final LinkedList<Structure> STRUCTURES;

	public Stage_NA(LinkedList<Unit> units, LinkedList<Structure> structures) {
		this.UNITS = units;
		this.STRUCTURES = structures;
	}
}
