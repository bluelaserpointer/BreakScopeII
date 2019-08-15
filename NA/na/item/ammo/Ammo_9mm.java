package item.ammo;

import item.ItemData;
import item.weapon.ACCAR.ACCAR_AMMO;
import paint.ImageFrame;

public class Ammo_9mm extends Ammo implements ACCAR_AMMO{
	private static final long serialVersionUID = 1468750527265985047L;
	@Override
	public String getName() {
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
