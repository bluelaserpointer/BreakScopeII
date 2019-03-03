package item;

import paint.ImageFrame;
import paint.RectPaint;
import unit.Item;

public class Ammo extends InventoryItem{
	
	public static final int
		AMMO_9MM = 0,
		AMMO_45 = 1;
	public int kind;
	public static final String ammoNames[];
	private static RectPaint ammoPaints[];
	private static final int ammoStackCaps[];
	static {
		final int AMOUNT = 2;
		ammoNames = new String[AMOUNT];
		ammoStackCaps = new int[AMOUNT];
		//9mm
		ammoNames[AMMO_9MM] = "AMMO_9MM";
		ammoPaints[AMMO_9MM] = new ImageFrame("picture/DoubleM9.png");
		ammoStackCaps[AMMO_9MM] = 64;
		//45acp
		ammoNames[AMMO_45] = "AMMO_45";
		ammoPaints[AMMO_45] = new ImageFrame("picture/HandgunBullet.png");
		ammoStackCaps[AMMO_45] = 64;
	}
	public Ammo(int ammoID) {
		super(ammoNames[ammoID], ammoStackCaps[ammoID], ammoPaints[ammoID]);
		kind = ammoID;
	}
	@Override
	public boolean isStackable(Item item) {
		return item instanceof Ammo && ((Ammo)item).kind == kind;
	}
}
