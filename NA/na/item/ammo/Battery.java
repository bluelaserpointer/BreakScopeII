package item.ammo;

import item.ItemData;
import paint.ImageFrame;

public class Battery extends Ammo{
	private static final long serialVersionUID = -4928866416334877003L;
	@Override
	public String name() {
		return "Battery";
	}
	public Battery(int amount) {
		super(ImageFrame.create("picture/battery.png"), amount);
		setStackCap(100);
	}
	@Override
	public boolean isStackable(ItemData item) {
		return item instanceof Ammo_45acp;
	}
}
