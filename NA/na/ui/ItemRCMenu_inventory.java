package ui;

import java.awt.Color;
import java.awt.event.MouseEvent;

import core.GHQ;
import gui.ClickMenu;
import gui.TextButton;
import item.ItemData;
import item.equipment.Equipment;
import paint.ColorFilling;
import paint.ColorFraming;

public class ItemRCMenu_inventory extends ClickMenu<ItemData> {
	public ItemRCMenu_inventory() {
		super(80, 20);
		super.setBGColor(Color.WHITE);
		addNewLine(new TextButton("equip", new ColorFilling(Color.LIGHT_GRAY), new ColorFraming(Color.GRAY, GHQ.stroke1)) {
			@Override
			public boolean clicked(MouseEvent e) {
				super.clicked(e);
				final ItemData item = get();
				if(item instanceof Equipment)
					((Equipment)item).equipToOwner();
				return true;
			}
		});
		addNewLine(new TextButton("throw", new ColorFilling(Color.GRAY), new ColorFraming(Color.GRAY, GHQ.stroke1)));
	}
}
