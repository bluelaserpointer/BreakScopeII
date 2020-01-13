package unit;

import core.GHQ;
import item.weapon.ACCAR;
import paint.ImageFrame;

public class HumanGuard extends BasicEnemy{
	private static final long serialVersionUID = 8209027182300124313L;
	public HumanGuard(int initialGroup) {
		super(120, initialGroup);
	}
	{
		charaSpeed = 6;
	}
	@Override
	public final String name() {
		return "HumanGuard";
	}
	@Override
	public final HumanGuard respawn(int x, int y) {
		super.respawn(x, y);
		mainSlot = addItem(new ACCAR());
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
		mainSlot.reloadIfEquipment();
		final Unit targetEnemy = GHQ.stage().getNearstVisibleEnemy(this);
		if(targetEnemy != null) {
			final double TARGET_ANGLE = point().angleTo(targetEnemy);
			if(angle().isDeltaSmaller(TARGET_ANGLE, Math.PI*10/18)) {
				dstPoint.setXY(targetEnemy);
				if(angle().spinTo_Suddenly(TARGET_ANGLE, 10) < 0.10)
					mainSlot.use();
			}else
				angle().spinTo_Suddenly(point().moveAngle(), 10);
		}else
			angle().spinTo_Suddenly(point().moveAngle(), 10);
		point().approachIfNoObstacles(this, dstPoint, charaSpeed);
	}
}
