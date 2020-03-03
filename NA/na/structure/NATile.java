package structure;

import core.GHQ;
import paint.ImageFrame;

public class NATile extends Tile {
	ImageFrame wallTopIF = ImageFrame.create("picture/map/WallTop.png");
	ImageFrame wallSideIF = ImageFrame.create("picture/map/WallSide.png");
	public NATile(int ox, int oy, int x_tiles, int y_tiles) {
		super(ox, oy, x_tiles, y_tiles);
	}
	@Override
	protected void paintCell(int x, int y, int w, int h) {
		if(!GHQ.stage().structures.intersected_dot(x + w/2, (int)(y + h*1.25)))
			wallSideIF.rectPaint(x, y + TileHitShape.TILE_SIZE, w, h/2);
		wallTopIF.rectPaint(x, y, w, h);
	}
}
