package structure;

import java.util.Arrays;

import damage.DamageComposition;
import damage.DamageMaterial;
import damage.NADamage;
import paint.ImageFrame;
import preset.structure.Tile;

public class NATile extends Tile {
	ImageFrame wallTopIF = ImageFrame.create("picture/map/Wall_1.png");
	ImageFrame wallSideIF = ImageFrame.create("picture/map/WallSide_2.png");
	NATileEnum tileType;
	double hps[];
	double armors[];
	public NATile(NATileEnum tileType, int ox, int oy, int x_tiles, int y_tiles) {
		super(ox, oy, x_tiles, y_tiles);
		final int totalTiles = x_tiles*y_tiles;
		hps = new double[totalTiles];
		armors = new double[totalTiles];
		this.tileType = tileType;
		Arrays.fill(hps, tileType.hp);
		Arrays.fill(armors, tileType.armor);
	}
	public void attackedLastHitTile(NADamage damage) {
		final int hitPos = super.lastHitTilePos();
		for(DamageComposition dmgCompo : damage.compositions()) {
			if(dmgCompo.material() == DamageMaterial.Poi)
				continue;
			final double value = dmgCompo.damage();
			if(dmgCompo.material() == DamageMaterial.Cold) {
				armors[hitPos] -= value;
			}
			hps[hitPos] -= value;
			if(hps[hitPos] <= 0) {
				((TileHitShape)hitShape()).aliveTiles().set(hitPos, false);
			}
		}
	}
	@Override
	protected void paintCell(int x, int y, int w, int h) {
		//if(!GHQ.stage().structures.shapeIntersected_dot(x + w/2, (int)(y + h*1.25)))
			wallSideIF.rectPaint(x, y + TILE_SIZE, w, (int)(h*2));
		wallTopIF.rectPaint(x, y, w, h);
	}
}
