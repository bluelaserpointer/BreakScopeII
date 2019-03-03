package unit;

import core.DynamInteractable;
import core.GHQ;
import paint.ImageFrame;
import thhunit.EnemyBulletLibrary;
import thhunit.THHUnit;
import unit.Unit;
import weapon.Weapon;

public class BlackMan extends THHUnit{
	private static final long serialVersionUID = 474244930122842766L;
	public BlackMan(int initialGroup) {
		super(initialGroup);
	}
	{
		charaSize = 120;
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
		charaPaint = new ImageFrame("BlackBall.png");
		bulletPaint[0] = new ImageFrame("DarkNiddle3.png");
		bulletPaint[1] = new ImageFrame("DodgeMarker.png");
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
		Unit chara = GHQ.getNearstEnemy(this, (int)charaX, (int)charaY);
		if(chara != null) 
			dynam.setAngle(dynam.getAngle(charaDstX = chara.dynam.getX(),charaDstY = chara.dynam.getY()));
		dynam.approach(charaDstX, charaDstY, charaSpeed);
	}
	@Override
	public void setEffect(int kind,DynamInteractable source) {}
	@Override
	public void setBullet(int kind,DynamInteractable source) {}
}
