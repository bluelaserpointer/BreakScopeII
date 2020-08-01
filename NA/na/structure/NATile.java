package structure;

import paint.ImageFrame;
import preset.structure.Tile;

public class NATile extends Tile {
	ImageFrame wallTopIF = ImageFrame.create("picture/map/Wall_1.png");
	ImageFrame wallSideIF = ImageFrame.create("picture/map/WallSide_2.png");
	public NATile(int ox, int oy, int x_tiles, int y_tiles) {
		super(ox, oy, x_tiles, y_tiles);
	}
	@Override
	protected void paintCell(int x, int y, int w, int h) {
		//if(!GHQ.stage().structures.shapeIntersected_dot(x + w/2, (int)(y + h*1.25)))
			wallSideIF.rectPaint(x, y + TILE_SIZE, w, (int)(h*2));
		wallTopIF.rectPaint(x, y, w, h);
	}
}
