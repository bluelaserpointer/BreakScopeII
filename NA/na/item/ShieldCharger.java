package item;

import item.equipment.Equipment;
import item.equipment.weapon.ElectronShield;
import paint.ImageFrame;
import unit.NAUnit;

public class ShieldCharger extends NAItem {
	int chargeSpeed = 100;
	public ShieldCharger(int amount) {
		super(ImageFrame.create("picture/battery.png"));
		super.amount = amount;
	}
	@Override
	public String name() {
		return "ShieldCharger";
	}
	//init
	public ShieldCharger setChargeSpeed(int speed) {
		chargeSpeed = speed;
		return this;
	}
	//main role
	@Override
	public void use() {
		if(owner() == null)
			return;
		final NAUnit unit = (NAUnit)owner();
		final Equipment equipment = unit.body().shield();
		if(equipment == null)
			return;
		final ElectronShield shield = (ElectronShield)equipment;
		final int diff = shield.getShieldSize() - shield.getShieldValue();
		if(diff == 0)
			return;
		final int charge = chargeSpeed <= amount ? chargeSpeed : amount;
		System.out.println("diff: " + diff + ", charge: " + charge);
		super.add(-shield.addEnergy(charge));
	}
	//information
	public int chargeSpeed() {
		return chargeSpeed;
	}
	@Override
	public boolean stackable(ItemData item) {
		return item instanceof ShieldCharger;
	}
	@Override
	public boolean supportSerialUse() {
		return true;
	}
	@Override
	public double weight() {
		return 1;
	}
}
