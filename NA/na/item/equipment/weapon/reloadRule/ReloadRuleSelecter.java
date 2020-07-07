package item.equipment.weapon.reloadRule;

import java.util.LinkedList;

import core.GHQ;
import item.ammo.AmmoType;
import item.ammo.storage.AmmoBag;
import item.equipment.weapon.NAFirearms;
import paint.dot.DotPaint;
import unit.NAUnit;

public class ReloadRuleSelecter {
	
	protected final NAUnit unit;
	protected int rollPos;
	protected int rolledFrame;
	public ReloadRuleSelecter(NAUnit unit) {
		this.unit = unit;
	}
	//main role
	public ReloadRule getRule(int id) {
		final int ruleAmount = availableRuleAmount();
		id = (id + rollPos) % ruleAmount;
		if(id < 0)
			id += ruleAmount;
		if(id == 0) {
			return currentArm().acceptAllReloadRule();
		}
		--id;
		if(id < availableAmmoBagAmount()) {
			return availableAmmoBagList().get(id).specialReloadRule();
		}
		return currentArm().userDefinedReloadRule();
	}
	public ReloadRule getCurrentSelection() {
		return getRule(0);
	}
	public DotPaint reloadRuleIcon(int id) {
		return getRule(id).getDotPaint();
	}
	
	//control
	public void roll(int amount) {
		if(amount != 0) {
			final int ruleAmount = availableRuleAmount();
			rollPos = (rollPos + amount) % ruleAmount;
			rolledFrame = GHQ.nowFrame();
		}
	}
	public int availableAmmoBagAmount() {
		return availableAmmoBagList().size();
	}
	public int availableRuleAmount() {
		return availableAmmoBagAmount() + 2;
	}
	protected final NAFirearms currentArm() {
		return (NAFirearms)unit.currentEquipment();
	}
	protected final AmmoType currentUsingAmmoType() {
		return currentArm().usingAmmoType();
	}
	protected final LinkedList<AmmoBag> availableAmmoBagList() {
		return unit.ammoStorage.ammoBagList(currentUsingAmmoType());
	}
}
