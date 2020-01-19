package ui;

import java.awt.Color;

import core.GHQ;
import gui.ClickMenu;
import gui.TextButton;
import item.ItemData;
import item.weapon.Equipment;
import paint.ColorFilling;
import paint.ColorFraming;

public class ItemRCMenu extends ClickMenu<ItemData>{
	public ItemRCMenu() {
		super(80, 20);
		super.setBGColor(Color.WHITE);
		addNewLine(new TextButton("equip", new ColorFilling(Color.LIGHT_GRAY), new ColorFraming(Color.GRAY, GHQ.stroke1)) {
			@Override
			public void clicked() {
				final ItemData item = get();
				if(item instanceof Equipment)
					((Equipment)item).equipToOwner();
			}
		});
		addNewLine(new TextButton("throw", new ColorFilling(Color.GRAY), new ColorFraming(Color.GRAY, GHQ.stroke1)));
	}
}
