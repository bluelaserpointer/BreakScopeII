package ui;

import java.awt.Color;
import java.awt.event.MouseEvent;

import core.GHQ;
import engine.NAGame;
import gui.ClickMenu;
import gui.TextButton;
import paint.ColorFilling;
import paint.ColorFraming;
import preset.item.ItemData;

public class ItemRCMenu_ground extends ClickMenu<ItemData> {
	public ItemRCMenu_ground() {
		super(80, 20);
		super.setBGColor(Color.WHITE);
		addNewLine(new TextButton("捡起", new ColorFilling(Color.LIGHT_GRAY), new ColorFraming(Color.GRAY, GHQ.stroke1)) {
			@Override
			public boolean clicked(MouseEvent e) {
				super.clicked(e);
				NAGame.controllingUnit().addItemToStorage(ItemRCMenu_ground.this.get());
				disableMenu();
				return true;
			}
		});
		addNewLine(new TextButton("调查", new ColorFilling(Color.GRAY), new ColorFraming(Color.GRAY, GHQ.stroke1)));
	}
	@Override
	public void idle() {
		super.idle();
		if(get().point().distance(NAGame.controllingUnit()) > NAGame.controllingUnit().width()*2) {
			disableMenu();
		}
	}
	@Override
	public boolean tryOpen(ItemData item) {
		super.tryOpen(item);
		return true;
	}
	private void disableMenu() {
		super.disable();
	}
}