package unit;

import core.GHQ;
import item.weapon.ACCAR;
import paint.ImageFrame;
import unit.Unit;

public class HumanGuard2 extends BasicEnemy{
	private static final long serialVersionUID = -8167654165444569286L;
	public HumanGuard2(int initialGroup) {
		super(70, initialGroup);
	}
	@Override
	public final String getName() {
		return "FairyA";
	}
	@Override
	public final HumanGuard2 respawn(int x, int y) {
		super.respawn(x, y);
		mainSlot = addItem(new ACCAR());
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
		mainSlot.reloadIfEquipment();
		final Unit targetEnemy = GHQ.stage().getNearstVisibleEnemy(this);
		if(targetEnemy != null) {
			final double TARGET_ANGLE = dynam.angleTo(targetEnemy);
			if(baseAngle.isDeltaSmaller(TARGET_ANGLE, Math.PI*10/18)) {
				if(baseAngle.spinTo_Suddenly(TARGET_ANGLE, 10) < 0.10)
					mainSlot.use();
			}else
				baseAngle.spinTo_Suddenly(dynam.moveAngle(), 10);
		}else
			baseAngle.spinTo_Suddenly(dynam.moveAngle(), 10);
	}
	@Override
	public void paint(boolean doAnimation) {
		super.paint(true);
	}
}
