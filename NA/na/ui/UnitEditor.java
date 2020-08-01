package ui;

import java.awt.Color;
import java.awt.FontMetrics;

import core.GHQ;
import gui.ClickMenu;
import gui.InputOptionList;
import gui.TitledLabel;
import paint.ColorFraming;
import preset.unit.Unit;
import unit.HumanGuard2;
import unit.HumanGuard;

public class UnitEditor extends ClickMenu<Unit>{
	
	//label
	@Override
	public void idle() {
		super.idle();
	}
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
					GENERATED_UNIT = GHQ.stage().addUnit(Unit.initialSpawn(new HumanGuard(), targetObject.point()));
					break;
				case "FAIRY":
					GENERATED_UNIT = GHQ.stage().addUnit(Unit.initialSpawn(new HumanGuard2(), targetObject.point()));
					break;
				default:
					GENERATED_UNIT = null;
					return;
				}
				targetObject.claimDeleteFromStage();
				targetObject = GENERATED_UNIT;
			}
		});
		nameLabel.setTitle("EnemyName");
		addLast(new InputOptionList(nameLabel)).addWord("WHITE_MAN", "BLACK_MAN", "FAIRY");
		addEmptyLine();
		addNewLine(new TitledLabel() {
			@Override
			public void idle() {
				super.idle();
				final FontMetrics fm = GHQ.getG2D().getFontMetrics(GHQ.initialFont.deriveFont(20F));
			}
		}.setName("dumpLabel1").setBGColor(Color.WHITE), new TitledLabel().setName("dumpLabel2").setBGColor(Color.WHITE));
	}
}
