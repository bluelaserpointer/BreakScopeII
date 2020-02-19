package ui;

import java.awt.Color;

import core.GHQ;
import gui.GHQTextArea;
import gui.GUIParts;
import gui.ScrollBar;
import unit.Person;

public class Dialog extends GUIParts {
	private Person currentSpeaker;
	private final GHQTextArea textArea = new GHQTextArea();
	private final ScrollBar scrollBar = new ScrollBar(textArea);
	{
		addLast(scrollBar.setScrollSpd(10)).setBGColor(Color.WHITE);
		inputTalk(Person.ASIDE, "Hello world!");
	}
	@Override
	public void paint() {
		super.paint();
		GHQ.drawStringGHQ(currentSpeaker.personalName(), point().intX(), point().intY() + height()/4);
		currentSpeaker.personalIcon().rectPaint(point().intX(), point().intY() + height()/4, height()*3/4, height()*3/4);
	}
	
	//control
	public void inputTalk(Person person, String text) {
		currentSpeaker = person;
		textArea.textArea().setText(text);
	}
	@Override
	public Dialog setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		scrollBar.setBounds(x + h*3/4, y, w - h*3/4, h);
		return this;
	}
	
	//information
	public Person currentTalkingPerson() {
		return currentSpeaker;
	}
}
