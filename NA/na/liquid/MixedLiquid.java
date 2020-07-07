package liquid;

import java.awt.Color;
import java.util.LinkedList;

import calculate.Damage;
import core.GHQ;
import core.GHQObject;
import damage.DamageComposition;
import damage.NADamage;
import engine.NAGame;
import physics.Direction.Direction4;
import stage.GridArrayList;

public class MixedLiquid extends Liquid {
	protected LinkedList<LiquidTag> liquidTags;
	protected int reactFrame = -1;
	protected NALiquidState state;
	public MixedLiquid(MixedLiquid sample) {
		super(sample.grids, sample.depth);
		liquidTags = new LinkedList<LiquidTag>(sample.liquidTags);
		state = sample.state;
	}
	public MixedLiquid(GridArrayList<Liquid> grids, NALiquidState state, double depth, LiquidTag... liquidTags) {
		super(grids, depth);
		this.liquidTags = new LinkedList<LiquidTag>();
		for(LiquidTag tag : liquidTags)
			this.liquidTags.add(tag);
		this.state = state;
	}
	@Override
	public void rectPaint(int x, int y, int size) {
		//paint mixed color
		int sumR = 0, sumG = 0, sumB = 0;
		for(LiquidTag tag : liquidTags) {
			final Color color = tag.mainColor(state, depth, x, y);
			sumR += color.getRed();
			sumG += color.getGreen();
			sumB += color.getBlue();
		}
		final int total = liquidTags.size();
		paintMixedColor(new Color(sumR/total, sumG/total, sumB/total), x, y, size);
		//paint additional components
		for(LiquidTag action : liquidTags) {
			action.additionalPaint(depth, x, y, size);
		}
	}
	protected void paintMixedColor(Color color, int x, int y, int size) {
		switch(state) {
		case GAS:
			GHQ.random.setSeed(GHQ.nowFrame()/10 + hashCode());
			for(int i = 0; i < 3; ++i) {
				final int particleW = 25 + GHQ.random.nextInt(10), particleH = 25 - GHQ.random.nextInt(10);
				GHQ.getG2D(color)
					.fillRect(x + GHQ.random.nextInt(size) - particleW, y + GHQ.random.nextInt(size) - particleH, particleW, particleH);
			}
			break;
		case OIL_SOLUABLE:
		case SOLID:
		case WATER_SOLUABLE:
		default:
			GHQ.getG2D(color).fillRect(x - size/2, y - size/2, size, size);
			break;
		}
	}
	protected static final int REACT_SPAN = 5;
	@Override
	public void idle() {
		super.idle();
		if(GHQ.nowFrame() == reactFrame) {
			for(Direction4 direction : Direction4.values()) {
				int xPos = gridPoint().xPos() + direction.x(), yPos = gridPoint().yPos() + direction.y();
				final Liquid sideLiquid = grids.get_cellPos(xPos, yPos);
				if(sideLiquid != null && sideLiquid.liquidState() == state) {
					final MixedLiquid liquid = (MixedLiquid)sideLiquid;
					//direct union
					for(LiquidTag tag : liquid.liquidTags) {
						if(liquid.liquidState() == NALiquidState.GAS && tag instanceof Water)
							continue;
						if(!liquidTags.contains(tag))
							liquidTags.add(tag);
					}
					//integrate
					if(!liquidTags.isEmpty()) {
						liquid.liquidTags = liquidTags.getFirst().integrateStream(liquidTags);
					}
					liquid.reactFrame = GHQ.nowFrame() + REACT_SPAN;
				}
			}
		}
		for(LiquidTag tag : liquidTags)
			tag.idle(this, gridPoint(), depth);
		if(state == NALiquidState.GAS) {
			if((depth -= 0.04*Math.random()) < 0)
				super.remove();
		}
	}
	@Override
	public void affect(GHQObject object) {
		for(LiquidTag tag : liquidTags)
			tag.affect(this, object);
	}
	@Override
	public void damage(Damage damage, Liquid affecter) {
		for(DamageComposition composition : ((NADamage)damage).compositions()) {
			if(composition.material().isHeat()) {
				double dmg;
				switch(state) {
				case WATER_SOLUABLE: //distinguish flame
					for(LiquidTag tag : liquidTags)
						tag.damage(damage, affecter);
					dmg = composition.damage();
					depth -= dmg;
					affecter.depth -= dmg;
					new MixedLiquid(NAGame.stage().gasGrids(), NALiquidState.GAS, dmg, liquidTags.toArray(new LiquidTag[0]))
						.expand(gridPoint);
					if(depth + depthChange <= 0)
						remove();
					break;
				case OIL_SOLUABLE: //rich flame
					for(LiquidTag tag : liquidTags)
						tag.damage(damage, affecter);
					dmg = Math.min(1, composition.damage());
					final double fireChange = Math.min(depth, dmg);
					depth -= dmg;
					affecter.depth += fireChange;
					if(depth + depthChange <= 0)
						remove();
					break;
				case GAS: //do nothing
				default:
					break;
				}
			}
		}
	}
	@Override
	public void damage(Damage damage, GHQObject affecter) { //TODO
		//for(LiquidTag tag : liquidTags)
			//tag.damage(damage, affecter);
	}
	@Override
	public void depthChanged(Direction4 direction, double change) {
		if(state == NALiquidState.GAS)
			change /= 10;
		super.depthChanged(direction, change);
	}
	@Override
	public MixedLiquid clone() {
		final MixedLiquid mixedLiquid = new MixedLiquid(this);
		mixedLiquid.reactFrame = GHQ.nowFrame() + REACT_SPAN;
		return mixedLiquid;
	}
	public LinkedList<LiquidTag> liquidTags() {
		return liquidTags;
	}
	public int liquidTagAmount() {
		return liquidTags.size();
	}
	public void addLiquidTag(LiquidTag tag) {
		liquidTags.add(tag);
	}
	@Override
	public LiquidState liquidState() {
		return state;
	}
	public double flowResistance() {
		double sum = 0.0;
		for(LiquidTag tag : liquidTags) {
			sum += tag.flowResistance();
		}
		return sum;
	}
	@Override
	public double visibility() {
		if(state == NALiquidState.GAS) {
			double worstVisibility = 0.0;
			for(LiquidTag tag : liquidTags) {
				final double visibility = tag.visibility(this);
				if(worstVisibility > visibility)
					worstVisibility = visibility;
			}
			return worstVisibility;
		}else
			return 0.0;
	}
	@Override
	public boolean sameKind(Liquid target) {
		return super.sameKind(target) && liquidState() == target.liquidState();
	}
}
