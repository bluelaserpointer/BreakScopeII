package liquid;

import java.awt.Color;
import core.GHQ;

public class Smoke extends NALiquidTag {
	public static final Smoke FIXED_SMOKE_TAG = new Smoke();
	@Override
	public Color mainColor(NALiquidState state, double depth, int x, int y) {
		GHQ.random.setSeed(GHQ.nowFrame()/10 + hashCode());
		int darkness = Math.max(0, Math.min(255, 255 - GHQ.random.nextInt(25) - (int)(depth*50.0) - (int)(Math.sin(hashCode() + GHQ.nowFrame()/5)*30.0)));
		return new Color(darkness, darkness, darkness);
	}
	@Override
	public double flowResistance() {
		return 0.5;
	}
	@Override
	public double visibilityWhenGas() {
		return -20.0;
	}
}
