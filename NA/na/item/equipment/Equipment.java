package item.equipment;

import item.NAItem;
import paint.dot.DotPaint;
import unit.NAUnit;

public abstract class Equipment extends NAItem {
	public static final Equipment NULL_EQUIPMENT = new Equipment(DotPaint.BLANK_SCRIPT) {
		@Override
		public double weight() {
			return 0;
		}
	};
	public Equipment(DotPaint paint) {
		super(paint);
		amount = 1;
		super.setStackCap(1); //not stackable
	}
	//init
	@Override
	public void idle() {
		super.idle();
		if(hasOwner() && isEquipped()) {
			this.equippedIdle();
		}
	}
	public void equippedIdle() {}
	
	//control
	public final void equipToOwner() {
		((NAUnit)owner).equip(this);
	}
	public final void dequipFromOwner() {
		((NAUnit)owner).dequip(this);
	}
	
	//information
	@Override
	public boolean keepEvenEmpty() {
		return false;
	}
}
