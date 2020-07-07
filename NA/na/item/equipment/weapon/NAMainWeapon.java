package item.equipment.weapon;

import paint.dot.DotPaint;
import unit.NAUnit;

public abstract class NAMainWeapon extends NAWeaponEquipment {
	public NAMainWeapon(DotPaint paint) {
		super(paint);
		setEquippableBodyPartsType(NAUnit.BodyPartsTypeLibrary.MAIN_WEAPON, NAUnit.BodyPartsTypeLibrary.SUB_WEAPON);
	}
}
