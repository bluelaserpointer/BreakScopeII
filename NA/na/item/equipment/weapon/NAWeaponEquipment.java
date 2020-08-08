package item.equipment.weapon;

import core.GHQ;
import item.equipment.Equipment;
import item.equipment.weapon.gripStyle.GripStyle;
import paint.dot.DotPaint;
import physics.HasPoint;
import weapon.Weapon;

public abstract class NAWeaponEquipment extends Equipment {
	public NAWeaponEquipment(DotPaint paint) {
		super(paint);
		weapon = def_weapon();
		weapon.setAutoReload(false);
	}
	protected double effectiveRange = GHQ.MAX, effectiveAngleWidth = 0.10;
	protected Weapon weapon;
	protected GripStyle gripStyle;

	//init
	protected abstract Weapon def_weapon();
	
	//main role
	@Override
	public void equippedIdle() {
		weapon.idle();
	}
	
	//control
	@Override
	public void use() {
		if(owner != null) {
			if(weapon.magazineReady())
				weapon.trigger(owner);
			else
				this.reloadWeapon();
		}
	}
	@Override
	public void reset() {
		weapon.reset();
	}
	public Equipment setGripStyle(GripStyle gripStyle) {
		this.gripStyle = gripStyle;
		return this;
	}
	public void reloadWeapon() {
		weapon.startReloadIfNotDoing();
	}
	
	//information
	@Override
	public abstract double weight();
	public GripStyle gripStyle() {
		return gripStyle;
	}
	public Weapon weapon() {
		return weapon;
	}
	public double effectiveRange() {
		return effectiveRange;
	}
	public double effectiveAngleWidth() {
		return effectiveAngleWidth;
	}
	public boolean effectiveTarget(double distance, double angleDiff) {
		return effectiveRange() > distance && effectiveAngleWidth() > angleDiff;
	}
	public boolean effectiveTarget(HasPoint target) {
		return effectiveTarget(owner().point().distance(target), owner().angle().diff(point().angleTo(target)));
	}
}
