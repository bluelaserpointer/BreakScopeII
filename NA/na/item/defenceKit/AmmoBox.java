package item.defenceKit;

import engine.NAGame;
import paint.ImageFrame;
import preset.item.ItemData;
import storage.TableStorage;

public class AmmoBox extends DefenceKitOnGround {
	public AmmoBox() {
		super(ImageFrame.create("picture/map/ArmyBox1.png"), ImageFrame.create("picture/map/ArmyBox2.png"));
	}
	public static final TableStorage<ItemData> storage = new TableStorage<>(5, 3, null);
	@Override
	public double weight() {
		return 200;
	}
	@Override
	public void openInfoUI() {
		NAGame.openInventoryInvester(storage);
	}
}
