package engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;

import action.Action;
import action.ActionInfo;
import action.ActionSource;
import bullet.Bullet;
import chara.*;
import core.GHQ;
import core.MessageSource;
import stage.ControlExpansion;
import stage.Stage;
import stage.StageEngine;
import stage.StageInfo;
import structure.Structure;

public class Engine_BS extends StageEngine implements MessageSource,ActionSource{
	private static final UserChara[] friendCharaClass = new UserChara[2];
	private static final ArrayList<Chara> enemyCharaClass = new ArrayList<Chara>();
	private static final Stage[] stages = new Stage[1];
	private int nowStage;
	
	public static final int FRIEND = 0,ENEMY = 100;
	final int F_MOVE_SPD = 6;
	
	int formationsX[],formationsY[];
	int formationCenterX,formationCenterY;
	
	//images
	//stageObject
	private int vegImageIID[] = new int[5];
	
	private static final CtrlEx_BS ctrlEx = new CtrlEx_BS();
	
	int focusIID,magicCircleIID;
	
	//editMode
	static boolean editMode;
	
	//initialization
	@Override
	public String getTitleName() {
		return "BreakScope";
	}
	public static void main(String args[]){
		new GHQ(new Engine_BS());
	}
	@Override
	public final ControlExpansion getCtrl_ex() {
		return ctrlEx;
	}
	@Override
	public final void loadResource() {
		focusIID = GHQ.loadImage("focus.png");
		magicCircleIID = GHQ.loadImage("MagicCircle.png");
		vegImageIID[0] = GHQ.loadImage("veg_leaf.png");
		vegImageIID[1] = GHQ.loadImage("veg_flower.png");
		vegImageIID[2] = GHQ.loadImage("veg_leaf2.png");
		vegImageIID[3] = GHQ.loadImage("veg_stone.png");
		vegImageIID[4] = GHQ.loadImage("veg_leaf3.png");
		Editor.freeShapeIID = GHQ.loadImage("gui_editor/FreeShape.png");
	}
	@Override
	public final Chara[] charaSetup() {
		//formation
		formationCenterX = GHQ.getScreenW()/2;formationCenterY = GHQ.getScreenH() - 100;
		formationsX = new int[2];
		formationsY = new int[2];
		formationsX[0] = -15;formationsY[0] = 0;
		formationsX[1] = +15;formationsY[1] = 0;
		//friend
		friendCharaClass[0] = (UserChara)new Marisa().initialSpawn(FRIEND,formationCenterX + formationsX[0],formationCenterY + formationsY[0],4000);
		friendCharaClass[1] = (UserChara)new Reimu().initialSpawn(FRIEND,formationCenterX + formationsX[1],formationCenterY + formationsY[1],4000);
		//action
		ActionInfo.clear();
		ActionInfo.addDstPlan(1000, GHQ.getScreenW() - 200, GHQ.getScreenH() + 100);
		ActionInfo.addDstPlan(1000, GHQ.getScreenW() + 200, GHQ.getScreenH() + 100);
		final Action moveLeftToRight200 = new Action(this);
		//enemy
		enemyCharaClass.add(new Fairy().initialSpawn(ENEMY, 300, 100,2500));
		enemyCharaClass.add(new Fairy().initialSpawn(ENEMY, 700, 20,2500));
		enemyCharaClass.add(new Fairy().initialSpawn(ENEMY, 1200, 300,2500));
		enemyCharaClass.add(new Fairy().initialSpawn(ENEMY, 1800, 700,2500));
		enemyCharaClass.add(new WhiteMan().initialSpawn(ENEMY, 400, GHQ.random2(100, 150),50000));
		enemyCharaClass.add(new BlackMan().initialSpawn(ENEMY, 200, GHQ.random2(100, 150),10000));
		
		//result
		final ArrayList<Chara> allCharaClass = new ArrayList<Chara>();
		allCharaClass.add(friendCharaClass[0]);
		allCharaClass.add(friendCharaClass[1]);
		allCharaClass.addAll(enemyCharaClass);
		return allCharaClass.toArray(new Chara[0]);
	}
	@Override
	public final Stage stageSetup() {
		StageInfo.clear();
		StageInfo.name = "stage1_1";
		StageInfo.stageW = StageInfo.stageH = 2000;
		return stages[0] = new Stage();
	}
	@Override
	public final void openStage() {
		GHQ.addMessage(this,"This is a prototype stage.");
	}
	//idle
	private int gameFrame;
	@Override
	public final void idle(Graphics2D g2,int stopEventKind) {
		gameFrame++;
		//stagePaint
		//background
		g2.setColor(new Color(112,173,71));
		g2.fillRect(0,0,stages[nowStage].getStageW(),stages[nowStage].getStageH());
		//landscape
		g2.setColor(Color.LIGHT_GRAY);
		for(Structure ver : stages[nowStage].getPrimeStructures())
			ver.doFill(g2);
		for(Structure ver : stages[nowStage].getSubStructures())
			ver.doFill(g2);
		g2.setColor(Color.GRAY);
		g2.setStroke(GHQ.stroke3);
		for(Structure ver : stages[nowStage].getPrimeStructures())
			ver.doDraw(g2);
		for(Structure ver : stages[nowStage].getSubStructures())
			ver.doDraw(g2);
		//vegitation
		GHQ.drawImageTHH_center(vegImageIID[3], 1172, 886,1.3);
		GHQ.drawImageTHH_center(vegImageIID[0], 1200, 800,1.0);
		GHQ.drawImageTHH_center(vegImageIID[0], 1800, 350,1.4);
		GHQ.drawImageTHH_center(vegImageIID[0], 1160, 870,1.7);
		GHQ.drawImageTHH_center(vegImageIID[1], 1180, 830,1.3);
		GHQ.drawImageTHH_center(vegImageIID[2], 1102, 815,1.3);
		GHQ.drawImageTHH_center(vegImageIID[2], 1122, 826,1.3);
		GHQ.drawImageTHH_center(vegImageIID[4], 822, 886,1.3);
		////////////////
		GHQ.drawImageTHH_center(magicCircleIID, formationCenterX, formationCenterY, (double)GHQ.getNowFrame()/35.0);
		g2.setColor(Color.RED);
		g2.fillOval(formationCenterX - 2, formationCenterY - 2, 5, 5);
		////////////////
		final int MOUSE_X = GHQ.getMouseX(),MOUSE_Y = GHQ.getMouseY();
		if(stopEventKind == NONE) {
			//gravity
			if(doGravity) {
				//<editting>
			}
			//others
			switch(nowStage) {
			case 0:
				for(int i = 0;i < friendCharaClass.length;i++)
					friendCharaClass[i].teleportTo(formationCenterX + formationsX[i], formationCenterY + formationsY[i]);
				//friend
				GHQ.defaultCharaIdle(friendCharaClass);
				//enemy
				for(int i = 0;i < enemyCharaClass.size();i++) {
					final Chara enemy = enemyCharaClass.get(i);
					if(enemy.getHP() <= 0) {
						enemyCharaClass.remove(enemy);
						continue;
					}
					GHQ.defaultCharaIdle(enemy);
					if(enemy.getName() == "FairyA") {
						final int FRAME = gameFrame % 240;
						if(FRAME < 100)
							enemyCharaClass.get(i).dynam.setSpeed(-5, 0);
						else if(FRAME < 120)
							enemyCharaClass.get(i).dynam.setSpeed(0, 0);
						else if(FRAME < 220)
							enemyCharaClass.get(i).dynam.setSpeed(5, 0);
						else
							enemyCharaClass.get(i).dynam.setSpeed(0, 0);
					}
				}
				//leap
				if(ctrlEx.getCommandBool(CtrlEx_BS.LEAP)){
					//final int DX = MOUSE_X - formationCenterX,DY = MOUSE_Y - formationCenterY;
					//if(DX*DX + DY*DY < 5000) {
						formationCenterX = MOUSE_X;formationCenterY = MOUSE_Y;
					//}else {
						//formationCenterX += (double)DX/10.0;formationCenterY += (double)DY/10.0;
					//}
					for(int i = 0;i < friendCharaClass.length;i++)
						friendCharaClass[i].teleportTo(formationCenterX + formationsX[i], formationCenterY + formationsY[i]);
				}
				//shot
				for(Chara chara : friendCharaClass)
					chara.attackOrder = ctrlEx.getCommandBool(CtrlEx_BS.SHOT);
				//spell
				{
					int spellUser;
					while((spellUser = ctrlEx.pullSpellUser()) != NONE) {
						if(spellUser < friendCharaClass.length)
							friendCharaClass[spellUser].spellOrder = true;
					}
				}
				break;
			}
		}else if(stopEventKind == GHQ.STOP || stopEventKind == GHQ.NO_ANM_STOP) {
			GHQ.defaultCharaIdle(friendCharaClass);
			GHQ.defaultCharaIdle(enemyCharaClass);
		}
		GHQ.defaultEntityIdle();
		//focus
		g2.setColor(new Color(200,120,10,100));
		g2.setStroke(GHQ.stroke3);
		g2.drawLine(formationCenterX,formationCenterY,MOUSE_X,MOUSE_Y);
		GHQ.drawImageTHH_center(focusIID,MOUSE_X,MOUSE_Y);
		//editor
		if(editMode) {
			Editor.doEditorPaint(g2);
		}else { //game GUI
			GHQ.translateForGUI(true);
			int pos = 1;
			for(UserChara chara : friendCharaClass) 
				GHQ.drawImageTHH(chara.faceIID, pos++*90 + 10, GHQ.getScreenH() - 40, 80, 30);
			GHQ.translateForGUI(false);
		}
		if(stopEventKind == NONE) { //scroll
			//scroll by keys
			if(ctrlEx.getCommandBool(CtrlEx_BS.UP)) {
				formationCenterY -= F_MOVE_SPD;
				GHQ.viewTargetMove(0,-F_MOVE_SPD);
				GHQ.pureViewMove(0,-F_MOVE_SPD);
			}else if(ctrlEx.getCommandBool(CtrlEx_BS.DOWN)) {
				formationCenterY += F_MOVE_SPD;
				GHQ.viewTargetMove(0,F_MOVE_SPD);
				GHQ.pureViewMove(0,F_MOVE_SPD);
			}
			if(ctrlEx.getCommandBool(CtrlEx_BS.LEFT)) {
				formationCenterX -= F_MOVE_SPD;
				GHQ.viewTargetMove(-F_MOVE_SPD,0);
				GHQ.pureViewMove(-F_MOVE_SPD,0);
			}else if(ctrlEx.getCommandBool(CtrlEx_BS.RIGHT)) {
				formationCenterX += F_MOVE_SPD;
				GHQ.viewTargetMove(F_MOVE_SPD,0);
				GHQ.pureViewMove(F_MOVE_SPD,0);
			}
			//scroll by mouse
			if(doScrollView) {
				GHQ.viewTargetTo((MOUSE_X + formationCenterX)/2,(MOUSE_Y + formationCenterY)/2);
				GHQ.viewApproach_rate(10);
			}
		}
	}
	
	//control
	@Override
	public final void resetStage() {
		
	}
	@Override
	public final Chara[] callBulletEngage(Chara[] characters,Bullet bullet) {
		final Chara[] result = new Chara[characters.length];
		int searched = 0;
		for(int i = 0;i < characters.length;i++) {
			final Chara chara = characters[i];
			if(chara.bulletEngage(bullet))
				result[searched++] = chara;
		}
		return Arrays.copyOf(result, searched);
	}
	@Override
	public boolean deleteChara(Chara chara) {
		return enemyCharaClass.remove(chara);
	}
	//information
	@Override
	public final int getGameFrame() {
		return gameFrame;
	}
	final static Chara[] getUserChara() {
		return friendCharaClass;
	}
	private boolean doScrollView = true;
	private boolean doGravity = false;
	
	private static final class Editor{
		//gui
		private static int freeShapeIID;
		//role
		static void doEditorPaint(Graphics2D g2) {
			GHQ.translateForGUI(true);
			g2.setColor(Color.WHITE);
			g2.drawString("EDIT_MODE", 20, 20);
			GHQ.drawImageTHH_center(freeShapeIID,80,200);
			GHQ.translateForGUI(false);
		}
	}
}
