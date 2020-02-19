package unit;

import engine.NAGame;

public enum UnitGroup {
	SCIENTIST, GUARD, PRISONER, CAPITALIST, MONSTER, INVALID;
	
	public static final int GROUP_AMOUNT = 6;
	private final int groupBaseFavor[] = new int[GROUP_AMOUNT];
	private final int groupFavorDiffToPlayer[] = new int[GROUP_AMOUNT];

	static {
		//Scientist
		SCIENTIST.groupBaseFavor[SCIENTIST.ordinal()] = 50;
		SCIENTIST.groupBaseFavor[GUARD.ordinal()] = -20;
		SCIENTIST.groupBaseFavor[PRISONER.ordinal()] = 0;
		SCIENTIST.groupBaseFavor[CAPITALIST.ordinal()] = 0;
		SCIENTIST.groupBaseFavor[MONSTER.ordinal()] = -100;
		SCIENTIST.groupBaseFavor[INVALID.ordinal()] = 0;
		//Guard
		GUARD.groupBaseFavor[SCIENTIST.ordinal()] = 20;
		GUARD.groupBaseFavor[GUARD.ordinal()] = 50;
		GUARD.groupBaseFavor[PRISONER.ordinal()] = -100;
		GUARD.groupBaseFavor[CAPITALIST.ordinal()] = 0;
		GUARD.groupBaseFavor[MONSTER.ordinal()] = -100;
		GUARD.groupBaseFavor[INVALID.ordinal()] = 0;
		//Prisoner
		PRISONER.groupBaseFavor[SCIENTIST.ordinal()] = 0;
		PRISONER.groupBaseFavor[GUARD.ordinal()] = -20;
		PRISONER.groupBaseFavor[PRISONER.ordinal()] = 50;
		PRISONER.groupBaseFavor[CAPITALIST.ordinal()] = 0;
		PRISONER.groupBaseFavor[MONSTER.ordinal()] = -100;
		PRISONER.groupBaseFavor[INVALID.ordinal()] = 0;
		//Capitalist
		CAPITALIST.groupBaseFavor[SCIENTIST.ordinal()] = 0;
		CAPITALIST.groupBaseFavor[GUARD.ordinal()] = -100;
		CAPITALIST.groupBaseFavor[PRISONER.ordinal()] = -100;
		CAPITALIST.groupBaseFavor[CAPITALIST.ordinal()] = 50;
		CAPITALIST.groupBaseFavor[MONSTER.ordinal()] = -100;
		CAPITALIST.groupBaseFavor[INVALID.ordinal()] = 0;
		//Monster
		for(int i = 0; i < GROUP_AMOUNT; ++i)
			MONSTER.groupBaseFavor[i] = -100;
		//Invalid
		for(int i = 0; i < GROUP_AMOUNT; ++i)
			INVALID.groupBaseFavor[i] = 0;
	}
	public int groupFavorTo(NAUnit unit) {
		final UnitGroup GROUP = unit.unitGroup();
		if(unit == NAGame.controllingUnit())
			return groupBaseFavor[GROUP.ordinal()] + groupFavorDiffToPlayer[GROUP.ordinal()];
		return groupBaseFavor[GROUP.ordinal()];
	}
}
