package vegetation;

import paint.ImageFrame;
import physics.Point;
import preset.vegetation.Vegetation;

public class DownStair extends Vegetation {
	public DownStair() {
		super(ImageFrame.create("picture/map/DownStair.png"), new Point.IntPoint());
	}
}
