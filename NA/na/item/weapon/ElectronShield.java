package item.weapon;

import calculate.adjuster.ValueChangeAdjuster;
import core.GHQ;
import damage.DamageResourceType;
import damage.NADamage;
import damage.DamageMaterialType;
import paint.ImageFrame;
import unit.NAUnit;
import weapon.Weapon;

public class ElectronShield extends Equipment implements SubSlot {
	private static final long serialVersionUID = -8064058001166040739L;
	private int lastDamaged = 0;
	public ElectronShield() {
		super(ImageFrame.create("picture/FreezeEffect.png"));
	}
	@Override
	public String name() {
		return "ELECTRON_SHIELD";
	}
	@Override
	protected Weapon def_weapon() {
		return new Weapon() {
			private static final long serialVersionUID = -3255234899242748103L;
			{
				name = "ELECTRON_SHIELD";
				magazineSize = 500;
				magazine = 500;
			}
			@Override
			public void idle() {
				super.idle();
				if(GHQ.isExpired_dynamicSeconds(lastDamaged, 10.0)) {
					damaged();
					magazine += magazineSize/10;
					if(magazine > magazineSize)
						magazine = magazineSize;
				}
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
	public void damaged() {
		lastDamaged = GHQ.nowFrame();
	}
	public final ValueChangeAdjuster shieldAdjuster = new ValueChangeAdjuster(name()) {
		@Override
		public Number decreased(Number oldNumber, Number newNumber) {
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
}
