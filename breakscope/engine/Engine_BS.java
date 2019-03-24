package engine;

import static java.awt.event.KeyEvent.*;
import static thhunit.THHUnit.HP;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.util.Arrays;

import action.ActionInfo;
import action.ActionSource;
import bullet.Bullet;
import core.GHQ;
import gui.ItemStorageViewer;
import gui.DefaultStageEditor;
import gui.MessageSource;
import gui.MouseHook;
import input.MouseListenerEx;
import input.SingleKeyListener;
import input.SingleNumKeyListener;
import item.Ammo;
import item.Item;
import paint.ImageFrame;
import paint.RectPaint;
import stage.StageEngine;
import stage.StageSaveData;
import storage.TableStorage;
import structure.Structure;
import unit.BlackMan;
import unit.Fairy;
import unit.Player;
import unit.Unit;
import unit.WhiteMan;
import vegetation.Vegetation;

/**
 * The core class for game "BreakScope II"
 * @author bluelaserpointer
 * @version alpha1.0
 */

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
	private final MouseListenerEx s_mouseL = new MouseListenerEx();
	private final SingleKeyListener s_keyL = new SingleKeyListener(inputKeys);
	private final SingleNumKeyListener s_numKeyL = new SingleNumKeyListener();
	
	//images
	
	int focusIID,magicCircleIID;
	
	//GUIParts
	private static DefaultStageEditor editor;
	private static ItemStorageViewer itemContainer;
	private static MouseHook<Item> itemMouseHook;
	
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
		focusIID = GHQ.loadImage("thhimage/focus.png");
		magicCircleIID = GHQ.loadImage("thhimage/MagicCircle.png");
		//GUI
		GHQ.addGUIParts(itemContainer = new ItemStorageViewer("MENU_GROUP",RectPaint.BLANK_SCRIPT,new ImageFrame("picture/gui/slot.png"),50,70,70,(TableStorage<Item>)player.inventory.items){
			@Override
			public void clicked() {
				final int HOVERED_ID = getMouseHoveredID();
				if(itemMouseHook.hasObject()) {
					itemMouseHook.hook(storage.set(HOVERED_ID, itemMouseHook.get()));
				}else {
					itemMouseHook.hook(storage.remove(HOVERED_ID));
				}
			}
		});
		GHQ.addGUIParts(editor = new DefaultStageEditor("EDITER_GROUP", new File("stage/saveData1.txt")));
		GHQ.addGUIParts(itemMouseHook = new MouseHook<Item>("MOUSE_HOOK", null, 70) {
				@Override
				public void idle() {
					super.idle();
					if(hookingObject instanceof Item && hookingObject != Item.BLANK_ITEM) {
						final int AMOUNT = ((Item)hookingObject).getAmount();
						final Graphics2D G2 = GHQ.getGraphics2D();
						G2.setColor(Color.GRAY);
						G2.drawString(String.valueOf(AMOUNT), GHQ.getMouseScreenX() + SIZE/2 - 23, GHQ.getMouseScreenY() + SIZE/2 - 9);
						G2.setColor(Color.BLACK);
						G2.drawString(String.valueOf(AMOUNT), GHQ.getMouseScreenX() + SIZE/2 - 24, GHQ.getMouseScreenY() + SIZE/2 - 10);
					}
				}
		}).enable();
		Ammo.loadResource();
		GHQ.addListenerEx(s_mouseL);
		GHQ.addListenerEx(s_keyL);
		GHQ.addListenerEx(s_numKeyL);
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
		//vegetation
		GHQ.addVegetation(new Vegetation(new ImageFrame("thhimage/veg_leaf.png"),1172,886));
		GHQ.addVegetation(new Vegetation(new ImageFrame("thhimage/veg_flower.png"),1200,800));
		GHQ.addVegetation(new Vegetation(new ImageFrame("thhimage/veg_leaf2.png"),1800,350));
		GHQ.addVegetation(new Vegetation(new ImageFrame("thhimage/veg_stone.png"),1160,870));
		GHQ.addVegetation(new Vegetation(new ImageFrame("thhimage/veg_leaf3.png"),1102,830));
		GHQ.addVegetation(new Vegetation(new ImageFrame("thhimage/veg_leaf3.png"),1122,815));
		GHQ.addVegetation(new Vegetation(new ImageFrame("thhimage/veg_leaf3.png"),822,886));
		GHQ.addVegetation(new Ammo(Ammo.AMMO_9MM,10).drop(822,886));
		GHQ.addVegetation(new Ammo(Ammo.AMMO_45,10).drop(862,896));
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
		s_keyL.enable();
		s_numKeyL.enable();
		s_mouseL.enable();
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
		//vegetation
		for(Vegetation ver : GHQ.getVegetationList())
			ver.paint();
		//landscape
		for(Structure ver : GHQ.getStructureList())
			ver.paint();
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
				if(s_keyL.hasEvent(VK_SHIFT)){
					formationCenterX = MOUSE_X;formationCenterY = MOUSE_Y;
					player.teleportTo(formationCenterX, formationCenterY);
				}
				//shot
				player.attackOrder = s_mouseL.hasButton1Event();
				//spell
				player.spellOrder = s_mouseL.pullButton2Event();
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
		if(s_keyL.pullEvent(VK_F6)) {
			editor.flit();
			if(editor.isEnabled())
				itemContainer.disable();
		}
		if(!editor.isEnabled()){ //game GUI
			GHQ.translateForGUI(true);
			int pos = 1;
			player.iconPaint.rectPaint(pos++*90 + 10, GHQ.getScreenH() - 40, 80, 30);
			GHQ.translateForGUI(false);
			if(s_keyL.pullEvent(VK_ESCAPE)) {
				itemContainer.flit();
			}
		}
		if(stopEventKind == NONE || editor.isEnabled()) { //scroll
			//scroll by keys
			if(s_keyL.hasEvent(VK_W)) {
				formationCenterY -= F_MOVE_SPD;
				GHQ.viewTargetMove(0,-F_MOVE_SPD);
				GHQ.pureViewMove(0,-F_MOVE_SPD);
			}else if(s_keyL.hasEvent(VK_S)) {
				formationCenterY += F_MOVE_SPD;
				GHQ.viewTargetMove(0,F_MOVE_SPD);
				GHQ.pureViewMove(0,F_MOVE_SPD);
			}
			if(s_keyL.hasEvent(VK_A)) {
				formationCenterX -= F_MOVE_SPD;
				GHQ.viewTargetMove(-F_MOVE_SPD,0);
				GHQ.pureViewMove(-F_MOVE_SPD,0);
			}else if(s_keyL.hasEvent(VK_D)) {
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
	public final int getEngineGameFrame() {
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
