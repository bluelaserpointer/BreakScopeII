package unit;

import core.GHQ;
import item.Equipment;
import paint.DotPaint;
import paint.ImageFrame;
import unit.Unit;

public class Fairy extends BasicEnemy{
	private static final long serialVersionUID = -8167654165444569286L;
	public Fairy(int initialGroup) {
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
		charaPaint = new ImageFrame("thhimage/YouseiA.png");
		magicCirclePaint = new ImageFrame("thhimage/MagicCircleBlue.png");
	}
	@Override
	public void extendIdle() {
		mainWeapon.startReloadIfNotDoing();
		final Unit targetEnemy = GHQ.getNearstVisibleEnemy(this);
		if(targetEnemy != null) {
			this.dynam.setAngleToTarget(targetEnemy);
			mainWeapon.trigger(this);
		}
	}
	@Override
	public void paint(boolean doAnimation) {
		super.paintMode_magicCircle(magicCirclePaint);
		super.paint(doAnimation);
	}
}
