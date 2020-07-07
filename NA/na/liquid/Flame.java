package liquid;

import java.awt.Color;

import calculate.Damage;
import core.GHQ;
import core.GHQObject;
import damage.DamageMaterial;
import engine.NAGame;
import physics.GridPoint;
import unit.NAUnit;

public class Flame extends NALiquidTag {
	public static final Flame FIXED_FLAME_TAG = new Flame();
	@Override
	public void damage(Damage damage, GHQObject affecter) {
	}
	@Override
	public void affect(Liquid liquid, GHQObject object) {
		if(GHQ.nowFrame() % 5 == 0 && object instanceof NAUnit) {
			((NAUnit)object).damage(DamageMaterial.Heat.makeDamage(liquid.depth()*super.effectRate(liquid)*5));
			liquid.depth += -((NAUnit)object).RED_BAR.lastSetDiff();
		}
	}
	@Override
	public void idle(Liquid liquid, GridPoint gridPoint, double depth) {
		if(true) {
			final Liquid bottomLiquid = NAGame.stage().liquidGrids().get_cellPos(gridPoint.xPos(), gridPoint.yPos());
			if(bottomLiquid != null)
				bottomLiquid.damage(DamageMaterial.Heat.makeDamage(depth), liquid);
		}
	}
	@Override
	public Color mainColor(NALiquidState state, double depth, int x, int y) {
		switch(state) {
		case GAS:
		case OIL_SOLUABLE:
		case SOLID:
		case WATER_SOLUABLE:
		default:
			GHQ.random.setSeed(GHQ.nowFrame()/10 + (((x + 1)*(y + 1)) % 10));
			int darkness = Math.max(0, Math.min(255, 255 + (int)(Math.sin(GHQ.random.nextDouble()*Math.PI*2 + GHQ.nowFrame()/30*depth)*60.0) - (int)(255/(1.0 + depth*0.1))));
			return new Color(255, Math.min(255, darkness*2), Math.max(0, 2*(darkness - 128)));
		}
	}
	@Override
	public double flowResistance() {
		return 1.0;
	}
	@Override
	public double visibilityWhenGas() {
		return -1.0;
	}
}
