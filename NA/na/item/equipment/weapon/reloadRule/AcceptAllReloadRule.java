package item.equipment.weapon.reloadRule;

import java.util.LinkedList;

import item.ammo.Ammo;
import item.ammo.enchant.AmmoEnchants;
import item.ammo.storage.AmmoBag;
import item.ammo.storage.AmmoStorage;
import item.equipment.weapon.NAFirearms;
import paint.ImageFrame;
import paint.dot.DotPaint;

public class AcceptAllReloadRule extends ReloadRule {
	public static final ImageFrame iconPaint = ImageFrame.create("picture/icon/reloadRule/All.png");
	private final NAFirearms arm;
	protected LinkedList<Ammo> magazineRecord = new LinkedList<Ammo>();
	public AcceptAllReloadRule(NAFirearms arm) {
		this.arm = arm;
	}
	@Override
	public int reloadAmmo(NAFirearms firearm) {
		int needReload = firearm.magazineSize() - firearm.magazineFilledAmount();
		final int initialNeedReload = needReload;
		for(AmmoBag bag : arm.owner().ammoStorage.ammoBagList(arm.usingAmmoType())) {
			final int filled = bag.consume(needReload);
			final AmmoEnchants enchants = bag.enchants();
			for(int i = 0; i < filled; ++i) {
				firearm.pushAmmoToMagazine(enchants);
			}
			if((needReload -= filled) == 0)
				break;
		}
		return initialNeedReload - needReload;
	}
	@Override
	public DotPaint getDotPaint() {
		return iconPaint;
	}
	@Override
	public AmmoBag posAmmoBag(int pos) {
		return arm.owner().ammoStorage.ammoBagList(arm.usingAmmoType()).getFirst();
	}
	@Override
	public int reloadableAmmoAmount(AmmoStorage storage) {
		return storage.countByType(arm.usingAmmoType());
	}
}
