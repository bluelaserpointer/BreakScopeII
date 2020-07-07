package liquid;

import java.awt.Color;

public class Oil extends NALiquidTag {
	public final static Oil FIXED_OIL_TAG = new Oil();
	@Override
	public Color mainColor(NALiquidState state, double depth, int x, int y) {
		return new Color(241 - Math.max(Math.min((int)(depth*2.0), 241), 0),
				169 - Math.max(Math.min((int)(depth*2.0), 169), 0),
				75 - Math.max(Math.min((int)(depth*2.0), 75), 0));
	}
	@Override
	public double flowResistance() {
		return 0.8;
	}
	@Override
	public double visibilityWhenGas() {
		return -3.0;
	}
}
