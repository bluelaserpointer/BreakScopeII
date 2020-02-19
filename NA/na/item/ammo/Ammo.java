package item.ammo;

import item.NAItem;
import item.ItemData;
import paint.dot.DotPaint;

public abstract class Ammo extends NAItem {
	
	public Ammo(DotPaint paint, int amount) {
		super(paint);
		this.amount = amount;
	}
	@Override
	public abstract boolean isStackable(ItemData item);
	@Override
	public boolean keepEvenEmpty() {
		return false;
	}
}
