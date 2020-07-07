package item.equipment.weapon;

import paint.dot.DotPaint;
import unit.NAUnit;

public abstract class NASubWeapon extends NAWeaponEquipment {
	public NASubWeapon(DotPaint paint) {
		super(paint);
		setEquippableBodyPartsType(NAUnit.BodyPartsTypeLibrary.MELLE_WEAPON);
	}
	public double sharpness() {
		return 1.0;
	}
	public double knockbackRate() {
		return 1.0;
	}
}
