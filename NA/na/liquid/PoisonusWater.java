package liquid;

import java.awt.Color;
import java.util.LinkedList;

import core.GHQ;
import core.GHQObject;
import damage.DamageComposition;
import damage.DamageMaterial;
import unit.NAUnit;

public class PoisonusWater extends Water {
	protected double strength;
	public PoisonusWater(double strength) {
		this.strength = strength;
	}
	@Override
	public Color mainColor(NALiquidState state, double depth, int x, int y) {
		return new Color(0, 255 - Math.max(Math.min((int)(depth*5.0), 254), 0), 0);
	}
	@Override
	public LinkedList<LiquidTag> integrateStream(LinkedList<LiquidTag> liquidTags, LinkedList<LiquidTag> traversedTags) {
		LiquidTag waitingDeleteTag = null;
		for(LiquidTag tag : liquidTags) {
			if(tag == this)
				continue;
			if(tag instanceof PoisonusWater) { //same kind
				waitingDeleteTag = strength < ((PoisonusWater)tag).strength ? this : tag;
			}
		}
		if(waitingDeleteTag != null)
			liquidTags.remove(waitingDeleteTag);
		traversedTags.add(this);
		return integrateBatonPass(liquidTags, traversedTags);
	}
	@Override
	public void affect(Liquid liquid, GHQObject object) {
		if(liquid.liquidState().equals(NALiquidState.GAS) && object instanceof NAUnit && (hashCode() + GHQ.nowFrame()) % 20 == 0) {
			((NAUnit)object).damage(DamageMaterial.Poi.makeDamage(liquid.depth()*super.effectRate(liquid)*10, DamageComposition.OUTSIDE).setDoSurpriseAttack(false));
		}
	}
}
