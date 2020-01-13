package item.ammo;

import item.ItemData;
import paint.ImageFrame;

public class Ammo_45acp extends Ammo{
	private static final long serialVersionUID = -4928866416334877003L;
	@Override
	public String name() {
		return "Ammo_45acp";
	}
	public Ammo_45acp(int amount) {
		super(ImageFrame.create("picture/HandgunBullet.png"), amount);
		setStackCap(64);
	}
	@Override
	public boolean isStackable(ItemData item) {
		return item instanceof Ammo_45acp;
	}
}
