package item.magicChip;

import core.GHQ;
import item.NAItem;
import paint.dot.DotPaint;
import unit.Unit;

public class MagicChip extends NAItem {
	private int coolNeeds;
	private int coolRatePerSecond;
	private double currentCoolProcess;
	public MagicChip(DotPaint paint, int coolNeeds, int coolRatePerSecond) {
		super(paint);
		super.amount = 1;
		this.coolNeeds = coolNeeds;
		this.coolRatePerSecond = coolRatePerSecond;
	}
	@Override
	public void idle() {
		super.idle();
		if(!isReady()) {
			currentCoolProcess += coolRatePerSecond*GHQ.getSPF();
			if(currentCoolProcess >= coolNeeds) {
				currentCoolProcess = coolNeeds;
			}
		}
	}
	public boolean isReady() {
		return currentCoolProcess >= coolNeeds;
	}
	@Override
	public void setOwner(Unit unit) {
		super.setOwner(unit);
		currentCoolProcess = 0;
	}
	@Override
	public void use() {
		currentCoolProcess = 0;
	}
	public double coolRate() {
		return coolNeeds == 0.0 ? 1.0 : currentCoolProcess/coolNeeds;
	}
	public boolean supportSerialUse() {
		return false;
	}
	@Override
	public double weight() {
		return 0.1; //100g per chip
	}
}
