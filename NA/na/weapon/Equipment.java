package weapon;

import core.GHQ;
import engine.NAGame;
import item.NAItem;
import paint.dot.DotPaint;
import unit.NAUnit;
import weapon.Weapon;
import weapon.gripStyle.GripStyle;

public abstract class Equipment extends NAItem {
	public static final Equipment NULL_EQUIPMENT = new Equipment(DotPaint.BLANK_SCRIPT, NAUnit.BodyPartsTypeLibrary.MAIN_WEAPON) {
		@Override
		protected Weapon def_weapon() {
			return Weapon.NULL_WEAPON;
		}
	};
	public Weapon weapon;
	protected GripStyle gripStyle;
	public DotPaint focusPaint = DotPaint.BLANK_SCRIPT;
	public double effectiveRange = GHQ.MAX, effectiveAngleWidth = 0.10;
	public Equipment(DotPaint paint, NAUnit.BodyPartsTypeLibrary weaponSlotType) {
		super(paint);
		amount = 1;
		weapon = def_weapon();
		this.setEquippableBodyPartsType(weaponSlotType);
	}
	//init
	protected abstract Weapon def_weapon();
	protected void setFocusPaint(DotPaint focusPaint) {
		this.focusPaint = focusPaint;
	}
	@Override
	public void idle() {
		super.idle();
		if(hasOwner() && isEquipped()) {
			weapon.idle();
			if(NAGame.controllingUnit() == owner() && ((NAUnit)owner()).currentEquipment() == this) //draw focus image at cursor
				paintFocus(GHQ.mouseX(), GHQ.mouseY());
		}
	}
	protected void paintFocus(int x, int y) {
		focusPaint.dotPaint(x, y);
	}
	//control
	public Equipment setGripStyle(GripStyle gripStyle) {
		this.gripStyle = gripStyle;
		return this;
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
	//information
	public GripStyle gripStyle() {
		return gripStyle;
	}
}
