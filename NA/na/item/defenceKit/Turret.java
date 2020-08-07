package item.defenceKit;

import core.GHQ;
import engine.NAGame;
import paint.ImageFrame;
import preset.item.ItemData;
import preset.unit.Unit;
import storage.TableStorage;

public class Turret extends DefenceKitOnWall {
	public Turret() {
		super(ImageFrame.create("picture/player_change_1.png"), ImageFrame.create("picture/human1-1.png"));
		super.lastOwner = NAGame.controllingUnit();
	}
	//equipments
	protected final TableStorage<ItemData> storage = new TableStorage<>(1, 1, null);
	@Override
	public void idle() {
		super.idle();
		if(!installed())
			return;
		final Unit unit = GHQ.stage().getNearstVisibleEnemy(lastOwner);
	}
	@Override
	public double weight() {
		return 100;
	}
	@Override
	public void openInfoUI() {
		NAGame.openInventoryInvester(storage);
	}
}
