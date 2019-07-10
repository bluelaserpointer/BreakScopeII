package item;

import paint.ImageFrame;
import paint.dot.DotPaint;

public class Equipment extends BSItem{
	private static final long serialVersionUID = -1892229139354741548L;

	public int EQUIPMENT_ID;
	public Equipment(int equipmentID) {
		super(eqiupmentNames[equipmentID], 1, eqiupmentPaints[equipmentID]);
		amount = 1;
		this.EQUIPMENT_ID = equipmentID;
	}

	private static final int KIND_AMOUNT = 2;
	public static final int
		ACCAR = 0,ELECTRON_SHIELD = 1;
	public static final String eqiupmentNames[] = new String[KIND_AMOUNT];
	private static DotPaint eqiupmentPaints[] = new DotPaint[KIND_AMOUNT];
	public static void loadResource(){
		eqiupmentNames[ACCAR] = "ACCAR";
		eqiupmentPaints[ACCAR] = new ImageFrame("picture/AK.png");
		eqiupmentNames[ELECTRON_SHIELD] = "ELECTRON_SHIELD";
		eqiupmentPaints[ELECTRON_SHIELD] = new ImageFrame("picture/FreezeEffect.png");
	}
}
