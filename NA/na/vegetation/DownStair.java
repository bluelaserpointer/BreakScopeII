package vegetation;

import core.GHQ;
import engine.NAGame;
import paint.ImageFrame;
import physics.Point;
import preset.unit.Unit;
import preset.vegetation.Vegetation;
import stage.NAStage;
import unit.NAUnit;

public class DownStair extends Vegetation {
	public DownStair() {
		super(ImageFrame.create("picture/map/DownStair.png"), new Point.IntPoint());
	}
	
	@Override
	public void idle() {
		super.idle();
		if(isTarget()) {
			for(Unit rawUnit : GHQ.stage().units) {
				final NAUnit unit = (NAUnit)rawUnit;
				if(unit.isHostile(NAGame.controllingUnit()) && unit.point().inRangeSq(this, 100*100)) {
					NAStage.gameOver = true;
				}
			}
		}
	}
	final ImageFrame targetIF = ImageFrame.create("picture/map/target.png");
	@Override
	public void paint() {
		super.paint();
		if(this.isTarget()) {
			targetIF.dotPaint(point());
		}
	}
	public boolean isTarget() {
		return true;
	}
}
