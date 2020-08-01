package item.equipment.weapon.reloadRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import item.ammo.Ammo;
import item.ammo.storage.AmmoBag;
import item.ammo.storage.AmmoStorage;
import item.equipment.weapon.NAFirearms;

public class UserDefinedReloadRule extends ReloadRule {
	final HashMap<AmmoBag, Integer> requestMap = new HashMap<>();
	final ArrayList<AmmoBag> posAmmoBag = new ArrayList<AmmoBag>();
	protected LinkedList<Ammo> magazineRecord = new LinkedList<Ammo>();
	//control
	public void addMagazineSize(int size) {
		for(int i = 0; i < size; ++i) {
			posAmmoBag.add(posAmmoBag.get(posAmmoBag.size() - 1));
		}
	}
	public void shrinkMagazineSize(int size) {
		final int posSize = posAmmoBag.size();
		final int iMax = Math.min(size, posSize);
		for(int i = 0; i < iMax; ++i) {
			posAmmoBag.remove(posSize - 1 - i);
		}
	}
	public void set(int pos, AmmoBag ammoBag) {
		final AmmoBag oldBag = posAmmoBag.set(pos, ammoBag);
		if(!oldBag.equals(ammoBag)) {
			final int oldBagRequest = requestMap.get(oldBag);
			if(oldBagRequest == 1) {
				requestMap.remove(oldBag);
			} else {
				requestMap.put(oldBag, oldBagRequest - 1);
			}
			final int newBagRequest = requestMap.get(ammoBag);
			if(newBagRequest == -1) {
				requestMap.put(ammoBag, 1);
			} else {
				requestMap.put(ammoBag, newBagRequest + 1);
			}
		}
	}
	public void setAll(AmmoBag ammoBag) {
		for(int i = 0; i < posAmmoBag.size(); ++i) {
			posAmmoBag.set(i, ammoBag);
		}
		requestMap.clear();
		requestMap.put(ammoBag, posAmmoBag.size());
	}
	//information
	public AmmoBag getAmmoBag(int pos) {
		return posAmmoBag.get(pos);
	}
	@Override
	public int reloadAmmo(NAFirearms firearm, int amount) {
		int pos;
		final int magazineSize = firearm.magazineSize();
		final int startPos = magazineSize - 1 - firearm.magazineFilledAmount();
		for(pos = startPos; pos >= 0; --pos) {
			final AmmoBag bag = posAmmoBag.get(pos);
			if(bag.consumeWithAutoRemoveIfEmpty(1) > 0)
				firearm.pushAmmoToMagazine(bag.enchants());
			else //reload failed
				break;
		}
		return startPos - pos;
	}
	@Override
	public AmmoBag posAmmoBag(int pos) {
		return pos < posAmmoBag.size() ? posAmmoBag.get(pos) : AmmoBag.EMPTY_BAG;
	}
	@Override
	public int reloadableAmmoAmount(AmmoStorage storage) { //TODO:
		int leastReloadableAmount = Integer.MAX_VALUE;
		for(AmmoBag bag : requestMap.keySet()) {
			final AmmoBag targetBag = storage.getBag(bag.ammo());
			if(targetBag == null) {
				return 0;
			}
			final int reloadableTimes = targetBag.getAmount()/requestMap.get(bag);
			if(reloadableTimes < leastReloadableAmount)
				leastReloadableAmount = reloadableTimes;
		}
		return leastReloadableAmount;
	}
}
