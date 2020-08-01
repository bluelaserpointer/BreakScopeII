package liquid;

import java.awt.Color;

import core.GHQ;
import core.GHQObject;

public class Blood extends Water {
	public final static Blood FIXED_BLOOD_TAG = new Blood();
	@Override
	public Color mainColor(NALiquidState state, double depth, int x, int y) {
		switch(state) {
		case GAS:
			GHQ.random.setSeed(GHQ.nowFrame()/10 + hashCode());
			int darkness = Math.max(0, Math.min(255, 255 - GHQ.random.nextInt(25) - (int)(depth*50.0) - (int)(Math.sin(hashCode() + GHQ.nowFrame()/5)*30.0)));
			return new Color(255, darkness, darkness);
		case SOLID:
		case OIL_SOLUABLE:
		case WATER_SOLUABLE:
		default:
			return new Color(255 - Math.max(Math.min((int)(depth*5.0), 254), 0), 0, 0, Math.max(0, Math.min(190 + ((int)depth)*10, 255)));
		}
	}
	@Override
	public void affect(Liquid liquid, GHQObject object) { //do nothing
	}
	@Override
	public double flowResistance() {
		return 5.0;
	}
	@Override
	public double visibilityWhenGas() {
		return -4.0;
	}
}
