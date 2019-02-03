package chara;

import core.DynamInteractable;
import core.GHQ;
import weapon.Weapon;

public class Fairy extends UserChara{
	{
		charaSize = 70;
	}
	private final Weapon weaponController = EnemyBulletLibrary.getWeaponController(EnemyBulletLibrary.lightBall_S);
	private final int bulletIID[] = new int[10];
	private int magicCircleIID;
	@Override
	public final String getName() {
		return "FairyA";
	}
	
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaIID = GHQ.loadImage("YouseiA.png");
		magicCircleIID = GHQ.loadImage("MagicCircleBlue.png");
		bulletIID[0] = GHQ.loadImage("LightBallA.png");
	}
	@Override
	public void activeCons() {
		weaponController.defaultIdle();
		final Chara targetEnemy = GHQ.getNearstVisibleEnemy(this);
		if(targetEnemy != null && weaponController.trigger())
			EnemyBulletLibrary.inputBulletInfo(this,EnemyBulletLibrary.lightBall_ROUND,bulletIID[0],targetEnemy);
	}
	@Override
	public void paint(boolean doAnimation) {
		if(charaHP <= 0)
			return;
		super.paintMode_magicCircle(magicCircleIID);
		GHQ.paintHPArc((int) dynam.getX(), (int) dynam.getY(), 20,charaHP, charaBaseHP);
	}
	@Override
	public void setEffect(int kind,DynamInteractable source) {}
	@Override
	public void setBullet(int kind,DynamInteractable source) {}
}
