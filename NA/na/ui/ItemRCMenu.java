package ui;

import java.awt.Color;

import core.GHQ;
import engine.Engine_NA;
import gui.ClickMenu;
import gui.TextButton;
import item.BSItem;
import item.ItemData;
import item.weapon.MainSlot;
import item.weapon.SubSlot;
import paint.ColorFilling;
import paint.ColorFraming;

public class ItemRCMenu extends ClickMenu<ItemData>{
	public ItemRCMenu() {
		super(80, 20);
		super.setBGColor(Color.WHITE);
		addNewLine(new TextButton("equip", new ColorFilling(Color.LIGHT_GRAY), new ColorFraming(Color.GRAY, GHQ.stroke1)) {
			@Override
			public void clicked() {
				final BSItem WEAPON = (BSItem)get();
				if(WEAPON instanceof MainSlot) {
					Engine_NA.getPlayer().mainSlot = (Engine_NA.getPlayer().mainSlot == WEAPON ? BSItem.BLANK_ITEM : WEAPON);
				}else if(WEAPON instanceof SubSlot) {
					Engine_NA.getPlayer().subSlot = (Engine_NA.getPlayer().subSlot == WEAPON ? BSItem.BLANK_ITEM : WEAPON);
				}
			}
		});
		addNewLine(new TextButton("throw", new ColorFilling(Color.GRAY), new ColorFraming(Color.GRAY, GHQ.stroke1)));
	}
}
