package ui;

import java.awt.Color;

import core.GHQ;
import gui.TableStorageViewer;
import item.NAUsable;
import paint.ImageFrame;
import paint.dot.HasDotPaint;
import storage.TableStorage;

public class QuickSlotViewer extends TableStorageViewer<NAUsable> {
	public QuickSlotViewer(int x, int y, int cellSize, TableStorage<NAUsable> datalink) {
		super(x, y, cellSize, datalink);
	}
	private final ImageFrame ICON_BG = ImageFrame.create("picture/gui/slot.png");
	private int[] lastActivatedFrame = new int[10];
	{
		setName("QuickSlot");
		setBGColor(Color.WHITE);
	}
	@Override
	protected void paintOfCell(int id, HasDotPaint object, int x, int y) {
		ICON_BG.rectPaint(x, y, this.CELL_SIZE);
		super.paintOfCell(id, object, x, y);
		GHQ.getG2D(Color.RED, GHQ.stroke1).drawRect(x, y, CELL_SIZE, CELL_SIZE);
		if(GHQ.passedFrame(lastActivatedFrame[id]) < 5) {
			GHQ.getG2D(Color.WHITE, GHQ.stroke3).drawRect(point().intX() + id*CELL_SIZE, point().intY(), CELL_SIZE, CELL_SIZE);
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
	public NAUsable objectToT(Object object) {
		if(object instanceof NAUsable)
			return (NAUsable)object;
		return null;
	}
}
