package item;

import item.weapon.Equipment;
import item.weapon.MainSlot;
import item.weapon.SubSlot;
import paint.dot.DotPaint;
import unit.BasicUnit;

public class BSItem extends ItemData{
	private static final long serialVersionUID = -9128122250005568441L;
	public static final BSItem BLANK_ITEM = new BSItem(DotPaint.BLANK_SCRIPT);
	public BSItem(DotPaint paint) {
		super(paint);
	}
	//main role
	@Override
	public void idle() {
		super.idle();
		if(hasOwner() && this instanceof Equipment) {
			if(this instanceof MainSlot && ((BasicUnit)owner).mainSlot == this)
				((Equipment)this).weapon.idle();
			else if(this instanceof SubSlot && ((BasicUnit)owner).subSlot == this) {
				((Equipment)this).weapon.idle();
			}
		}
	}
	public void paintInInventory(int x, int y, int w, int h) {
		super.paintScript.dotPaint_turnAndCapSize(x, y, w, h);
	}
	public void reset() {}
	public void reloadIfEquipment() {
		if(this instanceof Equipment)
			((Equipment)this).weapon.startReloadIfNotDoing();
	}
}
