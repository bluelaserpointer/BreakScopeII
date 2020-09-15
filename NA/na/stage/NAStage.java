package stage;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Random;
import java.util.function.Predicate;

import core.GHQ;
import core.GHQObject;
import core.GHQObjectType;
import engine.NAGame;
import item.defenceKit.DefenceKit;
import liquid.Liquid;
import liquid.NALiquidState;
import paint.ImageFrame;
import liquid.LiquidTag;
import liquid.MixedLiquid;
import physics.Point;
import physics.stage.GHQStage;
import physics.stage.GridArrayList;
import physics.stage.GridBitSet;
import physics.stage.Grids;
import preset.item.ItemData;
import preset.unit.Unit;
import unit.NAUnit;

public class NAStage extends GHQStage {
	
	public static final int CELL_SIZE = 25;
	private final GridBitSet seenMark;
	private final GridBitSet enemySightMark;
	private final GridBitSet playerSightMark;
	private final GridArrayList<Liquid> liquidGrids;
	private final GridArrayList<Liquid> gasGrids;
	

	private final ImageFrame[] tileIFs_normal = new ImageFrame[] {
			ImageFrame.create("picture/map/Tile_empty.png"),
			ImageFrame.create("picture/map/Tile_empty_dark.png"),
		};
	private final ImageFrame[] tileIFs_special = new ImageFrame[] {
			ImageFrame.create("picture/map/Tile_40_percent.png"),
			ImageFrame.create("picture/map/Tile_30_percent.png"),
			ImageFrame.create("picture/map/Tile_20_percent.png"),
			ImageFrame.create("picture/map/Tile_minor_10_percent.png"),
			ImageFrame.create("picture/map/Tile_a_percent.png"),
		};
	public class LiquidInfo {
		public Liquid fluid;
		public int xPos, yPos;
	}
	
	public NAStage(int width, int height) {
		super(width, height);
		seenMark = new GridBitSet(this, CELL_SIZE);
		enemySightMark = new GridBitSet(this, CELL_SIZE);
		playerSightMark = new GridBitSet(this, CELL_SIZE);
		liquidGrids = new GridArrayList<Liquid>(this, CELL_SIZE);
		gasGrids = new GridArrayList<Liquid>(this, CELL_SIZE);
	}
	public static NAStage generate(GHQStage sample) {
		NAStage stage = new NAStage(sample.width(), sample.height());
		stage.units.addAll(sample.units);
		stage.bullets.addAll(sample.bullets);
		stage.items.addAll(sample.items);
		stage.structures.addAll(sample.structures);
		stage.vegetations.addAll(sample.vegetations);
		return stage;
	}
	
	//main role
	private static final Predicate<GHQObject> judgeIsDefenceKit = (object) -> object instanceof DefenceKit;
	private static final Predicate<GHQObject> judgeIsNotDefenceKit = judgeIsDefenceKit.negate();
	public static boolean gameOver;
	@Override
	public void idle() {
		//background
		final int TILE_SIZE = 25;
		final int startX = Math.max(GHQ.fieldScreenLeft()/TILE_SIZE - 2, 0);
		final int startY = Math.max(GHQ.fieldScreenTop()/TILE_SIZE - 2, 0);
		final int endX = startX + GHQ.fieldScreenW()/TILE_SIZE + 4;
		final int endY = startY + GHQ.fieldScreenH()/TILE_SIZE + 4;
		Random random = new Random();
		final int rate = GHQ.stage().width()/TILE_SIZE;
		for(int xi = startX;xi < endX;xi++) {
			for(int yi = startY;yi < endY;yi++) {
				random.setSeed(xi*rate + yi);
				random.nextDouble();
				if(seenMark.get_cellPos(xi, yi, false)) {
					tileIFs_normal[(xi + yi) % 2].dotPaint_capSize(xi*TILE_SIZE + TILE_SIZE/2, yi*TILE_SIZE + TILE_SIZE/2, TILE_SIZE);
				}
				//final double value = random.nextDouble();
//						final double angle = random.nextInt(tileIFs.length)*Math.PI/2;
//						if(value < 0.4)
//							tileIFs[0].dotPaint_turn(xi*TILE_SIZE + TILE_SIZE/2, yi*TILE_SIZE + TILE_SIZE/2, angle);
//						else if(value < 0.7)
//							tileIFs[1].dotPaint_turn(xi*TILE_SIZE + TILE_SIZE/2, yi*TILE_SIZE + TILE_SIZE/2, angle);
//						else if(value < 0.9)
//							tileIFs[2].dotPaint_turn(xi*TILE_SIZE + TILE_SIZE/2, yi*TILE_SIZE + TILE_SIZE/2, angle);
//						else if(value < 0.95)
//							tileIFs[3].dotPaint_turn(xi*TILE_SIZE + TILE_SIZE/2, yi*TILE_SIZE + TILE_SIZE/2, angle);
//						else
//							tileIFs[4].dotPaint_turn(xi*TILE_SIZE + TILE_SIZE/2, yi*TILE_SIZE + TILE_SIZE/2, angle);
			}
		}
		GHQObject.removeDeletedFromList(bulletCollisionGroup);
		////////////
		//Fluid
		////////////
		final LinkedList<Liquid> activeLiquids = new LinkedList<Liquid>();
		for(Liquid liquid : liquidGrids.list()) {
			if(liquid != null) {
				liquid.idle();
				if(liquid.depth() > liquid.flowResistance()) {
					activeLiquids.add(liquid);
				}
			}
		}
		for(Liquid liquid : activeLiquids)
			liquid.expand();
		////////////
		//Objects idle
		////////////
		GHQ.stage().idle(GHQObjectType.VEGETATION);
		GHQ.stage().idle(GHQObjectType.ITEM);
		GHQ.stage().items.defaultTraverse(judgeIsNotDefenceKit);
		GHQ.stage().idle(GHQObjectType.STRUCTURE);
		GHQ.stage().idle(GHQObjectType.UNIT);
		GHQ.stage().items.defaultTraverse(judgeIsDefenceKit);
		GHQ.stage().idle(GHQObjectType.BULLET);
		//affects
		for(Unit unit : units)
			affectByLiquids(unit);
		for(ItemData item : items)
			affectByLiquids(item);
		////////////
		//Gas
		////////////
		activeLiquids.clear();
		for(Liquid liquid : gasGrids.list()) {
			if(liquid != null) {
				liquid.idle();
				if(liquid.depth() > liquid.flowResistance()) {
					activeLiquids.add(liquid);
				}
			}
		}
		for(Liquid liquid : activeLiquids)
			liquid.expand();
		GHQ.stage().idle(GHQObjectType.EFFECT);
		////////////
		//EnlightVisibleArea
		////////////
		{
			final int xStart = seenMark.screenLeftXPos(), yStart = seenMark.screenTopYPos();
			final int xEnd = xStart + seenMark.gridsToFillScreenWidth(), yEnd = yStart + seenMark.gridsToFillScreenHeight();
			final Color enemySeenColor = new Color(1F, 0F, 0F, 0.5F);
			final Color playerNotSeenColor = new Color(0F, 0F, 0F, 0.5F);
			final Color combinedColor = new Color(170, 0, 0, 128);
			final Color notSeenGrayColor = new Color(0.1F, 0.1F, 0.1F);
			for(int xPos = xStart; xPos < xEnd; ++xPos) {
				boolean toDrawNotSeenWithBlack = (xPos + yStart) % 2 == 0;
				for(int yPos = yStart; yPos < yEnd; ++yPos) {
					toDrawNotSeenWithBlack = !toDrawNotSeenWithBlack;
					if(seenMark.get_cellPos(xPos, yPos, false)) {
						final boolean isGrayMarked = !playerSightMark.get_cellPos(xPos, yPos, false);
						final boolean isRedMarked = enemySightMark.get_cellPos(xPos, yPos, false);
						if(isRedMarked && isGrayMarked)
							seenMark.fillGrid(GHQ.getG2D(combinedColor), xPos, yPos);
						else if(isRedMarked && !isGrayMarked) //fill transparent red area that in an enemy's sight
							;//seenMark.fillGrid(GHQ.getG2D(enemySeenColor), xPos, yPos);
						else if(!isRedMarked && isGrayMarked) //fill transparent gray area that in the player's sight
							seenMark.fillGrid(GHQ.getG2D(playerNotSeenColor), xPos, yPos);
					} else { //fill black area that never seen before
						seenMark.fillGrid(GHQ.getG2D(toDrawNotSeenWithBlack ? Color.BLACK : notSeenGrayColor), xPos, yPos);
					}
				}
			}
		}
		////////////
		//CalcurateVisibleArea
		////////////
		{
			final int xStart = seenMark.screenLeftXPos(), yStart = seenMark.screenTopYPos();
			final int xEnd = xStart + seenMark.gridsToFillScreenWidth(), yEnd = yStart + seenMark.gridsToFillScreenHeight();
			//controlling unit
			NAUnit player = NAGame.controllingUnit();
			seenMark.clear();
			playerSightMark.clear();
			for(int xPos = 0; xPos < seenMark.xGrids(); ++xPos) {
				for(int yPos = 0; yPos < seenMark.yGrids(); ++yPos) {
					if(player.isVisible(seenMark.getPosPoint(xPos, yPos))) {
						seenMark.set_cellPos(xPos, yPos);
						playerSightMark.set_cellPos(xPos, yPos);
					}
				}
			}
			//other unit
			enemySightMark.clear();
			for(Unit ver : GHQ.stage().units) {
				final NAUnit unit = (NAUnit)ver;
				if(unit.isHostileToControllingUnit() && player.isVisible(unit)) { //hostile unit
					for(int xPos = 0; xPos < seenMark.xGrids(); ++xPos) {
						for(int yPos = 0; yPos < seenMark.yGrids(); ++yPos) {
							if(seenMark.get_cellPos(xPos, yPos, false) && unit.isVisible(seenMark.getPosPoint(xPos, yPos))) {
								enemySightMark.set_cellPos(xPos, yPos);
							}
						}
					}
				}
			}
		}
		if(gameOver) {
			GHQ.getG2D(Color.RED);
			GHQ.drawStringGHQ("GAME_OVER", GHQ.screenW()/2, GHQ.screenH() - 15);
		}
	}
	
	//information
	@Override
	public double visibility(int x1, int y1, int x2, int y2) {
		double currentVisibility = super.visibility(x1, y1, x2, y2);
		if(currentVisibility == Double.NEGATIVE_INFINITY)
			return currentVisibility;
		else {
			final double dx = x2 - x1, dy = y2 - y1;
			final double rate = CELL_SIZE/Math.sqrt(dx*dx + dy*dy);
			final double sx = dx*rate, sy = dy*rate;
			final double times = 1/rate;
			for(int i = 0; i < times ;++i) {
				final Liquid liquid = gasGrids.get_stageCod(x1, y1);
				if(liquid != null) {
					final double visibility = liquid.visibility();
					if(visibility == Double.NEGATIVE_INFINITY)
						return Double.NEGATIVE_INFINITY;
					currentVisibility -= visibility;
				}
				x1 += sx; y1 += sy;
			}
			final Liquid liquid = gasGrids.get_stageCod(x2, y2);
			if(liquid != null)
				currentVisibility -= liquid.visibility();
		}
		return currentVisibility;
	}
	public Grids gridPainter() {
		return seenMark;
	}
	public GridBitSet seenMark() {
		return seenMark;
	}
	public GridBitSet enemySightMark() {
		return enemySightMark;
	}
	public GridBitSet playerSightMark() {
		return playerSightMark;
	}
	public GridArrayList<Liquid> liquidGrids() {
		return liquidGrids;
	}
	public GridArrayList<Liquid> gasGrids() {
		return gasGrids;
	}
	public Liquid makeLiquid(LiquidTag liquidTag, NALiquidState state, double initialDepth) {
		switch(state) {
		case GAS:
			return new MixedLiquid(gasGrids, state, initialDepth, liquidTag);
		case OIL_SOLUABLE:
		case WATER_SOLUABLE:
			return new MixedLiquid(liquidGrids, state, initialDepth, liquidTag);
		default:
			return null;
		}
	}
	public Liquid addLiquid(Point point, LiquidTag liquidTag, NALiquidState state, double initialDepth) {
		return makeLiquid(liquidTag, state, initialDepth).expand(point);
	}
	public void affectByLiquids(GHQObject object) {
		final LinkedList<LiquidTag> tags = new LinkedList<LiquidTag>();
		for(Liquid liquid : liquidGrids.getIntersected(object)) {
			for(LiquidTag tag : ((MixedLiquid)liquid).liquidTags())
				tags.add(tag);
			for(LiquidTag tag : tags)
				tag.affect(liquid, object);
			tags.clear();
		}
		for(Liquid liquid : gasGrids.getIntersected(object)) {
			for(LiquidTag tag : ((MixedLiquid)liquid).liquidTags())
				tags.add(tag);
			for(LiquidTag tag : ((MixedLiquid)liquid).liquidTags()) {
				tag.affect(liquid, object);
			}
			tags.clear();
		}
	}
}
