package weapon;

import calculate.adjuster.ValueChangeAdjuster;
import core.GHQ;
import damage.DamageResourceType;
import damage.NADamage;
import damage.DamageMaterialType;
import paint.ImageFrame;
import unit.NAUnit;
import weapon.Weapon;

public class ElectronShield extends Equipment {
	private int lastDamaged = 0;
	protected int coolFinishFrame = 0;
	public ElectronShield(int capacity) {
		super(ImageFrame.create("picture/FreezeEffect.png"), NAUnit.BodyPartsTypeLibrary.SHIELD);
		weapon.magazine = weapon.magazineSize = capacity;
	}
	@Override
	public String name() {
		return "ELECTRON_SHIELD";
	}
	@Override
	protected Weapon def_weapon() {
		return new Weapon() {
			{
				name = "ELECTRON_SHIELD";
			} 
			@Override
			public void idle() {
				super.idle();
				/*if(GHQ.isExpired_dynamicSeconds(lastDamaged, 10.0)) {
					damaged();
					magazine += magazineSize/10;
					if(magazine > magazineSize)
						magazine = magazineSize;
				}*/
			}
			@Override
			public int getLeftAmmo() {
				return ((NAUnit)owner).BLUE_BAR.intValue();
			}
			@Override
			public void consumeAmmo(int value) {
				((NAUnit)owner).BLUE_BAR.consume(value);
			}
		};
	}
	//control
	public void addCoolFrame(int frame) {
		if(coolFinishFrame < GHQ.nowFrame()) {
			coolFinishFrame = GHQ.nowFrame();
		}
		coolFinishFrame += frame;
	}
	//event
	public void damaged() {
		lastDamaged = GHQ.nowFrame();
	}
	public final ValueChangeAdjuster shieldAdjuster = new ValueChangeAdjuster(name()) {
		@Override
		public Number decreased(Number oldNumber, Number newNumber) {
			if(coolFinishFrame <= GHQ.nowFrame()) {
				final NADamage LAST_DMG = ((NAUnit)owner).lastDamage();
				if(LAST_DMG.materialType() == DamageMaterialType.Poi) { //if this is poison damage
					if(LAST_DMG.resourceType() != DamageResourceType.Inner) //immune to outsider poison damage
						return oldNumber;
					else //cannot reduce inner poison damages
						return newNumber;
				}
				final int DMG_VALUE = oldNumber.intValue() - newNumber.intValue();
				//try shield
				if(weapon.unload(DMG_VALUE) == DMG_VALUE) //shield success
					damaged();
				else {
					removeFromUnit(); //destroy shield
				}
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
		((NAUnit)owner).RED_BAR.getValueWithCalculation_value().removeOneAdjuster(name());
	}
	public int getShieldValue() {
		return weapon.getMagazineFilledSpace();
	}
	public int getShieldSize() {
		return weapon.magazineSize;
	}
	public double getShieldRate() {
		return weapon.getMagazineFilledRate();
	}
}
