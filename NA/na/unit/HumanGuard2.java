package unit;

import item.weapon.ACCAR;
import paint.ImageFrame;
import unit.Unit;

public class HumanGuard2 extends BasicEnemy{
	private static final long serialVersionUID = -8167654165444569286L;
	public HumanGuard2(int initialGroup) {
		super(70, initialGroup);
		POW_FIXED.setMax(15).setToMax();
	}
	@Override
	public final String name() {
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
		final Unit targetEnemy = super.getVisibleEnemy();
		if(targetEnemy != null) {
			final double TARGET_ANGLE = point().angleTo(targetEnemy);
			if(angle().isDeltaSmaller(TARGET_ANGLE, Math.PI*10/18)) {
				if(angle().spinTo_Suddenly(TARGET_ANGLE, 10) < 0.10)
					mainSlot.use();
			}else
				angle().spinTo_Suddenly(point().moveAngle(), 10);
		}else
			angle().spinTo_Suddenly(point().moveAngle(), 10);
	}
}
