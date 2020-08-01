package item.ammo.storage;

import java.util.HashMap;
import java.util.LinkedList;

import item.ammo.Ammo;
import item.ammo.AmmoType;
import paint.dot.DotPaint;

/**
 * Stores ammo grouped by its AmmoType and its enchants combination.
 * @author bluelaserpointer
 *
 */
public class AmmoStorage {
	protected final HashMap<AmmoType, LinkedList<AmmoBag>> ammoBagLists = new HashMap<AmmoType, LinkedList<AmmoBag>>();
	public AmmoStorage() {}
	public AmmoStorage(AmmoType...types) {
		for(AmmoType type : types) {
			addAmmoBagList(type);
		}
	}
	
	//control
	public void addAmmoBagList(AmmoType type) {
		ammoBagLists.put(type, new LinkedList<AmmoBag>());
	}
	public void add(Ammo ammo) {
		final AmmoBag correspondingBag = getBag(ammo);
		if(correspondingBag != null) {
			correspondingBag.add(ammo.getAmount());
		} else {
			final LinkedList<AmmoBag> bagList = ammoBagList(ammo);
			bagList.add(new AmmoBag(bagList, ammo));
		}
	}
	public void removeStackable(Ammo ammo) { //TODO: implement
		
	}
	public void clear(AmmoType ammoType) {
		ammoBagList(ammoType).clear();
	}
	public void clear() {
		for(LinkedList<AmmoBag> ammoBagList : ammoBagLists.values())
			ammoBagList.clear();
	}
	public LinkedList<AmmoBag> removeAmmoBagList(AmmoType type) {
		return ammoBagLists.remove(type);
	}
	public void removeAllAmmoBagList() {
		ammoBagLists.clear();
	}
	
	//information
	public LinkedList<AmmoBag> ammoBagList(AmmoType ammoType) {
		return ammoBagLists.get(ammoType);
	}
	public LinkedList<AmmoBag> ammoBagList(Ammo ammo) {
		return ammoBagList(ammo.type());
	}
	public int countByType(AmmoType ammoType) {
		int count = 0;
		for(AmmoBag bag : ammoBagLists.get(ammoType)) {
			count += bag.getAmount();
		}
		return count;
	}
	public AmmoBag getBag(Ammo ammo) {
		for(AmmoBag bag : ammoBagLists.get(ammo.type())) {
			if(bag.enchants().equals(ammo.enchants()) && bag.stackable(ammo))
				return bag;
		}
		return null;
	}
	private final class BasicReloadRuleIconPaint extends DotPaint {
		final DotPaint bulletPaint;
		public BasicReloadRuleIconPaint(DotPaint bulletPaint) {
			this.bulletPaint = bulletPaint;
		}
		@Override
		public void dotPaint(int x, int y) {
			for(int i = 0; i < 3; ++i) {
				bulletPaint.dotPaint(x - 15 + 15*i, y);
			}
		}
		@Override
		public int width() {
			return bulletPaint.width()*3;
		}
		@Override
		public int height() {
			return 50;
		}
	};
	public DotPaint getBasicReloadRuleIcon(AmmoType ammoType, int id) {
		final LinkedList<AmmoBag> bagList = ammoBagList(ammoType);
		if(id < bagList.size()) {
			return new BasicReloadRuleIconPaint(bagList.get(id).enchants().getDotPaint());
		}
		return null;
	}
}
