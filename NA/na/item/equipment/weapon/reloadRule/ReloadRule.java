package item.equipment.weapon.reloadRule;

import item.ammo.storage.AmmoBag;
import item.ammo.storage.AmmoStorage;
import item.equipment.weapon.NAFirearms;
import paint.dot.DotPaint;
import paint.dot.HasDotPaint;

public abstract class ReloadRule implements HasDotPaint {
	protected final DotPaint iconPaint = new DotPaint() {
		@Override
		public void dotPaint(int x, int y) {
			DotPaint ammoPaint;
			for(int i = 0; i < 3 && (ammoPaint = posAmmoPaint(i)) != null; ++i) {
				ammoPaint.dotPaint(x - 15 + 15*i, y);
			}
		}
		@Override
		public int width() {
			return 50;
		}
		@Override
		public int height() {
			return 50;
		}
	};
	//control
	/**
	 * Remove some ammo from its target ammoBags and tell how many was successfully removed.
	 * @param firearm
	 * @return Reloaded amount
	 */
	public abstract int reloadAmmo(NAFirearms firearm, int amount);
	@Override
	public DotPaint getDotPaint() {
		return iconPaint;
	}
	public abstract AmmoBag posAmmoBag(int pos);
	public DotPaint posAmmoPaint(int pos) {
		return posAmmoBag(pos).ammoEnchantsPaint();
	}
	
	//information
	public abstract int reloadableAmmoAmount(AmmoStorage storage);
}
