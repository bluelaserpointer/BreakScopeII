package vegetation;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import core.GHQ;
import item.ammo.Ammo;
import item.ammo.AmmoType;
import paint.ImageFrame;
import physics.HitGroup;
import preset.item.ItemData;
import preset.vegetation.Vegetation;

public class AmmoArea extends Vegetation {
	public static LinkedList<AmmoArea> ammoAreaList = new LinkedList<>();
	protected List<Ammo> dropItems;
	protected int dropItemsAmount[] = new int[AmmoType.values().length];
	public AmmoArea(int x, int y) {
		super(ImageFrame.create("picture/map/ammoArea.png"), x, y);
		AmmoArea.ammoAreaList.add(this);
	}

	@Override
	public void idle() {
		super.idle();
		if(GHQ.nowFrame() % 10 == 0) {
			dropItems = detectsDropItems();
		}
	}
	@Override
	public void paint() {
		super.paintScript.rectPaint(cx() - 100, cy() - 100, 200);
		if(dropItems != null && dropItems.size() > 0) {
			GHQ.getG2D(Color.CYAN);
			for(AmmoType ammoType : AmmoType.values()) {
				final int ordinal = ammoType.ordinal();
				GHQ.drawString_left(ammoType.name() + ": " + dropItemsAmount[ordinal], left() + ordinal%2*100, bottom() + 50 + ordinal/2*30, 20);
			}
		}
	}
	
	protected List<Ammo> detectsDropItems() {
		final LinkedList<Ammo> ammoList = new LinkedList<>();
		Arrays.fill(dropItemsAmount, 0);
		for(ItemData item : GHQ.stage().items) {
			if(item instanceof Ammo && item.intersectsRect(HitGroup.HIT_ALL, cx(), cy(), 200, 200)) {
				final Ammo ammo = (Ammo)item;
				ammoList.add(ammo);
				++dropItemsAmount[ammo.type().ordinal()];
			}
		}
		return ammoList;
	}
	
	public List<Ammo> dropItems() {
		return dropItems;
	}
}
