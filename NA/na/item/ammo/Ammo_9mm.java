package item.ammo;

import item.ItemData;
import paint.ImageFrame;
import weapon.Type56.ACCAR_AMMO;

public class Ammo_9mm extends Ammo implements ACCAR_AMMO{
	@Override
	public String name() {
		return "AMMO_9MM";
	}
	public Ammo_9mm(int amount) {
		super(ImageFrame.create("picture/AssaultRifleBullet.png"), amount);
		setStackCap(64);
	}
	@Override
	public boolean isStackable(ItemData item) {
		return item instanceof Ammo_9mm;
	}
}
