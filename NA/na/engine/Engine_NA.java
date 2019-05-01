package engine;

import static java.awt.event.KeyEvent.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;

import action.ActionInfo;
import action.ActionSource;
import core.GHQ;
import gui.ItemStorageViewer;
import gui.BasicButton;
import gui.DefaultStageEditor;
import gui.GUIGroup;
import gui.MessageSource;
import gui.MouseHook;
import gui.TitledLabel;
import gui.UnitEditor;
import input.MouseListenerEx;
import input.SingleKeyListener;
import input.SingleNumKeyListener;
import item.Ammo;
import item.Equipment;
import item.Item;
import paint.ColorFilling;
import paint.ImageFrame;
import paint.RectPaint;
import stage.StageEngine;
import stage.StageSaveData;
import storage.TableStorage;
import structure.Structure;
import unit.BasicUnit;
import unit.BlackMan;
import unit.BulletLibrary;
import unit.EffectLibrary;
import unit.Fairy;
import unit.Player;
import unit.Unit;
import unit.WhiteMan;
import vegetation.Vegetation;

/**
 * The core class for game "NA"
 * @author bluelaserpointer
 * @version alpha1.0
 */

public class Engine_NA extends StageEngine implements MessageSource,ActionSource{
	public static final int FRIEND = 0,ENEMY = 100;
	
	private static Player player;
	private static final Stage_NA[] stages = new Stage_NA[1];
	private int nowStage;
	
	final int F_MOVE_SPD = 6;
	
	int formationCenterX,formationCenterY;
	
	private int stageW,stageH;
	
	public String getVersion() {
		return "alpha1.0.0";
	}
	
	//inputEvnet
	private static final int inputKeys[] = 
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
	public static final MouseListenerEx s_mouseL = new MouseListenerEx();
	public static final SingleKeyListener s_keyL = new SingleKeyListener(inputKeys);
	public static final SingleNumKeyListener s_numKeyL = new SingleNumKeyListener();
	
	//images
	
	int focusIID,magicCircleIID;
	
	//GUIParts
	private static DefaultStageEditor editor;
	private static GUIGroup stageFieldGUI;
	private static GUIGroup escMenu;
	private static ItemStorageViewer inventoryViewer;
	private static TitledLabel mainWeaponLabel,subWeaponLabel,meleeWeaponLabel;
	private static BasicButton useItemButton;
	private static MouseHook<Item> itemMouseHook;
	private static UnitEditor unitEditor;
	
	//initialization
	@Override
	public String getTitleName() {
		return "NA";
	}
	public static void main(String args[]){
		new GHQ(new Engine_NA());
	}
	@Override
	public final void loadResource() {
		/////////////////////////////////
		//images this engine required
		/////////////////////////////////
		focusIID = GHQ.loadImage("thhimage/focus.png");
		magicCircleIID = GHQ.loadImage("thhimage/MagicCircle.png");
		/////////////////////////////////
		//stage
		/////////////////////////////////
		stageW = stageH = 5000;
		/////////////////////////////////
		//items
		/////////////////////////////////
		Ammo.loadResource();
		Equipment.loadResource();
		/////////////////////////////////
		//bullets && effects
		/////////////////////////////////
		BulletLibrary.loadResource();
		EffectLibrary.loadResource();
		/////////////////////////////////
		//units
		/////////////////////////////////
		//formation
		formationCenterX = GHQ.getScreenW()/2;formationCenterY = GHQ.getScreenH() - 100;
		//friend
		GHQ.addUnit(Unit.initialSpawn(player = new Player(FRIEND),formationCenterX,formationCenterY));
		//action
		ActionInfo.clear();
		ActionInfo.addDstPlan(1000, GHQ.getScreenW() - 200, GHQ.getScreenH() + 100);
		ActionInfo.addDstPlan(1000, GHQ.getScreenW() + 200, GHQ.getScreenH() + 100);
		//final Action moveLeftToRight200 = new Action(this);
		//enemy
		GHQ.addUnit(Unit.initialSpawn(new Fairy(ENEMY), 300, 100));
		GHQ.addUnit(Unit.initialSpawn(new Fairy(ENEMY), 700, 20));
		GHQ.addUnit(Unit.initialSpawn(new Fairy(ENEMY), 1200, 300));
		GHQ.addUnit(Unit.initialSpawn(new Fairy(ENEMY), 1800, 700));
		GHQ.addUnit(Unit.initialSpawn(new WhiteMan(ENEMY), 400, GHQ.random2(100, 150)));
		GHQ.addUnit(Unit.initialSpawn(new BlackMan(ENEMY), 200, GHQ.random2(100, 150)));
		/////////////////////////////////
		//vegetation
		/////////////////////////////////
		GHQ.addVegetation(new Vegetation(new ImageFrame("thhimage/veg_leaf.png"),1172,886));
		GHQ.addVegetation(new Vegetation(new ImageFrame("thhimage/veg_flower.png"),1200,800));
		GHQ.addVegetation(new Vegetation(new ImageFrame("thhimage/veg_leaf2.png"),1800,350));
		GHQ.addVegetation(new Vegetation(new ImageFrame("thhimage/veg_stone.png"),1160,870));
		GHQ.addVegetation(new Vegetation(new ImageFrame("thhimage/veg_leaf3.png"),1102,830));
		GHQ.addVegetation(new Vegetation(new ImageFrame("thhimage/veg_leaf3.png"),1122,815));
		GHQ.addVegetation(new Vegetation(new ImageFrame("thhimage/veg_leaf3.png"),822,886));
		GHQ.addVegetation(new Ammo(Ammo.AMMO_9MM,10).drop(822,886));
		GHQ.addVegetation(new Ammo(Ammo.AMMO_45,10).drop(862,896));
		GHQ.addVegetation(new Equipment(Equipment.ACCAR).drop(702,796));
		GHQ.addVegetation(new Equipment(Equipment.ELECTRON_SHIELD).drop(702,856));
		/////////////////////////////////
		//GUI
		/////////////////////////////////
		//ESC menu
		GHQ.addGUIParts(escMenu = new GUIGroup("ESC_MENU", null, 0, 0, GHQ.getScreenW(), GHQ.getScreenH())).addParts(inventoryViewer = new ItemStorageViewer("MENU_GROUP",RectPaint.BLANK_SCRIPT,new ImageFrame("picture/gui/slot.png"),50,70,70,(TableStorage<Item>)player.inventory.items){
			@Override
			public void clicked() {
				final int HOVERED_ID = getMouseHoveredID();
				if(itemMouseHook.hasObject()) {
					itemMouseHook.hook(storage.set(HOVERED_ID, itemMouseHook.get()));
				}else {
					itemMouseHook.hook(storage.remove(HOVERED_ID));
				}
			}
			@Override
			public void released() {
				clicked();
			}
			@Override
			public void outsideReleased() {
				final Item hookingObject = itemMouseHook.get();
				if(hookingObject instanceof Equipment) {
					final Equipment EQUIPMENT = (Equipment)hookingObject;
					if(mainWeaponLabel.isMouseEntered()) {
						player.mainWeapon = player.getWeapon(EQUIPMENT);
						itemMouseHook.hook(Item.BLANK_ITEM);
						return;
					}else if(subWeaponLabel.isMouseEntered()) {
						player.subWeapon = player.getWeapon(EQUIPMENT);
						itemMouseHook.hook(Item.BLANK_ITEM);
						return;
					}else if(meleeWeaponLabel.isMouseEntered()) {
						player.meleeWeapon = player.getWeapon(EQUIPMENT);
						itemMouseHook.hook(Item.BLANK_ITEM);
						return;
					}
				}
				final double ANGLE = player.dynam.getMouseAngle();
				if(hookingObject != null)
					GHQ.addVegetation(hookingObject.drop((int)(player.dynam.getX() + 50*Math.cos(ANGLE)), (int)(player.dynam.getY() + 50*Math.sin(ANGLE))));
				itemMouseHook.hook(Item.BLANK_ITEM);
			}
		});
		escMenu.addParts(mainWeaponLabel = new TitledLabel("mainWeaponLabel", new ColorFilling(Color.WHITE), 500, 70, 400, 40)).setTitle("mainWeapon");
		escMenu.addParts(subWeaponLabel = new TitledLabel("subWeaponLabel", new ColorFilling(Color.WHITE), 500, 140, 400, 40)).setTitle("subWeapon");
		escMenu.addParts(meleeWeaponLabel = new TitledLabel("meleeWeaponLabel", new ColorFilling(Color.WHITE), 500, 210, 400, 40)).setTitle("meleeWeapon");
		escMenu.addParts(useItemButton = new BasicButton("useItemButton", new ImageFrame("picture/gui/slot.png"), 60, 500, 100, 40) {
			@Override
			public void clicked() { //useItem or equipWeapon
				
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
		editor.addPartsToTop(unitEditor = new UnitEditor());
		GHQ.addGUIParts(stageFieldGUI = new GUIGroup("stageFieldGUI", null, 0, 0, GHQ.getScreenW(), GHQ.getScreenH())).enable();
		/////////////////////////////////
		//input
		/////////////////////////////////
		GHQ.addListenerEx(s_mouseL);
		GHQ.addListenerEx(s_keyL);
		GHQ.addListenerEx(s_numKeyL);
	}
	@Override
	public final void openStage() {
		stages[0] = (Stage_NA)GHQ.loadData(new File("stage/saveData1.txt"));
		if(stages[0] != null) {
			for(Structure structure : stages[0].STRUCTURES) {
				GHQ.addStructure(structure);
			}
		}
		GHQ.addMessage(this,"Press enter key to start.");
		s_keyL.enable();
		s_numKeyL.enable();
		s_mouseL.enable();
	}
	@Override
	public final StageSaveData getStageSaveData() {
		return new Stage_NA(GHQ.getUnits(),GHQ.getStructures());
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
				player.idle();
				//enemy
				for(Unit enemy : GHQ.getUnitList()) {
					enemy.idle();
					if(enemy.getName() == "FairyA") {
						final int FRAME = gameFrame % 240;
						if(FRAME < 100)
							enemy.getDynam().setSpeed(-5, 0);
						else if(FRAME < 120)
							enemy.getDynam().setSpeed(0, 0);
						else if(FRAME < 220)
							enemy.getDynam().setSpeed(5, 0);
						else
							enemy.getDynam().setSpeed(0, 0);
					}
				}
				//leap
				if(s_keyL.hasEvent(VK_SHIFT)){
					formationCenterX = MOUSE_X;formationCenterY = MOUSE_Y;
					player.teleportTo(formationCenterX, formationCenterY);
				}
				if(stageFieldGUI.isClicking()) {
					//shot
					player.attackOrder = s_mouseL.hasButton1Event();
					//spell
					player.spellOrder = s_mouseL.pullButton2Event();
				}else {
					player.attackOrder = player.spellOrder = false;
				}
				break;
			}
		}else if(stopEventKind == GHQ.STOP)
			GHQ.defaultCharaIdle(GHQ.getUnitList());
		GHQ.defaultEntityIdle();
		////////////
		//focus
		////////////
		g2.setColor(new Color(200,120,10,100));
		g2.setStroke(GHQ.stroke3);
		g2.drawLine(formationCenterX,formationCenterY,MOUSE_X,MOUSE_Y);
		GHQ.drawImageGHQ_center(focusIID,MOUSE_X,MOUSE_Y);
		g2.setColor(player.mainWeapon.canFire() ? Color.WHITE : Color.RED);
		final int UNFIRED = player.mainWeapon.getMagazineFilledSpace(), LEFT_AMMO = player.mainWeapon.getLeftAmmo();
		g2.drawString(UNFIRED != GHQ.MAX ? String.valueOf(UNFIRED) : "-", MOUSE_X - 25, MOUSE_Y);
		g2.drawString(LEFT_AMMO != GHQ.MAX ? String.valueOf(LEFT_AMMO) : "-", MOUSE_X, MOUSE_Y);
		g2.setColor(player.mainWeapon.isCoolFinished() ? Color.WHITE : Color.RED);
		g2.drawString(player.mainWeapon.getCoolProgress() + "/" + 50, MOUSE_X - 25, MOUSE_Y + 25);
		g2.setColor(player.mainWeapon.isReloadFinished() ? Color.WHITE : Color.RED);
		g2.drawString(player.mainWeapon.getReloadProgress() + "/" + 150, MOUSE_X + 35, MOUSE_Y + 25);
		////////////
		//editor
		////////////
		if(s_keyL.pullEvent(VK_F6)) {
			editor.flit();
			if(editor.isEnabled())
				inventoryViewer.disable();
		}
		if(editor.isEnabled()){ //editor GUI
			if(s_mouseL.pullButton3Event()) {
				unitEditor.setWithEnable(GHQ.getMouseOverUnit());
			}
		}else { //game GUI
			GHQ.translateForGUI(true);
			int pos = 1;
			if(player.iconPaint != null)
				player.iconPaint.rectPaint(pos++*90 + 10, GHQ.getScreenH() - 40, 80, 30);
			GHQ.translateForGUI(false);
			if(s_keyL.pullEvent(VK_ESCAPE)) {
				if(escMenu.isEnabled()) {
					escMenu.disable();
					GHQ.clearStopEvent();
				}else {
					escMenu.enable();
					GHQ.stopScreen();
				}
			}
		}
		///////////////
		//GUI idle
		///////////////
		final Graphics2D G2 = GHQ.getGraphics2D();
		//HP bars
		G2.setStroke(GHQ.stroke5);
		GHQ.translateForGUI(true);
		G2.setColor(Color.RED);
		G2.drawLine(15, 15, 15 + Math.max(0, (int)(400*player.status.getRate(BasicUnit.RED_BAR))), 15);
		G2.setColor(Color.BLUE);
		G2.drawLine(15, 30, 15 + Math.max(0, (int)(400*player.status.getRate(BasicUnit.BLUE_BAR))), 30);
		G2.setColor(Color.GREEN);
		G2.drawLine(15, 45, 15 + Math.max(0, (int)(400*player.status.getRate(BasicUnit.GREEN_BAR))), 45);
		GHQ.translateForGUI(false);
		//weaponName update
		mainWeaponLabel.setText(player.mainWeapon.NAME);
		subWeaponLabel.setText(player.subWeapon.NAME);
		meleeWeaponLabel.setText(player.meleeWeapon.NAME);
		///////////////
		//scroll
		///////////////
		if(stopEventKind == NONE || editor.isEnabled()) {
			//scroll by keys
			if(s_keyL.hasEvent(VK_W) && !GHQ.hitObstacle_DXDY(player, 0, -F_MOVE_SPD)) {
				formationCenterY -= F_MOVE_SPD;
				GHQ.viewTargetMove(0,-F_MOVE_SPD);
				GHQ.pureViewMove(0,-F_MOVE_SPD);
			}else if(s_keyL.hasEvent(VK_S) && !GHQ.hitObstacle_DXDY(player, 0, +F_MOVE_SPD)) {
				formationCenterY += F_MOVE_SPD;
				GHQ.viewTargetMove(0,F_MOVE_SPD);
				GHQ.pureViewMove(0,F_MOVE_SPD);
			}
			if(s_keyL.hasEvent(VK_A) && !GHQ.hitObstacle_DXDY(player, -F_MOVE_SPD, 0)) {
				formationCenterX -= F_MOVE_SPD;
				GHQ.viewTargetMove(-F_MOVE_SPD,0);
				GHQ.pureViewMove(-F_MOVE_SPD,0);
			}else if(s_keyL.hasEvent(VK_D) && !GHQ.hitObstacle_DXDY(player, +F_MOVE_SPD, 0)) {
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
