package unit;

import core.GHQ;
import paint.DotPaint;
import paint.ImageFrame;
import physicis.Dynam;
import physicis.HasDynam;
import thhunit.EnemyBulletLibrary;
import unit.Unit;
import weapon.Weapon;

public class Fairy extends BSUnit{
	private static final long serialVersionUID = -8167654165444569286L;
	public Fairy(int initialGroup) {
		super(70, initialGroup);
	}
	private final Weapon weaponController = EnemyBulletLibrary.getWeaponController(EnemyBulletLibrary.lightBall_S);
	private DotPaint magicCirclePaint;
	@Override
	public final String getName() {
		return "FairyA";
	}
	
	//Dynam
	private final Dynam dynam = new Dynam();
	@Override
	public final Dynam getDynam() {
		return dynam;
	}
	
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaPaint = new ImageFrame("thhimage/YouseiA.png");
		magicCirclePaint = new ImageFrame("thhimage/MagicCircleBlue.png");
		bulletPaint[0] = new ImageFrame("thhimage/LightBallA.png");
	}
	@Override
	public void activeCons() {
		weaponController.defaultIdle();
		final Unit targetEnemy = GHQ.getNearstVisibleEnemy(this);
		if(targetEnemy != null && weaponController.trigger())
			EnemyBulletLibrary.inputBulletInfo(this,EnemyBulletLibrary.lightBall_ROUND,bulletPaint[0],targetEnemy);
	}
	@Override
	public void paint(boolean doAnimation) {
		if(!isAlive())
			return;
		super.paintMode_magicCircle(magicCirclePaint);
		GHQ.paintHPArc((int) dynam.getX(), (int) dynam.getY(), 20,status.get(HP), status.getDefault(HP));
	}
	@Override
	public void setEffect(int kind,HasDynam source) {}
	@Override
	public void setBullet(int kind,HasDynam source) {}
}
