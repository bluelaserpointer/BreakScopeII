package unit;

import item.equipment.weapon.Type56;
import paint.ImageFrame;

public class HumanGuard extends NAUnit {
	public HumanGuard() {
		super(25);
	}
	@Override
	public final String name() {
		return "HumanGuard";
	}
	@Override
	public final UnitGroup unitGroup() {
		return UnitGroup.GUARD;
	}
	@Override
	public final HumanGuard respawn(int x, int y) {
		super.respawn(x, y);
		body.equip(addItemToStorage(new Type56()));
		return this;
	}
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaPaint = ImageFrame.create("picture/SHESS.png");
	}
	@Override
	public void idle() {
		super.idle();
		//mainEquip().reloadIfEquipment();
		/*final Unit targetEnemy = GHQ.stage().getNearstVisibleEnemy(this);
		if(targetEnemy != null) {
			final double TARGET_ANGLE = point().angleTo(targetEnemy);
			if(angle().isDeltaSmaller(TARGET_ANGLE, Math.PI*10/18)) {
				dstPoint.setXY(targetEnemy);
				if(angle().spinTo_Suddenly(TARGET_ANGLE, 10) < 0.10)
					mainSlot.use(false);
			}else
				angle().spinTo_Suddenly(point().moveAngle(), 10);
		}else
			angle().spinTo_Suddenly(point().moveAngle(), 10);*/
		approachIfNoObstacles(dstPoint, 6);
	}
}
