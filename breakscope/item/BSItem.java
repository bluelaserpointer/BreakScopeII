package item;

import core.GHQ;
import paint.RectPaint;
import unit.Item;

public abstract class BSItem extends Item{
	final String BASE_NAME;
	@Override
	public final String getName() {
		return BASE_NAME;
	}
	public BSItem(String baseName,int stackCap, RectPaint paint) {
		super(stackCap, paint);
		BASE_NAME = baseName;
	}
	public BSItem(String baseName, RectPaint paint) {
		super(GHQ.MAX, paint);
		BASE_NAME = baseName;
	}
	//main role
	public void paintInInventory(int x,int y,int w,int h) {
		super.paintScript.paint(x, y, w, h);
	}
	public void paintInStage(int x,int y,int w,int h) {
		super.paintScript.paint(x, y, w, h);
	}
}
