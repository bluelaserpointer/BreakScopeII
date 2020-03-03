package item;

import core.GHQ;
import paint.dot.DotPaint;
import stage.NAStage;
import storage.Storage;
import unit.NAUnit;
import weapon.Equipment;

public class NAItem extends ItemData implements NAUsable {
	public static final NAItem BLANK_ITEM = new NAItem(DotPaint.BLANK_SCRIPT);
	public NAItem(DotPaint paint) {
		super(paint);
	}
	public void paintInInventory(int x, int y, int w, int h) {
		super.paintScript.dotPaint_turnAndCapSize(x, y, w, h);
	}
	@Override
	public void paint() {
		if(((NAStage)GHQ.stage()).playerSeenMark().get_stageCod(point().intX(), point().intY(), false))
			super.paint();
	}
	public void reset() {}
	public void reloadIfEquipment() {
		if(this instanceof Equipment) {
			((Equipment)this).weapon.startReloadIfNotDoing();
		}
	}
	public final void removeFromUnit() {
		((NAUnit)owner).removeItem(this);
	}
	public void removed(Storage<ItemData> storage) {}
	@Override
	public final boolean use(boolean isHeadInput) {
		if(isHeadInput || supportSerialUse()) {
			use();
			return true;
		}
		return false;
	}
}
