package vegetation;

import paint.ImageFrame;
import preset.vegetation.Vegetation;

public class AmmoArea extends Vegetation {
	public AmmoArea(int x, int y) {
		super(ImageFrame.create("picture/map/ammoArea.png"), x, y);
	}

	@Override
	public void paint() {
		super.paintScript.rectPaint(cx() - 100, cy() - 100, 200);
	}
	
}
