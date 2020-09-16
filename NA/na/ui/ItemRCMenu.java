package ui;

import java.awt.Color;
import java.awt.event.MouseEvent;

import gui.BasicButton;
import gui.ClickMenu;
import preset.item.ItemData;
import item.equipment.Equipment;

public class ItemRCMenu extends ClickMenu<ItemData>{
	public ItemRCMenu() {
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
