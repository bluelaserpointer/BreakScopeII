package item;

import java.awt.Color;

import core.GHQ;
import engine.NAGame;
import object.HasWeight;
import paint.dot.DotPaint;
import preset.item.ItemData;
import stage.NAStage;
import storage.StorageWithSpace;
import unit.NAUnit;

public abstract class NAItem extends ItemData implements NAUsable, HasWeight {
	double hitPoint = 1;
	public static final NAItem BLANK_ITEM = new NAItem(DotPaint.BLANK_SCRIPT) {
		@Override
		public double weight() {
			return 0;
		}
	};
	public NAItem(DotPaint paint) {
		super(paint);
	}
	public void paintInInventory(int x, int y, int w, int h) {
		super.paintScript.dotPaint_turnAndCapSize(x, y, w, h);
	}
	@Override
	public void idle() {
		super.idle();
		if(!hasOwner()) {
			point().mulSpeed(0.8);
		}
		if(!hasDeleteClaimFromStage() && hitPoint <= 0)
			claimDeleteFromStage();
	}
	@Override
	public void paint() {
		if(((NAStage)GHQ.stage()).playerSightMark().get_stageCod(point().intX(), point().intY(), false)) {
			super.paint();
			if(NAGame.controllingUnit().intersects(this)) {
				GHQ.getG2D(Color.WHITE);
				GHQ.drawString_center(name(), cx(), top(), 10);
			}
		}
	}
	public void interact(NAUnit unit) {
		unit.addItemToStorage(this, true);
	}
	public void reset() {}
	public final void removeFromUnit() {
		((NAUnit)owner).removeItem(this);
	}
	public void removed(StorageWithSpace<ItemData> storage) {}
	
	//information
	@Override
	public NAUnit owner() {
		return (NAUnit)super.owner();
	}
}
