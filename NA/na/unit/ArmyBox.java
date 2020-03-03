package unit;

import engine.NAGame;
import paint.ImageFrame;
import paint.dot.DotPaint;
import paint.dot.DotPaintResizer;

public class ArmyBox extends NAUnit {
	
	public ArmyBox() {
		super(100);
		body().trunk().setBasePaint(new DotPaintResizer(ImageFrame.create("picture/map/ArmyBox1.png"), 0.5));
		body().hands().setBasePaint(DotPaint.BLANK_SCRIPT);
		body().head().setBasePaint(DotPaint.BLANK_SCRIPT);
		body().legs().setBasePaint(DotPaint.BLANK_SCRIPT);
		body().foots().setBasePaint(DotPaint.BLANK_SCRIPT);
	}

	@Override
	public UnitGroup unitGroup() {
		return UnitGroup.INVALID;
	} 
	@Override
	public boolean interact(NAUnit unit) {
		NAGame.openInventoryInvester(inventory);
		return true;
	}
}
