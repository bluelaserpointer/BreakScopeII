package item;

import paint.ImageFrame;
import unit.NAUnit;
import weapon.ElectronShield;
import weapon.Equipment;

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
		final int diff = shield.weapon.getMagazineEmptySpace();
		if(diff == 0)
			return;
		final int chargeAmount = chargeSpeed <= amount ? chargeSpeed : amount;
		System.out.println("diff: " + diff + ", charge: " + chargeAmount);
		if(chargeAmount < diff) {
			shield.weapon.magazine += chargeAmount;
			super.add(-chargeAmount);
		}else {
			shield.weapon.magazine += diff;
			super.add(-diff);
		}
		shield.addCoolFrame(1);
	}
	//information
	public int chargeSpeed() {
		return chargeSpeed;
	}
	@Override
	public boolean isStackable(ItemData item) {
		return item instanceof ShieldCharger;
	}
	public boolean supportSerialUse() {
		return true;
	}
}
