package item;

import paint.DotPaint;
import paint.ImageFrame;
import unit.Item;

public class Ammo extends BSItem{
	
	public static final int
		AMMO_9MM = 0,
		AMMO_45 = 1;
	public int kind;
	private static final int KIND_AMOUNT = 2;
	public static final String ammoNames[] = new String[KIND_AMOUNT];
	private static DotPaint ammoPaints[] = new DotPaint[KIND_AMOUNT];
	private static final int ammoStackCaps[] = new int[KIND_AMOUNT];
	public static void loadResource(){
		//9mm
		ammoNames[AMMO_9MM] = "AMMO_9MM";
		ammoPaints[AMMO_9MM] = new ImageFrame("picture/AssaultRifleBullet.png");
		ammoStackCaps[AMMO_9MM] = 64;
		//45acp
		ammoNames[AMMO_45] = "AMMO_45";
		ammoPaints[AMMO_45] = new ImageFrame("picture/HandgunBullet.png");
		ammoStackCaps[AMMO_45] = 64;
	}
	public Ammo(int ammoID, int amount) {
		super(ammoNames[ammoID], ammoStackCaps[ammoID], ammoPaints[ammoID]);
		kind = ammoID;
		this.amount = amount;
	}
	@Override
	public boolean isStackable(Item item) {
		return item instanceof Ammo && ((Ammo)item).kind == kind;
	}
}
