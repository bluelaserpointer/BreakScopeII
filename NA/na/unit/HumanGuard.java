package unit;

import core.GHQ;
import item.Equipment;
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
	public final String getName() {
		return "HumanGuard";
	}
	@Override
	public final void respawn(int x, int y) {
		super.respawn(x, y);
		mainWeapon = super.getWeapon(new Equipment(Equipment.ACCAR));
	}
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaPaint = new ImageFrame("picture/SHESS.png");
	}
	@Override
	public void extendIdle() {
		mainWeapon.startReloadIfNotDoing();
		final Unit targetEnemy = GHQ.getNearstVisibleEnemy(this);
		if(targetEnemy != null) {
			final double TARGET_ANGLE = dynam.angleTo(targetEnemy);
			if(baseAngle.isDeltaSmaller(TARGET_ANGLE, Math.PI*10/18)) {
				charaDstX = targetEnemy.getDynam().doubleX();
				charaDstY = targetEnemy.getDynam().doubleY();
				if(baseAngle.spinTo_Suddenly(TARGET_ANGLE, 10) < 0.10)
					mainWeapon.trigger(this);
			}else
				baseAngle.spinTo_Suddenly(dynam.moveAngle(), 10);
		}else
			baseAngle.spinTo_Suddenly(dynam.moveAngle(), 10);
		dynam.approachIfNoObstacles(this, charaDstX, charaDstY, charaSpeed);
	}
}
