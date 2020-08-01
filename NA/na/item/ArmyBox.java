package item;

import core.GHQ;
import damage.NADamage;
import paint.ImageFrame;
import paint.dot.DotPaintResizer;
import preset.item.ItemData;
import storage.TableStorage;

public class ArmyBox extends NACollisionableItem {
	public ArmyBox() {
		super(new DotPaintResizer(ImageFrame.create("picture/map/ArmyBox1.png"), 0.5), 100);
	}
	public final TableStorage<ItemData> inventory = new TableStorage<ItemData>(5, 3, ItemData.BLANK_ITEM);
//	@Override
//	public boolean interact(NAUnit unit) {
//		NAGame.openInventoryInvester(inventory);
//		return true;
//	}
	@Override
	public void killed() {
		for(int i = inventory.nextNonspaceIndex(); i != -1; i = inventory.nextNonspaceIndex(i)) {
			GHQ.stage().addItem(inventory.remove(i).drop((int)(point().doubleX() + GHQ.random2(-50,50)), (int)(point().doubleY() + GHQ.random2(-50,50))));
		}
	}
	@Override
	public double weight() {
		return 10;
	}
	@Override
	public double damageRes(NADamage damage) {
		return 0;
	}
	@Override
	public boolean stackable(ItemData item) {
		return false;
	}
}
