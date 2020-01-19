package engine;

import static java.awt.event.KeyEvent.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import action.ActionInfo;
import action.ActionSource;
import buff.Buff;
import core.CornerNavigation;
import core.GHQ;
import core.GHQObjectType;
import core.Game;
import gui.GUIParts;
import gui.InventoryViewer;
import gui.MessageSource;
import gui.stageEditor.DefaultStageEditor;
import input.key.SingleKeyListener;
import input.key.SingleNumKeyListener;
import input.mouse.MouseListenerEx;
import item.ItemData;
import item.ammo.Ammo_45acp;
import item.ammo.Ammo_9mm;
import item.weapon.ACCAR;
import item.weapon.ElectronShield;
import loading.MyDataSaver;
import paint.ImageFrame;
import paint.dot.DotPaint;
import physics.Point;
import physics.Route;
import physics.hitShape.MyPolygon;
import stage.GHQStage;
import storage.TableStorage;
import structure.Structure;
import structure.Terrain;
import structure.Tile;
import ui.ESC_menu;
import ui.UnitEditor;
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
				} else if(structure instanceof Terrain) {
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
		new ACCAR().drop(702, 796);
		new ElectronShield().drop(702, 796);
		//stageDataSaver.doLoad(new File("stage/saveData1.txt"));
		/////////////////////////////////
		//GUI
		/////////////////////////////////
		//ESC menu
		GHQ.addGUIParts(new GUIParts() {
			{
				setBounds(250, 500, 500 ,50);
				setBGColor(Color.LIGHT_GRAY);
			}
			@Override
			public void idle() {
				super.idle();
				//show player buffs
				int pos = 0;
				for(Buff buff : player.buffs()) {
					buff.getRectPaint().rectPaint(point().intX() + pos*50, point().intY(), 50);
					++pos;
				}
			}
		});
		GHQ.addGUIParts(new InventoryViewer(ImageFrame.create("picture/gui/slot.png"), 250, 550, 50, new TableStorage<ItemData>(10, 1, ItemData.BLANK_ITEM)) {
			{
				setName("QuickSlot");
				setBGColor(Color.WHITE);
			}
			@Override
			public void idle() {
				super.idle();
				//quick slot use
				int quickSlotID = SingleNumKeyListener.keyNumToLocationNum(s_numKeyL.pullHasEventKeyNum());
				if(quickSlotID != GHQ.NONE) {
					final ItemData itemData = storage.get(quickSlotID);
					if(itemData != null) {
						itemData.use();
						final Graphics2D g2 = GHQ.getG2D(Color.WHITE, GHQ.stroke3);
						g2.drawRect(point().intX() + quickSlotID*CELL_SIZE, point().intY(), CELL_SIZE, CELL_SIZE);
					}
				}
			}
			@Override
			public boolean doLinkDrag() {
				return true;
			}
		}).enable();
		GHQ.addGUIParts(escMenu = new ESC_menu()).disable();
		GHQ.addGUIParts(editor = new DefaultStageEditor("EDITER_GROUP") {
			@Override
			public void saveStage() {
				stageDataSaver.doSave(new File("stage/saveData1.txt"));
			}
		}).disable();
		editor.addFirst(unitEditor = new UnitEditor()).disable();
		
		GHQ.addGUIParts(stageFieldGUI = new GUIParts() {
			{
				setName("stageFieldGUI");
			}
			@Override
			public void clicked() {
				player.attackOrder = s_mouseL.hasButton1Event();
				player.spellOrder = s_mouseL.pullButton2Event();
			}
			@Override
			public void released() {
				player.attackOrder = player.spellOrder = false;
			}
			//stage field always does not invoke swap operation.
			@Override
			public void dragIn(GUIParts sourceUI, Object dropObject) {
				final double ANGLE = player.point().angleToMouse();
				((ItemData)dropObject).drop((int)(player.point().doubleX() + 50*Math.cos(ANGLE)), (int)(player.point().doubleY() + 50*Math.sin(ANGLE)));
			}
			@Override
			public boolean checkDragIn(GUIParts sourceUI, Object dropObject) { //item throw
				//only check this is a item.
				return dropObject instanceof ItemData;
			}
		});
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
		g2.fillOval(player.point().intX() - 2, player.point().intY() - 2, 5, 5);
		////////////////
		final int MOUSE_X = GHQ.mouseX(),MOUSE_Y = GHQ.mouseY();
		if(stopEventKind == GHQ.NONE) {
			//others
			switch(nowStage) {
			case 0:
				//enemy
				for(Unit enemy : GHQ.stage().units) {
					if(enemy.name() == "FairyA") {
						final int FRAME = gameFrame % 240;
						if(FRAME < 100)
							enemy.point().addSpeed(-1, 0);
						else if(FRAME < 120)
							;
						else if(FRAME < 220)
							enemy.point().addSpeed(1, 0);
						else
							;
					}
				}
				//warp
				if(s_keyL.hasEvent(VK_TAB)){
					player.point().setXY(MOUSE_X, MOUSE_Y);
				}
				break;
			}
		}
		//////////////////////////
		//idle
		//////////////////////////
		//System.out.println("UNIT");
		GHQ.stage().idle(GHQObjectType.UNIT);
		//System.out.println("BULLET");
		GHQ.stage().idle(GHQObjectType.BULLET);
		//System.out.println("EFFECT");
		GHQ.stage().idle(GHQObjectType.EFFECT);
		//System.out.println("STRUCTURE");
		GHQ.stage().idle(GHQObjectType.STRUCTURE);
		//System.out.println("VEGETATION");
		GHQ.stage().idle(GHQObjectType.VEGETATION);
		//System.out.println("ITEM");
		GHQ.stage().idle(GHQObjectType.ITEM);
		////////////
		//focus
		////////////
		if(!escMenu.isEnabled()) {
			g2.setColor(new Color(200,120,10,100));
			g2.setStroke(GHQ.stroke3);
			g2.drawLine(player.point().intX(), player.point().intY(), MOUSE_X, MOUSE_Y);
			focusIF.dotPaint(MOUSE_X, MOUSE_Y);
			final Weapon WEAPON = player.mainEquip().weapon;
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
		final Graphics2D G2 = GHQ.getG2D();
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
			//movement
			final double F_MOVE_SPD;
			boolean dashed = false;
			//dash speed*2
			if(s_keyL.hasEvent(VK_SHIFT) && !player.GREEN_BAR.isMin()) {
				F_MOVE_SPD = GHQ.mulSPF(player.SPEED_PPS.doubleValue())*2.0;
				dashed = true;
			}else
				F_MOVE_SPD = GHQ.mulSPF(player.SPEED_PPS.doubleValue());
			//judge rolling
			boolean doLolling = s_keyL.pullEvent(VK_SPACE) && player.GREEN_BAR.intValue() > 25;
			boolean didLolling = false;
			final double ROLL_STR = player.SPEED_PPS.doubleValue()/10;
			if(s_keyL.hasEvent(VK_W) && !GHQ.stage().hitObstacle_atNewPoint(player, 0, -F_MOVE_SPD)) {
				player.point().addY(-F_MOVE_SPD);
				GHQ.viewMove(0, -F_MOVE_SPD);
				if(doLolling) {
					player.point().addSpeed(0, -ROLL_STR);
					didLolling = true;
				}
			}else if(s_keyL.hasEvent(VK_S) && !GHQ.stage().hitObstacle_atNewPoint(player, 0, +F_MOVE_SPD)) {
				player.point().addY(F_MOVE_SPD);
				GHQ.viewMove(0, F_MOVE_SPD);
				if(doLolling) {
					player.point().addSpeed(0, ROLL_STR);
					didLolling = true;
				}
			}
			if(s_keyL.hasEvent(VK_A) && !GHQ.stage().hitObstacle_atNewPoint(player, -F_MOVE_SPD, 0)) {
				player.point().addX(-F_MOVE_SPD);
				GHQ.viewMove(-F_MOVE_SPD,0);
				if(doLolling) {
					player.point().addSpeed(-ROLL_STR, 0);
					didLolling = true;
				}
			}else if(s_keyL.hasEvent(VK_D) && !GHQ.stage().hitObstacle_atNewPoint(player, +F_MOVE_SPD, 0)) {
				player.point().addX(F_MOVE_SPD);
				GHQ.viewMove(F_MOVE_SPD,0);
				if(doLolling) {
					player.point().addSpeed(ROLL_STR, 0);
					didLolling = true;
				}
			}
			//reduce green bar when dash
			if(dashed && s_keyL.hasEventOne(VK_W, VK_S, VK_A, VK_D)) {
				player.GREEN_BAR.consume(GHQ.getSPF()*15.0);
			}
			//reduce green bar when rolling
			if(didLolling) {
				player.GREEN_BAR.consume(25.0);
			}
			//scroll by mouse
			if(doScrollView) {
				GHQ.viewTargetTo((MOUSE_X + player.point().intX())/2,(MOUSE_Y + player.point().intY())/2);
				GHQ.viewApproach_rate(10);
			}
		}
		//////////////////////////
		//test
		//////////////////////////
		cornerNavi.debugPreview();
	}
	//drag
	@Override
	public void mousePressed(MouseEvent e) {
		if(!GHQ.mouseHook.isEmpty() && e.getButton() == MouseEvent.BUTTON1 && GHQ.isMouseHoveredUI()) {
			//drag event
			final Object OBJ = GHQ.mouseHook.get(); //object for drag In/Out
			final GUIParts SRC = GHQ.mouseHook.sourceUI(); //drag source
			final GUIParts DST = GHQ.mouseHoveredUI(); //drag destination
			final boolean dragInPermit = DST.checkDragIn(SRC, OBJ);
			final boolean dragOutPermit = SRC.checkDragOut(DST, OBJ);
			final boolean dragPermit = dragInPermit && dragOutPermit; //judge this drag action is legal.
			GHQ.mouseHook.clear(); //release mouse hooked object
			//check general item drag I/O rule
			if(SRC.doLinkDrag() && !DST.doLinkDrag() && dragOutPermit) { //delete link
				SRC.dragOut(DST, OBJ, null);
			} else if(!SRC.doLinkDrag() && DST.doLinkDrag() && dragInPermit) { //create link
				DST.dragIn(SRC, OBJ);
			} else if(dragPermit) { //swap link || swap real object
				SRC.dragOut(DST, OBJ, DST.peekDragObject());
				DST.dragIn(SRC, OBJ);
			}
			SRC.dragFinished();
			DST.dragFinished();
		}else {
			GHQ.mouseHoveredUI().clicked();
		}
	}
	
	//information
	public static Player player() {
		return player;
	}
	private boolean doScrollView = true;
}
