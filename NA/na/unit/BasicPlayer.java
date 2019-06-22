package unit;

import core.GHQ;
import item.ItemData;
import storage.Storage;

public abstract class BasicPlayer extends BasicUnit{
	private static final long serialVersionUID = -5939573262134727671L;
	public BasicPlayer(int charaSize, int initialGroup, Storage<ItemData> itemStorageKind) {
		super(charaSize, initialGroup, itemStorageKind);
	}
	@Override
	public final void baseIdle() {
		super.baseIdle();
		final int mouseX = GHQ.getMouseX(), mouseY = GHQ.getMouseY();
		////////////
		//main
		////////////
		// dodge
		if (dodgeOrder)
			dodge(mouseX, mouseY);
		// attack
		if (attackOrder) {
			mainWeapon.trigger(this);
		}
		// spell
		if (spellOrder) {
			spellWeapon.trigger(this);
		}
		dynam.approachIfNoObstacles(this, charaDstX, charaDstY, charaSpeed);
	}
}
