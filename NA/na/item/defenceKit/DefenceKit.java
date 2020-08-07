package item.defenceKit;

import item.NAItem;
import paint.dot.DotPaint;
import preset.unit.Unit;
import ui.HUD;
import unit.NAUnit;

public abstract class DefenceKit extends NAItem {
	//owner
	protected NAUnit lastOwner;
	//init
	public DefenceKit(DotPaint foldedPaint, DotPaint openedPaint) {
		super(null);
		super.paintScript = new DotPaint() {
			private DotPaint getPaint() {
				return DefenceKit.this.installed() ? openedPaint : foldedPaint;
			}
			@Override
			public void dotPaint(int x, int y) {
				getPaint().dotPaint(x, y);
			}
			@Override
			public int width() {
				return getPaint().width();
			}
			@Override
			public int height() {
				return getPaint().height();
			}
		};
	}
	//idle
	//control
	public abstract void openInfoUI();
	@Override
	public void interact(NAUnit unit) {
		if(this.installed()) {
			openInfoUI();
		} else {
			super.interact(unit); // pick up
		}
	}
	public abstract boolean installToHUDTarget();
	public abstract void uninstall();
	@Override
	public void use() {
		if(HUD.installTargetTile != null) {
			this.installToHUDTarget();
		}
	}
	@Override
	public void setOwner(Unit unit) {
		super.setOwner(unit);
		if(unit != null)
			lastOwner = (NAUnit)unit;
	}
	//information
	public abstract boolean installed();
}
