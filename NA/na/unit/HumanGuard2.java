package unit;

import java.awt.Color;

import core.GHQ;
import engine.NAGame;
import engine.TowerDefence;
import item.ammo.AmmoType;
import item.equipment.weapon.Type56;
import paint.ImageFrame;
import physics.Point;
import physics.Route;
import talent.AllUp;

public class HumanGuard2 extends NAUnit {
	public HumanGuard2() {
		super(1);
		POW_FIXED.setMax(15).setToMax();
		this.addTalent(new AllUp(this));
		this.addTalent(new AllUp(this));
	}
	@Override
	public final String name() {
		return "HumanGuard2";
	}
	@Override
	public final UnitGroup unitGroup() {
		return UnitGroup.GUARD;
	}
	@Override
	public final HumanGuard2 respawn(int x, int y) {
		super.respawn(x, y);
		equip(addItemToStorage(new Type56()));
//		addItemToStorage(AmmoType._7d62.generate(100));
		return this;
	}
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaPaint = ImageFrame.create("picture/grayman.png");
	}
	@Override
	public void idle() {
		super.idle();
//		if(targetUnit == null) {
//			point().approach(GHQ.stage().width()/2, GHQ.stage().height()/2, 1);
//			angle().set(point().angleTo(GHQ.stage().width()/2, GHQ.stage().height()/2));
//		}
		NAUnit u = null;
		if(point().isVisible(u)) {
			point().setSpeed_DA(1.6, point().angleTo(u));
			point().moveIfNoObstacles(this);
		} else {
			if(point().isVisible(NAGame.towerDefence.protectTarget)) {
				point().setSpeed_DA(1.6, point().angleTo(NAGame.towerDefence.protectTarget));
				point().moveIfNoObstacles(this);
			} else {
				Route route = NAGame.cornerNavi.getRoot(this);
				if(route != null) {
					final Point goalPoint = route.peek();
					point().setSpeed_DA(1.0, point().angleTo(goalPoint));
					point().moveIfNoObstacles(this);
	//				GHQ.getG2D(Color.RED).drawOval(goalPoint.intX() - 50, goalPoint.intY() - 50, 100, 100);
		//			route.setDebugEffect(Color.RED, GHQ.stroke5);
				}
			}
		}
	}
}
