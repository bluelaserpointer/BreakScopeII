package item;

import core.GHQ;
import unit.Item;

public class Ammo extends InventoryItem{
	
	public static final int
		AMMO_9MM = 0,
		AMMO_45 = 1;
	public int kind;
	public static final String ammoNames[];
	private static int ammoImageIIDs[];
	static {
		ammoNames = new String[2];
		//9mm
		ammoNames[AMMO_9MM] = "AMMO_9MM";
		ammoImageIIDs[AMMO_9MM] = GHQ.loadImage("picture/DoubleM9.png");
		//45acp
		ammoNames[AMMO_45] = "AMMO_45";
		ammoImageIIDs[AMMO_45] = GHQ.loadImage("picture/HandgunBullet.png");
	}
	public Ammo(int ammoID) {
		super(ammoNames[ammoID], ammoImageIIDs[ammoID]);
		kind = ammoID;
	}
	@Override
	public boolean isStackable(Item item) {
		return item instanceof Ammo && ((Ammo)item).kind == kind;
	}
}
