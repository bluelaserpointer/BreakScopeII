package gui;

import java.awt.Color;

import core.GHQ;
import engine.Engine_NA;
import paint.ColorFraming;
import unit.HumanGuard2;
import unit.HumanGuard;
import unit.Unit;

public class UnitEditor extends ClickMenu<Unit>{
	
	//label
	private final TitledLabel nameLabel;
	public UnitEditor() {
		super(150, 24, 2);
		super.setBGPaint(new ColorFraming(Color.BLACK, GHQ.stroke1));
		addEmptyLine();
		addNewLine(nameLabel = new TitledLabel() {
			{
				setName("unitNameLabel").setBGColor(Color.WHITE);
			}
			@Override
			public void typeEnded(String text) {
				final Unit GENERATED_UNIT;
				switch(text){
				case "HumanGuard":
					GENERATED_UNIT = GHQ.stage().addUnit(Unit.initialSpawn(new HumanGuard(Engine_NA.ENEMY), targetObject.dynam));
					break;
				case "FAIRY":
					GENERATED_UNIT = GHQ.stage().addUnit(Unit.initialSpawn(new HumanGuard2(Engine_NA.ENEMY), targetObject.dynam));
					break;
				default:
					GENERATED_UNIT = null;
					return;
				}
				targetObject.claimDelete();
				targetObject = GENERATED_UNIT;
			}
		});
		nameLabel.setTitle("EnemyName");
		addLast(new InputOptionList(nameLabel)).addWord("WHITE_MAN", "BLACK_MAN", "FAIRY");
		addEmptyLine();
		addNewLine(new TitledLabel().setName("dumpLabel1").setBGColor(Color.WHITE), new TitledLabel().setName("dumpLabel2").setBGColor(Color.WHITE));
	}
	
	//information
	@Override
	public boolean isMouseEntered() {
		return GHQ.isMouseInArea_Screen(x, y, w, h);
	}
}
