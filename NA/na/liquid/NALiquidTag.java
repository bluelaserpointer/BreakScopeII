package liquid;

public abstract class NALiquidTag extends LiquidTag {
	@Override
	public double visibility(Liquid liquid) {
		return liquid.liquidState().equals(NALiquidState.GAS) ? visibilityWhenGas() : 0.0;
	}
	public abstract double visibilityWhenGas();
	protected final double effectRate(Liquid liquid) {
		if(liquid instanceof MixedLiquid) {
			return 1.0/((MixedLiquid)liquid).liquidTagAmount();
		}else
			return 1.0;
	}
}
