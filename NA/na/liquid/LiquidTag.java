package liquid;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

import calculate.Damage;
import core.GHQObject;
import physics.GridPoint;

public abstract class LiquidTag {
	public void damage(Damage damage, Liquid affecter) {}
	public void damage(Damage damage, GHQObject affecter) {}
	public void affect(Liquid liquid, GHQObject object) {}
	public abstract Color mainColor(NALiquidState state, double depth, int x, int y);
	public void idle(Liquid liquid, GridPoint gridPoint, double depth) {}
	public void additionalPaint(double depth, int x, int y, int size) {}
	public final boolean same(LiquidTag target) {
		return getClass() == target.getClass();
	}
	public LinkedList<LiquidTag> integrateStream(LinkedList<LiquidTag> liquidTags) {
		return integrateStream(liquidTags, new LinkedList<LiquidTag>());
	}
	/**
	 * 
	 * @param liquidTags
	 * @param traversedTags
	 * @param removedTags
	 * @return result state of liquidTags
	 */
	public LinkedList<LiquidTag> integrateStream(LinkedList<LiquidTag> liquidTags, LinkedList<LiquidTag> traversedTags) {
		final Iterator<LiquidTag> iterator = liquidTags.iterator();
		while(iterator.hasNext()) {
			final LiquidTag tag = iterator.next();
			if(tag == this)
				continue;
			if(tag.getClass().equals(getClass())) { //same kind
				iterator.remove();
			}
		}
		traversedTags.add(this);
		return integrateBatonPass(liquidTags, traversedTags);
	}
	protected final LinkedList<LiquidTag> integrateBatonPass(LinkedList<LiquidTag> liquidTags, LinkedList<LiquidTag> traversedTags) {
		for(LiquidTag tag : liquidTags) {
			if(!traversedTags.contains(tag)) {
				return tag.integrateStream(liquidTags, traversedTags);
			}
		}
		return liquidTags;
	}
	public abstract double flowResistance();
	public abstract double visibility(Liquid liquid);
}
