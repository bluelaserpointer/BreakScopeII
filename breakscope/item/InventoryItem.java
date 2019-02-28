package item;

import core.GHQ;
import unit.Item;

public abstract class InventoryItem extends Item{
	final int IMAGE_IID;
	final String BASE_NAME;
	@Override
	public final String getName() {
		return BASE_NAME;
	}
	public InventoryItem(String baseName,int imageIID) {
		BASE_NAME = baseName;
		IMAGE_IID = imageIID;
	}
	//main role
	public void paintInInventory(int x,int y,int w,int h) {
		GHQ.drawImageGHQ(IMAGE_IID, x, y, w, h);
	}
	public void paintInStage(int x,int y,int w,int h) {
		GHQ.drawImageGHQ(IMAGE_IID, x, y, w, h);
	}
}
