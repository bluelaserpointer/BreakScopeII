package ui;

import java.awt.Color;
import java.awt.event.MouseEvent;

import engine.NAGame;
import gui.BasicButton;
import gui.ClickMenu;
import preset.item.ItemData;

public class ItemRCMenu_ground extends ClickMenu<ItemData> {
	public ItemRCMenu_ground() {
		super(80, 20);
		super.setBGColor(Color.WHITE);
		addNewLine(new BasicButton("捡起") {
			@Override
			public boolean clicked(MouseEvent e) {
				super.clicked(e);
				NAGame.controllingUnit().addItemToStorage(ItemRCMenu_ground.this.get());
				disableMenu();
				return true;
			}
		});
		addNewLine(new BasicButton("调查"));
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