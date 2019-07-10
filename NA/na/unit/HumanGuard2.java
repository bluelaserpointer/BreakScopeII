package unit;

import core.GHQ;
import item.Equipment;
import paint.ImageFrame;
import paint.dot.DotPaint;
import unit.Unit;

public class HumanGuard2 extends BasicEnemy{
	private static final long serialVersionUID = -8167654165444569286L;
	public HumanGuard2(int initialGroup) {
		super(70, initialGroup);
	}
	private DotPaint magicCirclePaint;
	@Override
	public final String getName() {
		return "FairyA";
	}
	@Override
	public final void respawn(int x, int y) {
		super.respawn(x, y);
		mainWeapon = super.getWeapon(new Equipment(Equipment.ACCAR));
	}
	
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaPaint = new ImageFrame("picture/grayman.png");
		magicCirclePaint = new ImageFrame("picture/focus.png");
	}
	@Override
	public void idle() {
		super.idle();
		mainWeapon.startReloadIfNotDoing();
		final Unit targetEnemy = GHQ.stage().getNearstVisibleEnemy(this);
		if(targetEnemy != null) {
			final double TARGET_ANGLE = dynam.angleTo(targetEnemy);
			if(baseAngle.isDeltaSmaller(TARGET_ANGLE, Math.PI*10/18)) {
				if(baseAngle.spinTo_Suddenly(TARGET_ANGLE, 10) < 0.10)
					mainWeapon.trigger(this);
			}else
				baseAngle.spinTo_Suddenly(dynam.moveAngle(), 10);
		}else
			baseAngle.spinTo_Suddenly(dynam.moveAngle(), 10);
	}
	@Override
	public void paint(boolean doAnimation) {
		super.paintMagicCircle(magicCirclePaint);
		super.paint(true);
	}
}
