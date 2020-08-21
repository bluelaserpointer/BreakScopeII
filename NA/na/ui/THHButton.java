package ui;

import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import gui.GUIParts;

public class THHButton extends GUIParts {
	final Consumer<MouseEvent> clickEvent;
	
	public THHButton(Consumer<MouseEvent> clickEvent) {
		this.clickEvent = clickEvent;
	}
	@Override
	public boolean clicked(MouseEvent e) {
		clickEvent.accept(e);
		return true;
	}
}
