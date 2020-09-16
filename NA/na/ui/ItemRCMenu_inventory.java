package ui;

import java.awt.Color;
import java.awt.event.MouseEvent;

import gui.BasicButton;
import gui.ClickMenu;
import item.equipment.Equipment;
import preset.item.ItemData;

public class ItemRCMenu_inventory extends ClickMenu<ItemData> {
	public ItemRCMenu_inventory() {
		super(80, 20);
		super.setBGColor(Color.WHITE);
		addNewLine(new BasicButton("equip") {
			@Override
			public boolean clicked(MouseEvent e) {
				super.clicked(e);
				final ItemData item = get();
				if(item instanceof Equipment)
					((Equipment)item).equipToOwner();
				return true;
			}
		});
		addNewLine(new BasicButton("throw"));
	}
}
