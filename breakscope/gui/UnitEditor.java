package gui;

import java.awt.Color;

import core.GHQ;
import engine.Engine_BS;
import paint.ColorFilling;
import paint.ColorFraming;
import unit.BlackMan;
import unit.Fairy;
import unit.Unit;
import unit.WhiteMan;

public class UnitEditor extends ClickMenu<Unit>{
	
	//label
	private final TitledLabel nameLabel;
	public UnitEditor() {
		super("UnitEditor", new ColorFraming(Color.BLACK, GHQ.stroke1), 150, 24, 2);
		addEmptyLine();
		addPartsLine(nameLabel = new TitledLabel("unitNameLabel", new ColorFilling(Color.WHITE)) {
			@Override
			public void typeEnded(String text) {
				final Unit GENERATED_UNIT;
				switch(text){
				case "WHITE_MAN":
					GENERATED_UNIT = GHQ.addUnit(Unit.initialSpawn(new WhiteMan(Engine_BS.ENEMY), (int)targetObject.dynam.getX(), (int)targetObject.dynam.getY()));
					break;
				case "BLACK_MAN":
					GENERATED_UNIT = GHQ.addUnit(Unit.initialSpawn(new BlackMan(Engine_BS.ENEMY), (int)targetObject.dynam.getX(), (int)targetObject.dynam.getY()));
					break;
				case "FAIRY":
					GENERATED_UNIT = GHQ.addUnit(Unit.initialSpawn(new Fairy(Engine_BS.ENEMY), (int)targetObject.dynam.getX(), (int)targetObject.dynam.getY()));
					break;
				default:
					GENERATED_UNIT = null;
					return;
				}
				GHQ.deleteUnit(targetObject);
				targetObject = GENERATED_UNIT;
			}
		});
		nameLabel.setTitle("EnemyName");
		addParts(new InputOptionList(nameLabel)).addWord("WHITE_MAN", "BLACK_MAN", "FAIRY");
		addEmptyLine();
		addPartsLine(new TitledLabel("dumpLabel1", new ColorFilling(Color.WHITE)), new TitledLabel("dumpLabel2", new ColorFilling(Color.WHITE)));
	}
	
	//information
	@Override
	public boolean isMouseEntered() {
		return GHQ.isMouseInArea_Screen(x, y, w, h);
	}
}
