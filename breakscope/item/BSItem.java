package item;

import core.GHQ;
import paint.DotPaint;

public abstract class BSItem extends Item{
	private static final long serialVersionUID = -9128122250005568441L;
	final String BASE_NAME;
	@Override
	public final String getName() {
		return BASE_NAME;
	}
	public BSItem(String baseName,int stackCap, DotPaint paint) {
		super(stackCap, paint);
		BASE_NAME = baseName;
	}
	public BSItem(String baseName, DotPaint paint) {
		super(GHQ.MAX, paint);
		BASE_NAME = baseName;
	}
	//main role
	public void paintInInventory(int x,int y,int w,int h) {
		super.paintScript.dotPaint(x, y, w, h);
	}
	public void paintInStage(int x,int y,int w,int h) {
		super.paintScript.dotPaint(x, y, w, h);
	}
}
