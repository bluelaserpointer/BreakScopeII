package liquid;

import calculate.Damage;
import core.GHQ;
import core.GHQObject;
import engine.NAGame;
import objectAffect.ObjectAffect;
import physics.GridPoint;
import physics.HasGridPoint;
import physics.Point;
import physics.direction.Direction4;
import physics.stage.GridArrayList;
import preset.item.ItemData;
import preset.structure.HasVisibility;
import preset.unit.Unit;
import unit.NAUnit;

public abstract class Liquid implements HasGridPoint, ObjectAffect, HasVisibility {
	protected GridArrayList<Liquid> grids;
	protected GridPoint gridPoint;
	protected double depth;
	protected double depthChange;
	public Liquid(GridArrayList<Liquid> grids) {
		this.grids = grids;
		gridPoint = new GridPoint(grids.gridSize());
	}
	public Liquid(GridArrayList<Liquid> grids, double depth) {
		this.grids = grids;
		gridPoint = new GridPoint(grids.gridSize());
		this.depth = depth;
	}
	//tool
	public Liquid expand(Point point) {
		point().setAll(point);
		expand();
		return this;
	}
	public Liquid expand() {
		final Liquid[] sideLiquids = new Liquid[4];
		double totalDepth = depth;
		int flowCount = 0;
		for(Direction4 direction : Direction4.values()) {
			final int sideXPos = gridPoint().xPos() + direction.x(), sideYPos = gridPoint().yPos() + direction.y();
			if(!grids.inBounds(sideXPos, sideYPos) || GHQ.stage().structures.shapeIntersected_dot(point()))
				continue;
			Liquid sideLiquid = grids.get_cellPos(sideXPos, sideYPos);
			if(this.sameKind(sideLiquid)){ //same kind
				reactWithSame(sideXPos, sideYPos, sideLiquid);
				if(this.depth > sideLiquid.depth + this.flowResistance()*2) { //and shallower depth
					totalDepth += sideLiquid.depth;
				}else
					continue;
			}else { //TODO: react with different kind
				if(!reactWithAnother(sideXPos, sideYPos, sideLiquid))
					continue;
				sideLiquid = grids.get_cellPos(sideXPos, sideYPos);
			}
			sideLiquids[direction.ordinal()] = sideLiquid;
			++flowCount;
		}
		//average surrounding liquids depth
		final double averageDepth = totalDepth/(flowCount + 1);
		for(Direction4 direction : Direction4.values()) {
			final Liquid sideLiquid = sideLiquids[direction.ordinal()];
			if(sideLiquid == null)
				continue;
			final double change = averageDepth - sideLiquid.depth;
			sideLiquid.depthChange += change;
			depthChanged(direction, change);
		}
		this.depth = averageDepth;
		return this;
	}
	
	//main role
	public void idle() {
		paint();
		depth += depthChange;
		depthChange = 0;
	}
	public final void paint() {
		if(NAGame.stage().seenMark().get_stageCod(point().intX(), point().intY(), false)) {
			this.rectPaint(point().intX(), point().intY(), grids.gridSize());
		}
	}
	protected abstract void rectPaint(int x, int y, int size);
	
	//control
	public void directSetDepth(double depth) {
		this.depth = depth;
	}
	public void addDepthChange(double depth) {
		this.depthChange += depth;
	}
	public void remove() {
		grids.set_cellPos(gridPoint.xPos(), gridPoint.yPos(), null);
	}
	//event
	public void affect(GHQObject object) {
	}
	public void damage(Damage damage, Liquid liquid) {}
	public void damage(Damage damage, GHQObject affecter) {}
	public void depthChanged(Direction4 direction, double change) {
		if(Math.abs(change) > 0.05) {
			for(Unit unit : GHQ.stage().units) {
				if(unit.boundingBoxIntersectsDot(point())) {
					final double weight = ((NAUnit)unit).weight();
					unit.point().addSpeed(direction.x()*change/weight, direction.y()*change/weight);
				}
			}
			for(ItemData item : GHQ.stage().items) {
				if(item.boundingBoxIntersectsDot(point())) {
					final double weight = 1; //TODO: real weight upon each item
					item.point().addSpeed(direction.x()*change/weight, direction.y()*change/weight);
				}
			}
		}
	}
	
	//extends
	public boolean reactWithAnother(int xPos, int yPos, Liquid sideLiquid) {
		if(sideLiquid == null || this.depth > sideLiquid.depth + this.flowResistance() + sideLiquid.flowResistance()) {
			sideLiquid = clone();
			sideLiquid.depth = 0.0;
			sideLiquid.gridPoint().setPos(xPos, yPos);
			grids.set_cellPos(xPos, yPos, sideLiquid);
			return true;
		}else
			return false;
	}
	public void reactWithSame(int xPos, int yPos, Liquid sideLiquid) {
	}
	//information
	@Override
	public abstract Liquid clone();
	public double depth() {
		return depth;
	}
	public double depthChange() {
		return depthChange;
	}
	public abstract LiquidState liquidState();
	public double flowResistance() {
		return 1.0;
	}
	public double visibility() {
		return 0.0;
	}
	public boolean sameKind(Liquid target) {
		return target != null && getClass() == target.getClass();
	}
	public GridPoint gridPoint() {
		return gridPoint;
	}
}
