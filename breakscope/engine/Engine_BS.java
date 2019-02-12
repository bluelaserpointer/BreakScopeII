package engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;

import action.Action;
import action.ActionInfo;
import action.ActionSource;
import bullet.Bullet;
import chara.BlackMan;
import chara.Fairy;
import chara.Player;
import chara.WhiteMan;
import core.GHQ;
import core.MessageSource;
import stage.ControlExpansion;
import stage.StageEngine;
import structure.Structure;
import structure.Terrain;
import unit.*;

public class Engine_BS extends StageEngine implements MessageSource,ActionSource{
	private static final Player player = new Player();
	private static final Stage_BS[] stages = new Stage_BS[1];
	private int nowStage;
	
	public static final int FRIEND = 0,ENEMY = 100;
	final int F_MOVE_SPD = 6;
	
	int formationCenterX,formationCenterY;
	
	private int stageW,stageH;
	
	public String getVersion() {
		return "alpha1.0.0";
	}
	
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
	public final void charaSetup() {
		//formation
		formationCenterX = GHQ.getScreenW()/2;formationCenterY = GHQ.getScreenH() - 100;
		//friend
		player.initialSpawn(FRIEND,formationCenterX,formationCenterY,4000);
		GHQ.addUnit(player);
		//action
		ActionInfo.clear();
		ActionInfo.addDstPlan(1000, GHQ.getScreenW() - 200, GHQ.getScreenH() + 100);
		ActionInfo.addDstPlan(1000, GHQ.getScreenW() + 200, GHQ.getScreenH() + 100);
		final Action moveLeftToRight200 = new Action(this);
		//enemy
		GHQ.addUnit(new Fairy().initialSpawn(ENEMY, 300, 100,2500));
		GHQ.addUnit(new Fairy().initialSpawn(ENEMY, 700, 20,2500));
		GHQ.addUnit(new Fairy().initialSpawn(ENEMY, 1200, 300,2500));
		GHQ.addUnit(new Fairy().initialSpawn(ENEMY, 1800, 700,2500));
		GHQ.addUnit(new WhiteMan().initialSpawn(ENEMY, 400, GHQ.random2(100, 150),50000));
		GHQ.addUnit(new BlackMan().initialSpawn(ENEMY, 200, GHQ.random2(100, 150),10000));
	}
	@Override
	public final void stageSetup() {
		stageW = stageH = 5000;
		GHQ.addStructure(new Terrain(new int[]{0,0,300,400,500,600,700,700},new int[]{650,450,450,350,350,450,450,650}));
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
		final Stage_BS STAGE = stages[nowStage];
		//stagePaint
		//background
		g2.setColor(new Color(112,173,71));
		g2.fillRect(0,0,stageW,stageH);
		//landscape
		g2.setColor(Color.LIGHT_GRAY);
		for(Structure structure : GHQ.getStructureList())
			structure.doDraw(g2);
		g2.setColor(Color.GRAY);
		g2.setStroke(GHQ.stroke3);
		for(Structure structure : GHQ.getStructureList())
			structure.doDraw(g2);
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
			//others
			switch(nowStage) {
			case 0:
				//friend
				player.teleportTo(formationCenterX, formationCenterY);
				GHQ.defaultCharaIdle(player);
				//enemy
				for(Unit enemy : GHQ.getCharacterList()) {
					if(!enemy.isAlive())
						continue;
					GHQ.defaultCharaIdle(enemy);
					if(enemy.getName() == "FairyA") {
						final int FRAME = gameFrame % 240;
						if(FRAME < 100)
							enemy.dynam.setSpeed(-5, 0);
						else if(FRAME < 120)
							enemy.dynam.setSpeed(0, 0);
						else if(FRAME < 220)
							enemy.dynam.setSpeed(5, 0);
						else
							enemy.dynam.setSpeed(0, 0);
					}
				}
				//leap
				if(ctrlEx.getCommandBool(CtrlEx_BS.LEAP)){
					formationCenterX = MOUSE_X;formationCenterY = MOUSE_Y;
					player.teleportTo(formationCenterX, formationCenterY);
				}
				//shot
				player.attackOrder = ctrlEx.getCommandBool(CtrlEx_BS.SHOT);
				//spell
				int spellUser;
				while((spellUser = ctrlEx.pullSpellUser()) != NONE)
					player.spellOrder = true;
				break;
			}
		}else if(stopEventKind == GHQ.STOP || stopEventKind == GHQ.NO_ANM_STOP)
			GHQ.defaultCharaIdle(GHQ.getCharacterList());
		GHQ.defaultEntityIdle();
		//focus
		g2.setColor(new Color(200,120,10,100));
		g2.setStroke(GHQ.stroke3);
		g2.drawLine(formationCenterX,formationCenterY,MOUSE_X,MOUSE_Y);
		GHQ.drawImageTHH_center(focusIID,MOUSE_X,MOUSE_Y);
		//editor
		if(editMode) {
			Editor.idle(g2);
		}else { //game GUI
			GHQ.translateForGUI(true);
			int pos = 1;
			GHQ.drawImageTHH(player.faceIID, pos++*90 + 10, GHQ.getScreenH() - 40, 80, 30);
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
	public final Unit[] callBulletEngage(Unit[] characters,Bullet bullet) {
		final Unit[] result = new Unit[characters.length];
		int searched = 0;
		for(int i = 0;i < characters.length;i++) {
			final Unit chara = characters[i];
			if(chara.bulletEngage(bullet))
				result[searched++] = chara;
		}
		return Arrays.copyOf(result, searched);
	}
	//information
	@Override
	public final int getGameFrame() {
		return gameFrame;
	}
	@Override
	public final boolean inStage(int x,int y) {
		return 0 < x && x <= stageW && 0 < y && y <= stageH;
	}
	@Override
	public final int getStageW() {
		return stageW;
	}
	@Override
	public final int getStageH() {
		return stageH;
	}
	private boolean doScrollView = true;
	
	private static final class Editor{
		//gui
		private static int freeShapeIID;
		
		private static int placeX,placeY;
		
		private static byte placeKind;
		private static final byte 
			TERRAIN = 0,
			ENEMY = 1,
			ITEM = 2;
		//role
		static void idle(Graphics2D g2) {
			//mouse
			if(ctrlEx.getCommandBool(ctrlEx.LEAP)) {
				g2.setColor(Color.RED);
				final int N = 100;
				int S = 4;
				final int SX = (GHQ.getMouseX() + N/2)/N,SY = (GHQ.getMouseY() + N/2)/N;
				for(int xi = -1;xi <= +1;xi++) {
					for(int yi = -1;yi <= +1;yi++)
						g2.fillOval((SX + xi)*N - S/2, (SY + yi)*N - S/2, S, S);
				}
				S += 6;
				g2.drawOval(SX*N - S/2, SY*N - S/2, S, S);
				placeX = SX;
				placeY = SY;
			}else {
				placeX = GHQ.getMouseX();
				placeY = GHQ.getMouseY();
			}
			//blockPointSet
			if(ctrlEx.getCommandBool(ctrlEx.SHOT)) {
				switch(placeKind) {
				case TERRAIN:
					Terrain.blueprintAddPoint(placeX,placeY);
					break;
				case ENEMY:
					break;
				case ITEM:
					break;
				}
			}
			//gui
			GHQ.translateForGUI(true);
			g2.setColor(Color.WHITE);
			g2.drawString("EDIT_MODE", 20, 20);
			GHQ.drawImageTHH_center(freeShapeIID,80,200);
			GHQ.translateForGUI(false);
		}
	}
}
