package item.equipment.weapon;

import java.util.LinkedList;
import java.util.List;

import bullet.BulletLibrary;
import core.GHQ;
import effect.EffectLibrary;
import effect.EffectLibrary.FiredSmokeEF;
import item.ammo.AmmoType;
import item.ammo.enchant.AmmoEnchants;
import item.equipment.weapon.reloadRule.AcceptAllReloadRule;
import item.equipment.weapon.reloadRule.ReloadRule;
import item.equipment.weapon.reloadRule.UserDefinedReloadRule;
import paint.dot.DotPaint;
import preset.bullet.Bullet;

public abstract class NAFirearms extends NAMainWeapon {
	protected final AcceptAllReloadRule acceptAllReloadRule = new AcceptAllReloadRule(this);
	protected final UserDefinedReloadRule userDefinedReloadRule = new UserDefinedReloadRule();
	protected ReloadRule currentReloadRule = acceptAllReloadRule;
	protected LinkedList<AmmoEnchants> magazineContents = new LinkedList<>();
	public NAFirearms(DotPaint paint) {
		super(paint);
	}
	private int lastFiredFrame = -100;
	@Override
	public void idle() {
		super.idle();
		if(!GHQ.isExpired_frame(lastFiredFrame, 10)) {
			GHQ.stage().addEffect(new FiredSmokeEF(this));
		}
	}
	@Override
	public void use() {
		if(owner != null) {
			if(weapon.magazineReady()) {
				final List<Bullet> firedBullets = weapon.trigger(owner);
				if(firedBullets != null) {
					for(Bullet bullet : firedBullets) {
						final AmmoEnchants enchants = popAmmoFromMagazine().clone();
						if(enchants != null) {
							((BulletLibrary)bullet).setEnchants(enchants);
							enchants.applyFireEffect(bullet);
						}
					}
					final double scatterRange = Math.toRadians(10.0);
					for(int i = 0; i < 100; ++i) {
						GHQ.stage().addEffect(new EffectLibrary.FireEF(owner, Math.random()*scatterRange - scatterRange/2, Math.random()*45));
					}
					lastFiredFrame = GHQ.nowFrame();
				}
			} else {
				this.reloadWeapon();
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
