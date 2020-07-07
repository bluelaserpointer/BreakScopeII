package liquid;

import java.awt.Color;

import core.GHQ;
import core.GHQObject;

public class Water extends NALiquidTag {
	public final static Water FIXED_WATER_TAG = new Water();
	@Override
	public Color mainColor(NALiquidState state, double depth, int x, int y) {
		switch(state) {
		case GAS:
			GHQ.random.setSeed(GHQ.nowFrame()/10 + hashCode());
			int darkness = Math.max(0, Math.min(255, 255 - GHQ.random.nextInt(25) - (int)(depth*50.0) - (int)(Math.sin(hashCode() + GHQ.nowFrame()/5)*30.0)));
			return new Color(darkness, 255, 255);
		case SOLID:
		case OIL_SOLUABLE:
		case WATER_SOLUABLE:
		default:
			return new Color(0, 255 - Math.max(Math.min((int)(depth*5.0), 254), 0), 255);
		}
	}
	@Override
	public void affect(Liquid liquid, GHQObject object) { //do nothing
	}
	@Override
	public double flowResistance() {
		return 1.0;
	}
	@Override
	public double visibilityWhenGas() {
		return -3.0;
	}
}
