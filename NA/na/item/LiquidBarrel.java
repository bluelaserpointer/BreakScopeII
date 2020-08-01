package item;

import damage.NADamage;
import liquid.HasLiquid;
import liquid.Liquid;
import paint.ImageFrame;
import preset.item.ItemData;

public class LiquidBarrel extends NACollisionableItem implements HasLiquid {
	Liquid liquid;
	public LiquidBarrel(Liquid liquid) {
		super(ImageFrame.create("picture/map/OilBarrel3.png"), 100);
		this.liquid = liquid;
		super.amount = 1;
	}
	@Override
	public void killed() {
		if(liquid != null) {
			liquid.expand(point());
			liquid = null;
		}
	}
	@Override
	public double weight() {
		return 10 + liquid.depth()/10.0;
	}
	@Override
	public double damageRes(NADamage damage) {
		return 0;
	}
	@Override
	public boolean stackable(ItemData item) {
		return false;
	}
	@Override
	public boolean supportSerialUse() {
		return true;
	}
	private static final double FLOW_SPEED = 10.0;
	@Override
	public void use() {
		if(liquid == null)
			return;
		divideLiquid(FLOW_SPEED).expand(owner().point());
	}
	@Override
	public Liquid divideLiquid(double depth) {
		if(liquid == null)
			return null;
		final Liquid flow;
		if(liquid.depth() < depth) {
			flow = liquid;
			liquid = null;
		} else {
			flow = liquid.clone();
			flow.directSetDepth(depth);
			liquid.directSetDepth(liquid.depth() - depth);
		}
		return flow;
	}
	@Override
	public Liquid liquid() {
		return liquid;
	}
}
