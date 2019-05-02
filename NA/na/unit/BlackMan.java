package unit;

import core.GHQ;
import item.Equipment;
import paint.ImageFrame;
import unit.Unit;

public class BlackMan extends BasicEnemy{
	private static final long serialVersionUID = 474244930122842766L;
	public BlackMan(int initialGroup) {
		super(120, initialGroup);
	}
	{
		charaSpeed = 2;
	}
	@Override
	public final String getName() {
		return "BlackMan";
	}
	@Override
	public final void respawn(int x, int y) {
		super.respawn(x, y);
		mainWeapon = super.getWeapon(new Equipment(Equipment.ACCAR));
	}
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaPaint = new ImageFrame("thhimage/BlackBall.png");
	}
	@Override
	public void extendIdle() {
		mainWeapon.startReloadIfNotDoing();
		final Unit targetEnemy = GHQ.getNearstVisibleEnemy(this);
		if(targetEnemy != null) {
			dynam.setAngle(dynam.getAngle(charaDstX = targetEnemy.getDynam().getX(),charaDstY = targetEnemy.getDynam().getY()));
			mainWeapon.trigger(this);
		}
		dynam.approachIfNoObstacles(this, charaDstX, charaDstY, charaSpeed);
	}
}
