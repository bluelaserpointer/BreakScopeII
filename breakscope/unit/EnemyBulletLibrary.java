package unit;

import static java.lang.Math.*;
import bullet.BulletAgent;
import bullet.BulletBlueprint;
import core.GHQ;
import unit.Unit;
import weapon.Weapon;
import weapon.WeaponInfo;

public class EnemyBulletLibrary extends BulletAgent{
	public static final int lightBall_S = 0,lightBall_ROUND = 1,BLACK_SLASH_BURST = 2,HEAL_SHOTGUN = 3;
	
	public static Weapon getWeaponController(int bulletKind) {
		WeaponInfo.clear();
		switch(bulletKind) {
		case lightBall_S:
		case lightBall_ROUND:
			WeaponInfo.name = "lightBall_S";
			WeaponInfo.coolTime = 5;
			WeaponInfo.magazineSize = 6;
			WeaponInfo.reloadTime = 100;
			return new Weapon();
		case HEAL_SHOTGUN:
			WeaponInfo.name = "HEAL_SHOTGUN";
			WeaponInfo.coolTime = 5;
			WeaponInfo.magazineSize = 3;
			WeaponInfo.reloadTime = 20;
			return new Weapon();
		case BLACK_SLASH_BURST:
			WeaponInfo.name = "BLACK_SLASH_BURST";
			WeaponInfo.coolTime = 5;
			WeaponInfo.magazineSize = 1;
			WeaponInfo.reloadTime = 40;
			return new Weapon();
		default:
			return null;
		}
	}
	public static void inputBulletInfo(Unit user,int bulletKind,int bulletIID,Unit targetChara) {
		BulletBlueprint.clear(BulletBlueprint.DEFAULT_SCRIPT,user.dynam);
		BulletBlueprint.dynam.setAngle(targetChara);
		BulletBlueprint.team = user.getTeam();
		switch(bulletKind) {
		case lightBall_S:
			BulletBlueprint.name = "lightBall_S";
			BulletBlueprint.dynam.fastParaAdd_DSpd(10,3);
			BulletBlueprint.atk = 20;
			BulletBlueprint.imageID = bulletIID;
			GHQ.createBullet(user);
			break;
		case lightBall_ROUND:
			BulletBlueprint.name = "lightBall_ROUND";
			BulletBlueprint.atk = 20;
			BulletBlueprint.imageID = bulletIID;
			GHQ.createBullet(user).split_Burst(5, 10, 12.0);
			break;
		case HEAL_SHOTGUN:
			BulletBlueprint.name = "HEAL_SHOTGUN";
			BulletBlueprint.atk = -20;
			BulletBlueprint.limitRange = 150;
			BulletBlueprint.imageID = bulletIID;
			GHQ.createBullet(user).split_Burst(10, 16, 15.0);
			break;
		case BLACK_SLASH_BURST:
			BulletBlueprint.name = "BLACK_SLASH_BURST";
			BulletBlueprint.size = 10;
			BulletBlueprint.atk = 20;
			BulletBlueprint.offSet = 3;
			BulletBlueprint.reflection = 1;
			BulletBlueprint.limitFrame = 200;
			BulletBlueprint.imageID = bulletIID;
			final double DEG10 = PI/18;
			GHQ.createBullet(user).split_NWay(10,new double[] {-DEG10, 0, +DEG10},20);
			break;
		}
	}
}
