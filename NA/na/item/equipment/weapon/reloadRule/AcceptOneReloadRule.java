package item.equipment.weapon.reloadRule;

import item.ammo.storage.AmmoBag;
import item.ammo.storage.AmmoStorage;
import item.equipment.weapon.NAFirearms;

public class AcceptOneReloadRule extends ReloadRule {
	private final AmmoBag ammoBag;
	public AcceptOneReloadRule(AmmoBag ammoBag) {
		this.ammoBag = ammoBag;
	}
	@Override
	public int reloadAmmo(NAFirearms firearm) {
		final int need = firearm.magazineSize() - firearm.magazineFilledAmount();
		final int filled = ammoBag.consume(need);
		for(int i = 0; i < filled; ++i) {
			firearm.pushAmmoToMagazine(ammoBag.enchants());
		}
		return filled;
	}
	@Override
	public AmmoBag posAmmoBag(int pos) {
		return ammoBag;
	}
	@Override
	public int reloadableAmmoAmount(AmmoStorage storage) {
		return ammoBag.getAmount();
	}
}
