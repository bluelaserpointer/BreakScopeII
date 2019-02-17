package chara;

import static java.lang.Math.PI;
import static java.lang.Math.random;

import bullet.*;
import core.DynamInteractable;
import core.GHQ;
import effect.*;
import unit.THHUnit;
import weapon.Weapon;
import weapon.WeaponInfo;

public class Player extends THHUnit{
	private static final long serialVersionUID = 8121281285749873895L;
	{
		charaSize = 20;
	}
	@Override
	public final String getName() {
		return "Player";
	}
	
	//weapon&bullet kind name
	static final int
		MILLKY_WAY = 0,NARROW_SPARK = 1,REUSE_BOMB = 2,MAGIC_MISSILE = 3;
	//effect kind name
	private static final int LIGHTNING = 0,SPARK_HIT_EF = 1,MISSILE_TRACE1_EF = 2,MISSILE_TRACE2_EF = 3,
			MISSILE_HIT_EF = 4;
	
	//GUI
		
	//Sounds
	
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaIID = GHQ.loadImage("Marisa.png");
		faceIID = GHQ.loadImage("MarisaIcon.png");
		bulletIID[MILLKY_WAY] = GHQ.loadImage("MillkyWay.png");
		bulletIID[NARROW_SPARK] = GHQ.loadImage("NarrowSpark_2.png");
		bulletIID[REUSE_BOMB] = GHQ.loadImage("ReuseBomb.png");
		bulletIID[MAGIC_MISSILE] = GHQ.loadImage("MagicMissile.png");
		effectIID[LIGHTNING] = GHQ.loadImage("ReuseBomb_Effect.png");
		effectIID[SPARK_HIT_EF] = GHQ.loadImage("NarrowSpark_HitEffect.png");
		effectIID[MISSILE_TRACE1_EF] = GHQ.loadImage("StarEffect2.png");
		effectIID[MISSILE_TRACE2_EF] = GHQ.loadImage("MagicMissile.png");
		effectIID[MISSILE_HIT_EF] = GHQ.loadImage("MissileHitEffect.png");
	}
	
	@Override
	public final void loadSoundData(){
	}
	
	//Initialization
	@Override
	public final void battleStarted(){
		//test area
		//weaponSlot[0] = REUSE_BOMB;
		weaponSlot[0] = MAGIC_MISSILE;
		spellSlot[0] = NARROW_SPARK;
		spellSlot[1] = REUSE_BOMB;
		////weaponLoad
		//MILLKY_WAY
		WeaponInfo.clear();
		WeaponInfo.name = "MILLKY_WAY";
		WeaponInfo.coolTime = 10;
		weaponController[MILLKY_WAY] = new Weapon();
		//NARROW_SPARK
		WeaponInfo.clear();
		WeaponInfo.name = "NARROW_SPARK";
		WeaponInfo.reloadTime = 150;
		WeaponInfo.magazineSize = 1;
		weaponController[NARROW_SPARK] = new Weapon();
		//REUSE_BOMB
		WeaponInfo.clear();
		WeaponInfo.name = "REUSE_BOMB";
		WeaponInfo.coolTime = 10;
		weaponController[REUSE_BOMB] = new Weapon();
		//MAGIC_MISSILE
		WeaponInfo.clear();
		WeaponInfo.name = "MAGIC_MISSILE";
		WeaponInfo.coolTime = 25;
		weaponController[MAGIC_MISSILE] = new Weapon();
		/////////////////////
		slot_spell = 0;
	}
	@Override
	public final void respawn(int charaID,int charaTeam,int x,int y){
		super.respawn(charaID,charaTeam,x,y);
		status.setDefault(HP, 10000);
		status.setDefault(MP, 10000);
		for(Weapon ver : weaponController) {
			if(ver != null)
				ver.reset();
		}
	}	
	@Override
	public void activeCons() {
		super.activeCons();
		for(Weapon ver : weaponController) {
			if(ver != null)
				ver.defaultIdle();
		}
	}
	//bullet
	@Override
	public final boolean useWeapon(int kind) {
		return weaponController[kind].trigger();
	}
	@Override
	public final void setBullet(int kind,DynamInteractable user) {
		BulletBlueprint.clear(bulletScripts[kind],user.getDynam());
		BulletBlueprint.team = status.get(TEAM);
		switch(kind){
		case MILLKY_WAY:
			BulletBlueprint.name = "MILLKY_WAY";
			BulletBlueprint.dynam.setSpeed(20);
			BulletBlueprint.size = 30;
			BulletBlueprint.atk = 40;
			BulletBlueprint.offSet = 20;
			BulletBlueprint.limitFrame = 200;
			BulletBlueprint.imageID = bulletIID[MILLKY_WAY];
			GHQ.createBullet(user).split_Round(50, 8);
			break;
		case NARROW_SPARK:
			//message
			//if(this == source) {
			//	THH.addMessage(this,charaID,"KOIFU [MasterSpark]");
			//	THH.addMessage(this,charaID,"TEST MESSAGE 2");
			//}
			BulletBlueprint.name = "NARROW_SPARK";
			BulletBlueprint.dynam.setSpeed(GHQ.getImageByID(bulletIID[NARROW_SPARK]).getWidth(null));
			BulletBlueprint.size = 15;
			BulletBlueprint.atk = 8;
			BulletBlueprint.offSet = 20;
			BulletBlueprint.penetration = MAX;
			BulletBlueprint.reflection = 3;
			BulletBlueprint.limitFrame = 40;
			BulletBlueprint.imageID = bulletIID[NARROW_SPARK];
			BulletBlueprint.isLaser = true;
			GHQ.createBullet(user);
			break;
		case REUSE_BOMB:
			BulletBlueprint.name = "REUSE_BOMB";
			BulletBlueprint.dynam.setSpeed(40);
			BulletBlueprint.accel = 0.98;
			BulletBlueprint.size = 30;
			BulletBlueprint.atk = 40;
			BulletBlueprint.offSet = 20;
			BulletBlueprint.limitFrame = 200;
			BulletBlueprint.imageID = bulletIID[REUSE_BOMB];
			GHQ.createBullet(user).split_Round(50, 3);
			break;
		case MAGIC_MISSILE:
			BulletBlueprint.name = "MAGIC_MISSILE";
			BulletBlueprint.accel = 1.07;
			BulletBlueprint.size = 20;
			BulletBlueprint.atk = 500;
			BulletBlueprint.offSet = 100;
			BulletBlueprint.limitFrame = 2000;
			BulletBlueprint.imageID = bulletIID[MAGIC_MISSILE];
			BulletBlueprint.dynam.fastParaAdd_DASpd(10,GHQ.random2(PI/36),10.0);
			GHQ.createBullet(user);
			break;
		}
	}
	@Override
	public final void setEffect(int kind,DynamInteractable user) {
		EffectBlueprint.clear(effectScripts[kind],user.getDynam());
		switch(kind){
		case LIGHTNING:
			EffectBlueprint.name = "LIGHTNING";
			EffectBlueprint.dynam.fastParaAdd_DASpd(10,2*PI*random(),20);
			EffectBlueprint.limitFrame = 2;
			EffectBlueprint.imageID = effectIID[LIGHTNING];
			GHQ.createEffect(this);
			break;
		case SPARK_HIT_EF:
			EffectBlueprint.name = "SPARK_HIT_EF";
			EffectBlueprint.limitFrame = 3;
			EffectBlueprint.imageID = effectIID[SPARK_HIT_EF];
			EffectBlueprint.dynam.fastParaAdd_DASpd(10,2*PI*random(),GHQ.random2(0,12));
			GHQ.createEffect(this);
			break;
		case MISSILE_TRACE1_EF:
			EffectBlueprint.name = "MISSILE_TRACE1_EF";
			EffectBlueprint.accel = -0.2;
			EffectBlueprint.limitFrame = 7;
			EffectBlueprint.imageID = effectIID[kind];
			for(int i = 0;i < 4;i++) {
				EffectBlueprint.dynam.fastParaAdd_DASpd(10,2*PI*random(),GHQ.random2(0,12));
				GHQ.createEffect(this);
			}
			break;
		case MISSILE_TRACE2_EF:
			EffectBlueprint.name = "MISSILE_TRACE2_EF";
			EffectBlueprint.limitFrame = 7;
			EffectBlueprint.dynam.stop();
			EffectBlueprint.imageID = effectIID[kind];
			GHQ.createEffect(this);
			break;
		case MISSILE_HIT_EF:
			EffectBlueprint.name = "MISSILE_HIT_EF";
			EffectBlueprint.accel = -0.1;
			EffectBlueprint.imageID = effectIID[kind];
			for(int i = 0;i < 30;i++) {
				EffectBlueprint.limitFrame = GHQ.random2(10,40);
				GHQ.createEffect(this).dynam.fastParaAdd_DASpd(30,2*PI*random(),GHQ.random2(2,5));
			}
			break;
		}
	}
	private final BulletScript[] bulletScripts = new BulletScript[10];
	{
		bulletScripts[MILLKY_WAY] = BulletBlueprint.DEFAULT_SCRIPT;
		bulletScripts[NARROW_SPARK] = new BulletScript() {
			private static final long serialVersionUID = 7900689138570112752L;
			@Override
			public final void bulletIdle(Bullet bullet) {
				bullet.lifeSpanCheck();
				int count = 0;
				while(bullet.dynam.inStage()) {
					bullet.dynam(++count % 5 == 0);
					bullet.defaultPaint();
				}
				final DynamInteractable BULLET_SOURCE = bullet.source;
				bullet.dynam.setXY(BULLET_SOURCE);
				bullet.dynam.setAngle(BULLET_SOURCE.getDynam().getAngle());
			}
			@Override
			public final void bulletHitObject(Bullet bullet) {
				setEffect(SPARK_HIT_EF,bullet);
			}
		};
		bulletScripts[REUSE_BOMB] = new BulletScript() {
			private static final long serialVersionUID = -5081776847926794662L;

			@Override
			public final void bulletIdle(Bullet bullet) {
				bullet.defaultIdle();
				bullet.dynam.addSpeed(0.0,1.1);
				bullet.defaultPaint();
				if(random() < 0.2)
					setEffect(LIGHTNING,(DynamInteractable)bullet);
			}
		};
		bulletScripts[MAGIC_MISSILE] = new BulletScript() {
			private static final long serialVersionUID = -7592256488781770358L;
			@Override
			public final void bulletIdle(Bullet bullet) {
				for(int i = 0;i < 2;i++)
					setEffect(MISSILE_TRACE1_EF,bullet);
				setEffect(MISSILE_TRACE2_EF,bullet);
				super.bulletIdle(bullet);
			}
			@Override
			public final void bulletHitObject(Bullet bullet) {
				setEffect(MISSILE_HIT_EF,bullet);
			}
		};
	}
	private final EffectScript[] effectScripts = new EffectScript[10];
	{
		effectScripts[MISSILE_TRACE1_EF] = new EffectScript() {
			private static final long serialVersionUID = 2100372635933991953L;

			@Override
			public final void effectNoAnmPaint(Effect effect) {
				effectFadePaint(effect);
			}
		};
		effectScripts[MISSILE_TRACE2_EF] = new EffectScript() {
			private static final long serialVersionUID = -5749980868018351153L;

			@Override
			public final void effectNoAnmPaint(Effect effect) {
				GHQ.setImageAlpha((float)(1.0 - (double)GHQ.getPassedFrame(effect.INITIAL_FRAME)/effect.LIMIT_FRAME));
				super.effectNoAnmPaint(effect);
				GHQ.setImageAlpha();
			}
		};
		effectScripts[MISSILE_HIT_EF] = new EffectScript() {
			private static final long serialVersionUID = -6709638231571272900L;

			@Override
			public final void effectNoAnmPaint(Effect effect) {
				GHQ.setImageAlpha((float)(1.0 - (double)GHQ.getPassedFrame(effect.INITIAL_FRAME)/effect.LIMIT_FRAME));
				super.effectNoAnmPaint(effect);
				GHQ.setImageAlpha();
			}
		};
	}
}
