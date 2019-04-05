package gui;

import java.awt.Color;

import core.GHQ;
import paint.ColorFilling;
import paint.ColorFraming;
import unit.Unit;

public class UnitEditor extends ClickMenu<Unit>{
	
	//label
	private final TitledLabel nameLabel;
	public UnitEditor() {
		super("UnitEditor", new ColorFraming(Color.BLACK, GHQ.stroke1), 150, 24, 2);
		super.addPartsLine(nameLabel = new TitledLabel("unitNameLabel", new ColorFilling(Color.WHITE)));
		super.addEmptyLine();
		super.addPartsLine(new TitledLabel("dumpLabel1", new ColorFilling(Color.WHITE)), new TitledLabel("dumpLabel2", new ColorFilling(Color.WHITE)));
	}
	
	//information
	@Override
	public boolean isMouseEntered() {
		return GHQ.isMouseInArea_Screen(x, y, w, h);
	}
}
