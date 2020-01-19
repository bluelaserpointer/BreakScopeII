package item.weapon;

import item.NAItem;
import paint.dot.DotPaint;
import unit.NAUnit;
import weapon.Weapon;

public abstract class Equipment extends NAItem{
	private static final long serialVersionUID = -1892229139354741548L;
	public static final Equipment NULL_EQUIPMENT = new Equipment(DotPaint.BLANK_SCRIPT) {
		private static final long serialVersionUID = 2427537296164596351L;
		@Override
		protected Weapon def_weapon() {
			return Weapon.NULL_WEAPON;
		}
	};
	public Weapon weapon;
	private boolean isEquipped;
	public Equipment(DotPaint paint) {
		super(paint);
		amount = 1;
		weapon = def_weapon();
	}
	protected abstract Weapon def_weapon();
	@Override
	public void idle() {
		super.idle();
		if(hasOwner() && isEquipped())
			weapon.idle();
	}
	@Override
	public void reset() {
		weapon.reset();
	}
	@Override
	public void use() {
		if(owner != null)
			weapon.trigger(owner);
	}
	public final void equipToOwner() {
		((NAUnit)owner).equip(this);
	}
	public final void dequipFromOwner() {
		((NAUnit)owner).dequip(this);
	}
	public void equipped() {
		isEquipped = true;
	}
	public void dequipped() {
		isEquipped = false;
	}
	public boolean isEquipped() {
		return isEquipped;
	}
}
