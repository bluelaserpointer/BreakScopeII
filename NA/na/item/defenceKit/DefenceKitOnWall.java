package item.defenceKit;

import java.awt.Color;

import core.GHQ;
import paint.dot.DotPaint;
import preset.structure.Tile.TileHitShape;
import structure.NATile;
import ui.HUD;
import unit.NAUnit;

public abstract class DefenceKitOnWall extends DefenceKit {

	//installed position
	protected NATile baseTile;
	protected int baseTilePos;
	
	public DefenceKitOnWall(DotPaint foldedPaint, DotPaint openedPaint) {
		super(foldedPaint, openedPaint);
	}
	@Override
	public void paint() {
		super.paint();
		if(!installed())
			return;
		final TileHitShape tileHitShape = (TileHitShape)baseTile.hitShape();
		tileHitShape.drawBoundingBox(Color.RED, GHQ.stroke1);
	}
	public boolean installToWall(NATile baseTile, int baseTilePos) {
		if(baseTile == null)
			return false;
		this.baseTile = baseTile;
		this.baseTilePos = baseTilePos;
		final TileHitShape tileHitShape = (TileHitShape)baseTile.hitShape();
		if(hasOwner())
			((NAUnit)owner()).removeItem(this);
		drop(tileHitShape.tileX(baseTilePos), tileHitShape.tileY(baseTilePos));
		return true;
		
	}
	@Override
	public boolean installToHUDTarget() {
		return installToWall(HUD.installTargetTile, HUD.installTargetTilePos);
	}
	@Override
	public void uninstall() {
		baseTile = null;
	}
	@Override
	public boolean installed() {
		return baseTile != null;
	}
}
