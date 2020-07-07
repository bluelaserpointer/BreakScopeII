package item.equipment.weapon;

import calculate.adjuster.ValueChangeAdjuster;
import core.GHQ;
import item.equipment.Equipment;
import damage.DamageComposition;
import damage.DamageMaterial;
import damage.NADamage;
import paint.ImageFrame;
import unit.NAUnit;

public class ElectronShield extends Equipment {
	private int lastDamaged = 0;
	private int capacity;
	private int shieldEnergy;
	public ElectronShield(int capacity) {
		super(ImageFrame.create("picture/FreezeEffect.png"));
		this.capacity = capacity;
		shieldEnergy = capacity;
		setEquippableBodyPartsType(NAUnit.BodyPartsTypeLibrary.SHIELD);
	}
	@Override
	public String name() {
		return "ELECTRON_SHIELD";
	}
	//control
	public int addEnergy(int amount) {
		if(shieldEnergy + amount > capacity) {
			final int added = capacity - shieldEnergy;
			shieldEnergy = capacity;
			return added;
		} else {
			shieldEnergy += amount;
			return amount;
		}
	}
	
	//event
	public final ValueChangeAdjuster shieldAdjuster = new ValueChangeAdjuster(name()) {
		@Override
		public Number decreased(Number oldNumber, Number newNumber) {
			final NADamage damage = ((NAUnit)owner).lastDamage();
			final DamageComposition composition = damage.dealingComposition();
			if(composition.material() == DamageMaterial.Poi) { //if this is poison damage
				if(!composition.fromInside()) { //immune to outsider poison damage
					return oldNumber;
				}else //cannot reduce inner poison damages
					return newNumber;
			}
			//damage will be bear by shield, so it should show the dmg number by itself
			damage.makeEffect(owner, composition);
			final double DMG_VALUE = oldNumber.doubleValue() - newNumber.doubleValue();
			//try shield
			if((shieldEnergy -= (int)DMG_VALUE) >= 0) { //shield success
				lastDamaged = GHQ.nowFrame();
			
			} else {
				shieldEnergy = 0;
				removeFromUnit(); //destroy shield
			}
			return oldNumber;
		}
	};
	@Override
	public void equipped() {
		super.equipped();
		((NAUnit)owner).RED_BAR.getValueWithCalculation_value().pushAdjuster(shieldAdjuster);
	}
	@Override
	public void dequipped() {
		super.dequipped();
		if(owner != null)
			((NAUnit)owner).RED_BAR.getValueWithCalculation_value().removeOneAdjuster(name());
	}
	
	//information
	public int getShieldValue() {
		return shieldEnergy;
	}
	public int getShieldSize() {
		return capacity;
	}
	public double getShieldRate() {
		return (double)shieldEnergy/capacity;
	}
	public int lastDamaged() {
		return lastDamaged;
	}
	@Override
	public double weight() {
		return 1; //1kg
	}
}
