package item;

import paint.dot.DotPaint;
import storage.ItemStorage;
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
	public void reset() {}
	public void reloadIfEquipment() {
		if(this instanceof Equipment) {
			((Equipment)this).weapon.startReloadIfNotDoing();
		}
	}
	public final void removeFromUnit() {
		((NAUnit)owner).removeItem(this);
	}
	public void removed(ItemStorage storage) {}
	@Override
	public final boolean use(boolean isHeadInput) {
		if(isHeadInput || supportSerialUse()) {
			use();
			return true;
		}
		return false;
	}
}
