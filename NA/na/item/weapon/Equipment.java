package item.weapon;

import item.BSItem;
import paint.dot.DotPaint;
import weapon.Weapon;

public abstract class Equipment extends BSItem{
	private static final long serialVersionUID = -1892229139354741548L;
	public Weapon weapon;
	public Equipment(DotPaint paint) {
		super(paint);
		amount = 1;
		weapon = def_weapon();
	}
	protected abstract Weapon def_weapon();
	@Override
	public void reset() {
		weapon.reset();
	}
	@Override
	public void use() {
		if(owner != null)
			weapon.trigger(owner);
	}
}
