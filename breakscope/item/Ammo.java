package item;

import paint.ImageFrame;
import paint.RectPaint;
import unit.Item;

public class Ammo extends BSItem{
	
	public static final int
		AMMO_9MM = 0,
		AMMO_45 = 1;
	public int kind;
	private static final int AMOUNT = 2;
	public static final String ammoNames[] = new String[AMOUNT];
	private static RectPaint ammoPaints[] = new RectPaint[AMOUNT];
	private static final int ammoStackCaps[] = new int[AMOUNT];
	public static void loadResource(){
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
