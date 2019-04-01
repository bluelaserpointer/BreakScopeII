package unit;

import core.GHQ;
import paint.ImageFrame;
import physicis.HasDynam;
import thhunit.EnemyBulletLibrary;
import unit.Unit;
import weapon.Weapon;

public class BlackMan extends BSUnit{
	private static final long serialVersionUID = 474244930122842766L;
	public BlackMan(int initialGroup) {
		super(120, initialGroup);
	}
	{
		charaSpeed = 2;
	}
	
	private final Weapon weaponController = EnemyBulletLibrary.getWeaponController(EnemyBulletLibrary.lightBall_S);
	@Override
	public final String getName() {
		return "BlackMan";
	}
	
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaPaint = new ImageFrame("thhimage/BlackBall.png");
		bulletPaint[0] = new ImageFrame("thhimage/DarkNiddle3.png");
		bulletPaint[1] = new ImageFrame("thhimage/DodgeMarker.png");
	}
	@Override
	public void activeCons() {
		final int charaX = (int)dynam.getX(),charaY = (int)dynam.getY();
		weaponController.defaultIdle();
		final Unit targetEnemy = GHQ.getNearstVisibleEnemy(this);
		if(targetEnemy != null && weaponController.trigger()) {
			EnemyBulletLibrary.inputBulletInfo(this,EnemyBulletLibrary.BLACK_SLASH_BURST,bulletPaint[0],targetEnemy);
			EnemyBulletLibrary.inputBulletInfo(this,EnemyBulletLibrary.BLACK_SLASH_BURST,bulletPaint[1],targetEnemy);
		}
		Unit unit = GHQ.getNearstEnemy(this, (int)charaX, (int)charaY);
		if(unit != null) 
			dynam.setAngle(dynam.getAngle(charaDstX = unit.getDynam().getX(),charaDstY = unit.getDynam().getY()));
		dynam.approach(charaDstX, charaDstY, charaSpeed);
	}
	@Override
	public void setEffect(int kind,HasDynam source) {}
	@Override
	public void setBullet(int kind,HasDynam source) {}
}
