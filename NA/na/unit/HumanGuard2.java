package unit;

import paint.ImageFrame;
import talent.AllUp;
import weapon.ACCAR;

public class HumanGuard2 extends NAUnit {
	public HumanGuard2() {
		super(70);
		POW_FIXED.setMax(15).setToMax();
		this.addTalent(new AllUp(this));
		this.addTalent(new AllUp(this));
	}
	@Override
	public final String name() {
		return "FairyA";
	}
	@Override
	public final UnitGroup unitGroup() {
		return UnitGroup.GUARD;
	}
	@Override
	public final HumanGuard2 respawn(int x, int y) {
		super.respawn(x, y);
		equip(addItem(new ACCAR()));
		return this;
	}
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaPaint = ImageFrame.create("picture/grayman.png");
	}
}
