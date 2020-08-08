package item.defenceKit;

import engine.NAGame;
import item.ammo.AmmoType;
import item.ammo.storage.AmmoStorage;
import paint.ImageFrame;
import preset.item.ItemData;
import storage.TableStorage;

public class AmmoBox extends DefenceKitOnGround {
	public AmmoBox() {
		super(ImageFrame.create("picture/map/ArmyBox1.png"), ImageFrame.create("picture/map/ArmyBox2.png"));
	}
	public static final TableStorage<ItemData> storage = new TableStorage<>(5, 3, null);
	public static final AmmoStorage ammoStorage = new AmmoStorage(AmmoType.values());
	@Override
	public double weight() {
		return 200;
	}
	@Override
	public void openInfoUI() {
		NAGame.openInventoryInvester(storage);
	}
}
