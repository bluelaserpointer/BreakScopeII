package item.ammo;

import item.BSItem;
import item.ItemData;
import paint.dot.DotPaint;

public abstract class Ammo extends BSItem{
	private static final long serialVersionUID = -4135430612764459044L;
	
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
