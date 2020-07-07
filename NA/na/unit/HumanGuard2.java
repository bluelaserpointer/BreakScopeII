package unit;

import item.equipment.weapon.Type56;
import paint.ImageFrame;
import talent.AllUp;

public class HumanGuard2 extends NAUnit {
	public HumanGuard2() {
		super(70);
		POW_FIXED.setMax(15).setToMax();
		this.addTalent(new AllUp(this));
		this.addTalent(new AllUp(this));
	}
	@Override
	public final String name() {
		return "HumanGuard2";
	}
	@Override
	public final UnitGroup unitGroup() {
		return UnitGroup.GUARD;
	}
	@Override
	public final HumanGuard2 respawn(int x, int y) {
		super.respawn(x, y);
		equip(addItemToStorage(new Type56()));
		return this;
	}
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaPaint = ImageFrame.create("picture/grayman.png");
	}
}
