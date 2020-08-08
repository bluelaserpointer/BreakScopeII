package ui;

import java.awt.Color;

import core.GHQ;
import gui.GUIParts;
import gui.TableStorageViewer;
import item.NAUsable;
import paint.ImageFrame;
import paint.dot.HasDotPaint;

public class QuickSlotViewer extends TableStorageViewer<NAUsable> {
	private final ImageFrame ICON_BG = ImageFrame.create("picture/gui/Bag_item.png");
	private int[] lastActivatedFrame = new int[10];
	{
		setName("QuickSlot");
		setBGColor(Color.WHITE);
	}
	public QuickSlotViewer() {
		super(NAUsable.class);
	}
	@Override
	protected void paintOfCell(int id, HasDotPaint object, int x, int y) {
		ICON_BG.rectPaint(x, y, this.cellSize);
		super.paintOfCell(id, object, x, y);
		GHQ.getG2D(Color.RED, GHQ.stroke1).drawRect(x, y, cellSize, cellSize);
		if(GHQ.passedFrame(lastActivatedFrame[id]) < 5) {
			GHQ.getG2D(Color.WHITE, GHQ.stroke3).drawRect(point().intX() + id*cellSize, point().intY(), cellSize, cellSize);
		}
	}
	public void lit(int id) {
		lastActivatedFrame[id] = GHQ.nowFrame();
	}
	@Override
	public boolean doLinkDrag() {
		return true;
	}
	@Override
	public boolean checkDragIn(GUIParts sourceUI, Object dropObject) {
		if(sourceUI instanceof EquipmentSlot)
			return false;
		else
			return super.checkDragIn(sourceUI, dropObject);
	}
}
