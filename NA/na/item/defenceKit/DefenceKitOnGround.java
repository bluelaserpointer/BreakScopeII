package item.defenceKit;

import paint.dot.DotPaint;
import ui.HUD;
import unit.NAUnit;

public abstract class DefenceKitOnGround extends DefenceKit {

	//isInstalled
	protected boolean installed;
	
	public DefenceKitOnGround(DotPaint foldedPaint, DotPaint openedPaint) {
		super(foldedPaint, openedPaint);
	}
	public boolean installToGround(int x, int y) {
		if(hasOwner())
			((NAUnit)owner()).removeItem(this);
		drop(x, y);
		installed = true;
		return true;
		
	}
	@Override
	public boolean installToHUDTarget() {
		return installToGround(HUD.installTargetX, HUD.installTargetY);
	}
	@Override
	public void uninstall() {
		installed = false;
	}
	@Override
	public boolean installed() {
		return installed;
	}
}
