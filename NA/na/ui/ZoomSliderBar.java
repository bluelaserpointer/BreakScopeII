package ui;

import gui.GUIParts;
import paint.ImageFrame;

public class ZoomSliderBar extends GUIParts {
	private final ImageFrame sliderBarIF = ImageFrame.create("picture/gui/SliderBar.png");
	private final ImageFrame sliderIF = ImageFrame.create("picture/gui/Slider.png");
	
	protected double sliderValue = 0.5;//1.0;
	public ZoomSliderBar() {
	}
	//main role
	@Override
	public void paint() {
		sliderBarIF.rectPaint(point().intX() - width()/2, point().intY() - height()/2, width(), height());
		sliderIF.dotPaint_capSize(point().intX() - width()/2 + (int)(sliderValue*width()), point().intY(), (int)(height()*1.5));
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
