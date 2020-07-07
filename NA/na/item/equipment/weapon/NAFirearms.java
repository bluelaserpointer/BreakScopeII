package item.equipment.weapon;

import java.util.LinkedList;
import java.util.List;

import bullet.Bullet;
import bullet.BulletLibrary;
import item.ammo.AmmoType;
import item.ammo.enchant.AmmoEnchants;
import item.equipment.weapon.reloadRule.AcceptAllReloadRule;
import item.equipment.weapon.reloadRule.ReloadRule;
import item.equipment.weapon.reloadRule.UserDefinedReloadRule;
import paint.dot.DotPaint;

public abstract class NAFirearms extends NAMainWeapon {
	protected final AcceptAllReloadRule acceptAllReloadRule = new AcceptAllReloadRule(this);
	protected final UserDefinedReloadRule userDefinedReloadRule = new UserDefinedReloadRule();
	protected ReloadRule currentReloadRule;
	protected LinkedList<AmmoEnchants> magazineContents = new LinkedList<>();
	public NAFirearms(DotPaint paint) {
		super(paint);
	}
	
	@Override
	public void use() {
		if(owner != null) {
			final List<Bullet> firedBullets = weapon.trigger(owner);
			if(firedBullets != null) {
				for(Bullet bullet : firedBullets) {
					final AmmoEnchants enchants = popAmmoFromMagazine();
					if(enchants != null) {
						enchants.applyFireEffect(bullet);
						((BulletLibrary)bullet).setEnchants(enchants);
					}
				}
			}
		}
	}
	//control
	public void setReloadRule(ReloadRule reloadRule) {
		currentReloadRule = reloadRule;
	}
	public boolean pushAmmoToMagazine(AmmoEnchants ammoEnchants) {
		if(magazineContents.size() < magazineSize()) {
			magazineContents.push(ammoEnchants);
			return true;
		}
		return false;
	}
	public AmmoEnchants popAmmoFromMagazine() {
		return magazineContents.isEmpty() ? null : magazineContents.pop();
	}
	public void magazineCleared() {
		magazineContents.clear();
	}
	public int magazineFilledAmount() {
		return weapon.magazine();
	}
	public int magazineSize() {
		return weapon.magazineSize();
	}
	@Override
	public void reloadWeapon() {
		weapon.startReloadForced();
	}
	
	//information
	public abstract AmmoType usingAmmoType();
	public LinkedList<AmmoEnchants> magazineContents() {
		return magazineContents;
	}
	public AcceptAllReloadRule acceptAllReloadRule() {
		return acceptAllReloadRule;
	}
	public UserDefinedReloadRule userDefinedReloadRule() {
		return userDefinedReloadRule;
	}
	public ReloadRule currentReloadRule() {
		return currentReloadRule;
	}
}
