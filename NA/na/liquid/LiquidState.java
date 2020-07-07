package liquid;

public interface LiquidState {
	public static final LiquidState NULL_LIQUID_STATE = new LiquidState() {};
	public default boolean equals(LiquidState liquidState) {
		return this == liquidState;
	}
}
