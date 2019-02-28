package engine;

import static java.awt.event.KeyEvent.*;
import static unit.THHUnit.HP;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.util.Arrays;

import action.ActionInfo;
import action.ActionSource;
import bullet.Bullet;
import core.GHQ;
import core.MessageSource;
import gui.DefaultStageEditor;
import input.MouseListenerEx;
import input.SingleKeyListener;
import input.SingleNumKeyListener;
import stage.StageEngine;
import stage.StageSaveData;
import structure.Structure;
import unit.*;

public class Engine_BS extends StageEngine implements MessageSource,ActionSource{
	public static final int FRIEND = 0,ENEMY = 100;
	
	private static final Player player = new Player(FRIEND);
	private static final Stage_BS[] stages = new Stage_BS[1];
	private int nowStage;
	
	final int F_MOVE_SPD = 6;
	
	int formationCenterX,formationCenterY;
	
	private int stageW,stageH;
	
	public String getVersion() {
		return "alpha1.0.0";
	}
	
	//inputEvnet
	private final int inputKeys[] = 
	{
		VK_W,
		VK_A,
		VK_S,
		VK_D,
		VK_Q,
		VK_E,
		VK_R,
		VK_F,
		VK_G,
		VK_TAB,
		VK_SHIFT,
		VK_SPACE,
		VK_ESCAPE,
		VK_F6,
	};
	private final MouseListenerEx sml = new MouseListenerEx();
	private final SingleKeyListener skl = new SingleKeyListener(inputKeys);
	private final SingleNumKeyListener snkl = new SingleNumKeyListener();
	
	//images
	//stageObject
	private int vegImageIID[] = new int[5];
	
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
	public final void loadResource() {
		focusIID = GHQ.loadImage("focus.png");
		magicCircleIID = GHQ.loadImage("MagicCircle.png");
		GHQ.loadImageFolder(new File("picture"));
		vegImageIID[0] = GHQ.loadImage("veg_leaf.png");
		vegImageIID[1] = GHQ.loadImage("veg_flower.png");
		vegImageIID[2] = GHQ.loadImage("veg_leaf2.png");
		vegImageIID[3] = GHQ.loadImage("veg_stone.png");
		vegImageIID[4] = GHQ.loadImage("veg_leaf3.png");
		DefaultStageEditor.init(new File("stage/saveData1.txt"));
		GHQ.addListenerEx(sml);
		GHQ.addListenerEx(skl);
		GHQ.addListenerEx(snkl);
	}
	@Override
	public final void charaSetup() {
		//formation
		formationCenterX = GHQ.getScreenW()/2;formationCenterY = GHQ.getScreenH() - 100;
		//friend
		player.initialSpawn(formationCenterX,formationCenterY).status.setDefault(HP, 4000);
		GHQ.addUnit(player);
		//action
		ActionInfo.clear();
		ActionInfo.addDstPlan(1000, GHQ.getScreenW() - 200, GHQ.getScreenH() + 100);
		ActionInfo.addDstPlan(1000, GHQ.getScreenW() + 200, GHQ.getScreenH() + 100);
		//final Action moveLeftToRight200 = new Action(this);
		//enemy
		GHQ.addUnit(new Fairy(ENEMY).initialSpawn(300, 100)).status.setDefault(HP, 2500);
		GHQ.addUnit(new Fairy(ENEMY).initialSpawn(700, 20)).status.setDefault(HP, 2500);
		GHQ.addUnit(new Fairy(ENEMY).initialSpawn(1200, 300)).status.setDefault(HP, 2500);
		GHQ.addUnit(new Fairy(ENEMY).initialSpawn(1800, 700)).status.setDefault(HP, 2500);
		GHQ.addUnit(new WhiteMan(ENEMY).initialSpawn(400, GHQ.random2(100, 150))).status.setDefault(HP, 50000);
		GHQ.addUnit(new BlackMan(ENEMY).initialSpawn(200, GHQ.random2(100, 150))).status.setDefault(HP, 10000);
	}
	@Override
	public final void stageSetup() {
		stageW = stageH = 5000;
		stages[0] = (Stage_BS)GHQ.loadData(new File("stage/saveData1.txt"));
		if(stages[0] != null) {
			for(Structure structure : stages[0].STRUCTURES) {
				GHQ.addStructure(structure);
			}
		}
	}
	@Override
	public final void openStage() {
		GHQ.addMessage(this,"This is a prototype stage.");
		skl.enable();
		snkl.enable();
		sml.enable();
	}
	@Override
	public final StageSaveData getStageSaveData() {
		return new Stage_BS(GHQ.getCharacters(),GHQ.getStructures());
	}
	//idle
	private int gameFrame;
	@Override
	public final void idle(Graphics2D g2,int stopEventKind) {
		gameFrame++;
		//stagePaint
		//background
		g2.setColor(new Color(112,173,71));
		g2.fillRect(0,0,stageW,stageH);
		//landscape
		for(Structure ver : GHQ.getStructureList())
			ver.paint();
		//vegitation
		GHQ.drawImageGHQ_center(vegImageIID[3], 1172, 886,1.3);
		GHQ.drawImageGHQ_center(vegImageIID[0], 1200, 800,1.0);
		GHQ.drawImageGHQ_center(vegImageIID[0], 1800, 350,1.4);
		GHQ.drawImageGHQ_center(vegImageIID[0], 1160, 870,1.7);
		GHQ.drawImageGHQ_center(vegImageIID[1], 1180, 830,1.3);
		GHQ.drawImageGHQ_center(vegImageIID[2], 1102, 815,1.3);
		GHQ.drawImageGHQ_center(vegImageIID[2], 1122, 826,1.3);
		GHQ.drawImageGHQ_center(vegImageIID[4], 822, 886,1.3);
		////////////////
		GHQ.drawImageGHQ_center(magicCircleIID, formationCenterX, formationCenterY, (double)GHQ.getNowFrame()/35.0);
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
				if(skl.hasEvent(VK_SHIFT)){
					formationCenterX = MOUSE_X;formationCenterY = MOUSE_Y;
					player.teleportTo(formationCenterX, formationCenterY);
				}
				//shot
				player.attackOrder = sml.hasButton1Event();
				//spell
				player.spellOrder = sml.pullButton2Event();
				break;
			}
		}else if(stopEventKind == GHQ.STOP || stopEventKind == GHQ.NO_ANM_STOP)
			GHQ.defaultCharaIdle(GHQ.getCharacterList());
		GHQ.defaultEntityIdle();
		//focus
		g2.setColor(new Color(200,120,10,100));
		g2.setStroke(GHQ.stroke3);
		g2.drawLine(formationCenterX,formationCenterY,MOUSE_X,MOUSE_Y);
		GHQ.drawImageGHQ_center(focusIID,MOUSE_X,MOUSE_Y);
		//editor
		if(skl.pullEvent(VK_F6)) {
			if(editMode) {
				editMode = false;
				GHQ.disableGUIs(DefaultStageEditor.EDIT_MENU_GROUP);
				GHQ.clearStopEvent();
			}else if(GHQ.isNoStopEvent()) {
				editMode = true;
				GHQ.enableGUIs(DefaultStageEditor.EDIT_MENU_GROUP);
				GHQ.stopScreen_noAnm();
			}
		}
		if(editMode) {
			DefaultStageEditor.idle(g2);
		}else { //game GUI
			GHQ.translateForGUI(true);
			int pos = 1;
			GHQ.drawImageGHQ(player.faceIID, pos++*90 + 10, GHQ.getScreenH() - 40, 80, 30);
			GHQ.translateForGUI(false);
		}
		if(stopEventKind == NONE || editMode) { //scroll
			//scroll by keys
			if(skl.hasEvent(VK_W)) {
				formationCenterY -= F_MOVE_SPD;
				GHQ.viewTargetMove(0,-F_MOVE_SPD);
				GHQ.pureViewMove(0,-F_MOVE_SPD);
			}else if(skl.hasEvent(VK_S)) {
				formationCenterY += F_MOVE_SPD;
				GHQ.viewTargetMove(0,F_MOVE_SPD);
				GHQ.pureViewMove(0,F_MOVE_SPD);
			}
			if(skl.hasEvent(VK_A)) {
				formationCenterX -= F_MOVE_SPD;
				GHQ.viewTargetMove(-F_MOVE_SPD,0);
				GHQ.pureViewMove(-F_MOVE_SPD,0);
			}else if(skl.hasEvent(VK_D)) {
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
}
