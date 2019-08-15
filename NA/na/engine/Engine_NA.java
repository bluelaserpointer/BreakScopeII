package engine;

import static java.awt.event.KeyEvent.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import action.ActionInfo;
import action.ActionSource;
import core.CornerNavigation;
import core.GHQ;
import core.GHQStage;
import core.Game;
import gui.DefaultStageEditor;
import gui.GUIParts;
import gui.MessageSource;
import gui.UnitEditor;
import hitShape.MyPolygon;
import input.key.SingleKeyListener;
import input.key.SingleNumKeyListener;
import input.mouse.MouseListenerEx;
import item.ammo.Ammo_45acp;
import item.ammo.Ammo_9mm;
import item.weapon.ACCAR;
import item.weapon.ElectronShield;
import item.weapon.Equipment;
import loading.MyDataSaver;
import paint.ImageFrame;
import paint.dot.DotPaint;
import physics.Point;
import physics.Route;
import structure.Structure;
import structure.Terrain;
import structure.Tile;
import ui.ESC_menu;
import unit.HumanGuard2;
import unit.HumanGuard;
import unit.Player;
import unit.Unit;
import vegetation.Vegetation;
import weapon.Weapon;

/**
 * The core class for game "NA"
 * @author bluelaserpointer
 * @version alpha1.0
 */

public class Engine_NA extends Game implements MessageSource,ActionSource{
	public static final int FRIEND = 0,ENEMY = 100;
	
	private static Player player;
	private static final CornerNavigation cornerNavi = new CornerNavigation(100);
	private int nowStage;
	
	public String getVersion() {
		return "alpha1.0.0";
	}
	
	private static final MyDataSaver stageDataSaver = new MyDataSaver() {
		@Override
		public void output(ObjectOutputStream oos) throws IOException {
			//version
			oos.writeInt(0);
			//enemy
			oos.writeInt(GHQ.stage().units.size());
			for(Unit unit : GHQ.stage().units) {
				oos.writeObject(unit.getClass().getName());
				oos.writeObject(unit.point());
			}
			//structure
			oos.writeInt(GHQ.stage().structures.size());
			for(Structure structure : GHQ.stage().structures) {
				if(structure instanceof Tile) {
					oos.writeObject("Tile");
					final Tile tile = (Tile)structure;
					oos.writeInt(tile.point().intX());
					oos.writeInt(tile.point().intY());
					oos.writeInt(tile.xTiles());
					oos.writeInt(tile.yTiles());
				}else if(structure instanceof Terrain) {
					oos.writeObject("Terrain");
					oos.writeObject(((Terrain)structure).hitShape());
				}
			}
			//vegetation
			oos.writeInt(GHQ.stage().vegetations.size());
			for(Vegetation vegetation : GHQ.stage().vegetations) {
				oos.writeObject(vegetation.getDotPaint());
				oos.writeObject(vegetation.point());
			}
		}
		@Override
		public void input(ObjectInputStream ois) throws IOException {
			switch(ois.readInt()) {
			case 0:
				//enemy
				final int ENEMY_AMOUNT = ois.readInt();
				for(int i = 0;i < ENEMY_AMOUNT;++i) {
					try {
						final String unitName = (String)ois.readObject();
						System.out.println(unitName);
						switch(unitName) {
						case "unit.HumanGuard":
							GHQ.stage().addUnit(new HumanGuard(ENEMY)).respawn((Point)ois.readObject()).loadImageData();;
							break;
						case "unit.HumanGuard2":
							GHQ.stage().addUnit(new HumanGuard2(ENEMY)).respawn((Point)ois.readObject());
							break;
						case "unit.Player":
							GHQ.stage().addUnit(new Player(FRIEND)).respawn((Point)ois.readObject());
							break;
						default:
							System.out.println("unknown unit name: " + unitName);
						}
					}catch(ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				//structure
				final int STRUCTURE_AMOUNT = ois.readInt();
				for(int i = 0;i < STRUCTURE_AMOUNT;++i) {
					try {
						final String structureName = (String)ois.readObject();
						System.out.println(structureName);
						switch(structureName) {
						case "Tile":
							GHQ.stage().addStructure(new Tile(ois.readInt(), ois.readInt(), ois.readInt(), ois.readInt()));
							break;
						case "Terrain":
							GHQ.stage().addStructure(new Terrain((MyPolygon)ois.readObject()));
							break;
						default:
							System.out.println("unknown structure name: " + structureName);
						}
					}catch(ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				//vegetation
				final int VEG_AMOUNT = ois.readInt();
				for(int i = 0;i < VEG_AMOUNT;++i) {
					try {
						final DotPaint dotPaint = (DotPaint)ois.readObject();
						if(dotPaint instanceof ImageFrame)
							((ImageFrame)dotPaint).loadFromSave();
						GHQ.stage().addVegetation(new Vegetation(dotPaint, (Point)ois.readObject()));
					}catch(ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}
	};
	
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
	private static ImageFrame focusIF;
	
	//GUIParts
	private static DefaultStageEditor editor;
	private static GUIParts stageFieldGUI;
	private static GUIParts escMenu;
	private static UnitEditor unitEditor;
	
	//initialization
	@Override
	public String getTitleName() {
		return "NA";
	}
	public static void main(String args[]){
		new GHQ(new Engine_NA(), 1000, 600);
	}
	public Engine_NA() {
		super(null);
	}
	static Unit testUnit = null;
	@Override
	public final GHQStage loadStage() {
		return new GHQStage(5000, 5000);
	}
	@Override
	public final void loadResource() {
		/////////////////////////////////
		//images this engine required
		/////////////////////////////////
		focusIF = ImageFrame.create("thhimage/focus.png");
		/////////////////////////////////
		//items
		/////////////////////////////////
		/////////////////////////////////
		//units
		/////////////////////////////////
		//friend
		GHQ.stage().addUnit(Unit.initialSpawn(player = new Player(FRIEND), GHQ.screenW()/2, GHQ.screenH() - 100));
		//action
		ActionInfo.clear();
		ActionInfo.addDstPlan(1000, GHQ.screenW() - 200, GHQ.screenH() + 100);
		ActionInfo.addDstPlan(1000, GHQ.screenW() + 200, GHQ.screenH() + 100);
		//final Action moveLeftToRight200 = new Action(this);
		//enemy
		testUnit = 
		GHQ.stage().addUnit(Unit.initialSpawn(new HumanGuard2(ENEMY), 300, 100));
		GHQ.stage().addUnit(Unit.initialSpawn(new HumanGuard2(ENEMY), 700, 20));
		GHQ.stage().addUnit(Unit.initialSpawn(new HumanGuard2(ENEMY), 1200, 300));
		GHQ.stage().addUnit(Unit.initialSpawn(new HumanGuard2(ENEMY), 1800, 700));
		//GHQ.stage().addUnit(Unit.initialSpawn(new HumanGuard(ENEMY), 400, GHQ.random2(100, 150)));
		/////////////////////////////////
		//vegetation
		/////////////////////////////////
		GHQ.stage().addVegetation(new Vegetation(ImageFrame.create("thhimage/veg_leaf.png"),1172,886));
		GHQ.stage().addVegetation(new Vegetation(ImageFrame.create("thhimage/veg_flower.png"),1200,800));
		GHQ.stage().addVegetation(new Vegetation(ImageFrame.create("thhimage/veg_leaf2.png"),1800,350));
		GHQ.stage().addVegetation(new Vegetation(ImageFrame.create("thhimage/veg_stone.png"),1160,870));
		GHQ.stage().addVegetation(new Vegetation(ImageFrame.create("thhimage/veg_leaf3.png"),1102,830));
		GHQ.stage().addVegetation(new Vegetation(ImageFrame.create("thhimage/veg_leaf3.png"),1122,815));
		GHQ.stage().addVegetation(new Vegetation(ImageFrame.create("thhimage/veg_leaf3.png"),822,886));
		new Ammo_9mm(10).drop(822, 886);
		new Ammo_45acp(10).drop(862, 896);
		new ACCAR().drop(702,796);
		new ElectronShield().drop(702,796);
		//stageDataSaver.doLoad(new File("stage/saveData1.txt"));
		/////////////////////////////////
		//GUI
		/////////////////////////////////
		//ESC menu
		GHQ.addGUIParts(escMenu = new ESC_menu());
		GHQ.addGUIParts(editor = new DefaultStageEditor("EDITER_GROUP") {
			@Override
			public void saveStage() {
				stageDataSaver.doSave(new File("stage/saveData1.txt"));
			}
		});
		editor.addFirst(unitEditor = new UnitEditor());
		GHQ.addGUIParts(stageFieldGUI = new GUIParts().setName("stageFieldGUI")).enable();
		/////////////////////////////////
		//input
		/////////////////////////////////
		GHQ.addMessage(this,"Press enter key to start.");
		/////////////////////////////////
		//test
		/////////////////////////////////
		cornerNavi.defaultCornerCollect();
		cornerNavi.startCornerLink();
		cornerNavi.setGoalPoint(player);
		Route route = cornerNavi.getRoot(testUnit);
		if(route != null)
			route.setDebugEffect(Color.RED, GHQ.stroke5);
	}
	//idle
	private int gameFrame;
	@Override
	public final void idle(Graphics2D g2,int stopEventKind) {
		if(player == null || stageFieldGUI == null)
			return;
		gameFrame++;
		//stagePaint
		//background
		GHQ.stage().fill(new Color(112, 173, 71));
		//center point
		g2.setColor(Color.RED);
		g2.fillOval(player.dynam.intX() - 2, player.dynam.intY() - 2, 5, 5);
		////////////////
		final int MOUSE_X = GHQ.mouseX(),MOUSE_Y = GHQ.mouseY();
		if(stopEventKind == GHQ.NONE) {
			//others
			switch(nowStage) {
			case 0:
				//enemy
				for(Unit enemy : GHQ.stage().units) {
					if(enemy.getName() == "FairyA") {
						final int FRAME = gameFrame % 240;
						if(FRAME < 100)
							;//enemy.dynam().addSpeed(-1, 0);
						else if(FRAME < 120)
							;
						else if(FRAME < 220)
							;//enemy.dynam().addSpeed(1, 0);
						else
							;
					}
				}
				//warp
				if(s_keyL.hasEvent(VK_TAB)){
					player.dynam.setXY(MOUSE_X, MOUSE_Y);
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
		}
		////////////
		//focus
		////////////
		g2.setColor(new Color(200,120,10,100));
		g2.setStroke(GHQ.stroke3);
		g2.drawLine(player.dynam.intX(), player.dynam.intY(), MOUSE_X, MOUSE_Y);
		focusIF.dotPaint(MOUSE_X, MOUSE_Y);
		if(player.mainSlot instanceof Equipment) {
			final Weapon WEAPON = ((Equipment)player.mainSlot).weapon;
			g2.setColor(WEAPON.canFire() ? Color.WHITE : Color.RED);
			final int UNFIRED = WEAPON.getMagazineFilledSpace(), LEFT_AMMO = WEAPON.getLeftAmmo();
			g2.drawString(UNFIRED != GHQ.MAX ? String.valueOf(UNFIRED) : "-", MOUSE_X - 25, MOUSE_Y);
			g2.drawString(LEFT_AMMO != GHQ.MAX ? String.valueOf(LEFT_AMMO) : "-", MOUSE_X, MOUSE_Y);
			g2.setColor(WEAPON.isCoolFinished() ? Color.WHITE : Color.RED);
			g2.drawString(WEAPON.getCoolProgress() + "/" + 50, MOUSE_X - 25, MOUSE_Y + 25);
			g2.setColor(WEAPON.isReloadFinished() ? Color.WHITE : Color.RED);
			g2.drawString(WEAPON.getReloadProgress() + "/" + 150, MOUSE_X + 35, MOUSE_Y + 25);
		}
		////////////
		//editor
		////////////
		if(s_keyL.pullEvent(VK_F6)) {
			editor.flit();
			if(editor.isEnabled())
				escMenu.disable();
		}
		if(editor.isEnabled()){ //editor GUI
			if(s_mouseL.pullButton3Event()) {
				unitEditor.tryOpen(GHQ.stage().units.forMouseOver());
			}
		}else { //game GUI
			GHQ.translateForGUI(true);
			int pos = 1;
			if(player.iconPaint != null)
				player.iconPaint.rectPaint(pos++*90 + 10, GHQ.screenH() - 40, 80, 30);
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
		int barLength;
		barLength = (int)(400*player.RED_BAR.getRate());
		if(barLength > 0) {
			G2.setColor(Color.RED);
			G2.drawLine(15, 15, 15 + barLength, 15);
		}
		int shieldBarLength = (int)(player.getShield());
		if(shieldBarLength > 0) {
			G2.setColor(Color.CYAN);
			G2.drawLine(15 + barLength, 15, 15 + barLength + shieldBarLength, 15);
		}
		barLength = (int)(400*player.BLUE_BAR.getRate());
		if(barLength > 0) {
			G2.setColor(Color.BLUE);
			G2.drawLine(15, 30, 15 + barLength, 30);
		}
		barLength = (int)(400*player.GREEN_BAR.getRate());
		if(barLength > 0) {
			G2.setColor(Color.GREEN);
			G2.drawLine(15, 45, 15 + barLength, 45);
		}
		barLength = (int)(400*player.ENERGY.getRate());
		if(barLength > 0) {
			G2.setColor(Color.WHITE);
			G2.drawLine(15, 60, 15 + barLength, 60);
		}
		GHQ.translateForGUI(false);
		///////////////
		//scroll
		///////////////
		if(stopEventKind == GHQ.NONE || editor.isEnabled()) {
			final int F_MOVE_SPD;
			boolean dashed = false;
			//dash speed*2
			if(s_keyL.hasEvent(VK_SHIFT)) {
				F_MOVE_SPD = (int)(GHQ.mulSPF(player.SPEED_PPS.doubleValue()))*2;
				dashed = true;
			}else
				F_MOVE_SPD = (int)(GHQ.mulSPF(player.SPEED_PPS.doubleValue()));
			//judge rolling
			boolean doLolling = s_keyL.pullEvent(VK_SPACE) && player.GREEN_BAR.intValue() > 25;
			boolean didLolling = false;
			final double ROLL_STR = player.SPEED_PPS.doubleValue()/10;
			if(s_keyL.hasEvent(VK_W) && !GHQ.stage().hitObstacle_atNewPoint(player, 0, -F_MOVE_SPD)) {
				player.dynam.addY(-F_MOVE_SPD);
				GHQ.viewTargetMove(0, -F_MOVE_SPD);
				GHQ.pureViewMove(0, -F_MOVE_SPD);
				if(doLolling) {
					player.dynam.addSpeed(0, -ROLL_STR);
					didLolling = true;
				}
			}else if(s_keyL.hasEvent(VK_S) && !GHQ.stage().hitObstacle_atNewPoint(player, 0, +F_MOVE_SPD)) {
				player.dynam.addY(F_MOVE_SPD);
				GHQ.viewTargetMove(0, F_MOVE_SPD);
				GHQ.pureViewMove(0, F_MOVE_SPD);
				if(doLolling) {
					player.dynam.addSpeed(0, ROLL_STR);
					didLolling = true;
				}
			}
			if(s_keyL.hasEvent(VK_A) && !GHQ.stage().hitObstacle_atNewPoint(player, -F_MOVE_SPD, 0)) {
				player.dynam.addX(-F_MOVE_SPD);
				GHQ.viewTargetMove(-F_MOVE_SPD,0);
				GHQ.pureViewMove(-F_MOVE_SPD,0);
				if(doLolling) {
					player.dynam.addSpeed(-ROLL_STR, 0);
					didLolling = true;
				}
			}else if(s_keyL.hasEvent(VK_D) && !GHQ.stage().hitObstacle_atNewPoint(player, +F_MOVE_SPD, 0)) {
				player.dynam.addX(F_MOVE_SPD);
				GHQ.viewTargetMove(F_MOVE_SPD,0);
				GHQ.pureViewMove(F_MOVE_SPD,0);
				if(doLolling) {
					player.dynam.addSpeed(ROLL_STR, 0);
					didLolling = true;
				}
			}
			//reduce green bar when dash
			if(dashed && s_keyL.hasEventOne(VK_W, VK_S, VK_A, VK_D)) {
				player.GREEN_BAR.consume_getEffect(GHQ.getSPF()*15.0).doubleValue();
			}
			//reduce green bar when rolling
			if(didLolling) {
				player.GREEN_BAR.consume_getEffect(25.0).doubleValue();
			}
			//scroll by mouse
			if(doScrollView) {
				GHQ.viewTargetTo((MOUSE_X + player.dynam.intX())/2,(MOUSE_Y + player.dynam.intY())/2);
				GHQ.viewApproach_rate(10);
			}
		}
		//////////////////////////
		//idle
		//////////////////////////
		GHQ.stage().idle();
		//////////////////////////
		//test
		//////////////////////////
		cornerNavi.debugPreview();
	}
	public static Player getPlayer() {
		return player;
	}
	private boolean doScrollView = true;
}
