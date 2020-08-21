package ui;

import java.awt.Color;

import core.GHQ;
import engine.NAGame;
import gui.GUIParts;
import paint.ImageFrame;

public class ZoomSliderBar extends GUIParts {
	private final ImageFrame sliderBarIF = ImageFrame.create("picture/gui/SliderBar.png");
	private final ImageFrame sliderIF = ImageFrame.create("picture/gui/Slider.png");
	
	protected double sliderValue = 0.5;
	public ZoomSliderBar() {
		NAGame.playerCamera.zoom = sliderValue()*1.5 + 0.5;
	}
	//main role
	@Override
	public void paint() {
		sliderBarIF.rectPaint(left(), top(), width(), height());
		sliderIF.dotPaint_capSize(left() + (int)(sliderValue*width()), point().intY(), (int)(height()*1.5));
		GHQ.getG2D(Color.WHITE);
		GHQ.drawStringGHQ(GHQ.DF0_00.format(sliderValue*1.5 + 0.5), point().intX(), point().intY());
	}
	//control
	public void setSliderValue(double value) {
		sliderValue = value;
		if(sliderValue < 0.0)
			sliderValue = 0.0;
		else if(sliderValue > 1.0)
			sliderValue = 1.0;
	}
	//information
	public double sliderValue() {
		return sliderValue;
	}
}
