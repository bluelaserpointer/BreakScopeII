package engine;

import static java.awt.event.KeyEvent.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
import gui.MessageSource;
import gui.stageEditor.DefaultStageEditor;
import input.key.SingleKeyListener;
import input.key.SingleNumKeyListener;
import input.mouse.MouseListenerEx;
import item.ItemData;
import item.ammo.Ammo_45acp;
import item.ammo.Ammo_9mm;
import item.magicChip.FireBallChip;
import loading.MyDataSaver;
import paint.ImageFrame;
import paint.dot.DotPaint;
import physics.Point;
import physics.Route;
import physics.hitShape.MyPolygon;
import stage.GHQStage;
import structure.Structure;
import structure.Terrain;
import structure.Tile;
import ui.Dialog;
import ui.ESC_menu;
import ui.QuickSlotViewer;
import ui.UnitEditor;
import unit.HumanGuard2;
import unit.NAUnit;
import unit.Player;
import unit.Boss_1;
import unit.GameInput;
import unit.GameInputList;
import unit.HumanGuard;
import unit.Unit;
import vegetation.Vegetation;
import weapon.ACCAR;
import weapon.ElectronShield;
import weapon.Knife;

/**
 * The core class for game "NA"
 * @author bluelaserpointer
 * @version alpha1.0
 */

public class NAGame extends Game implements MessageSource, ActionSource {
	private static NAUnit controllingUnit;
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
							GHQ.stage().addUnit(new HumanGuard()).respawn((Point)ois.readObject()).loadImageData();;
							break;
						case "unit.HumanGuard2":
							GHQ.stage().addUnit(new HumanGuard2()).respawn((Point)ois.readObject());
							break;
						case "unit.controllingUnit":
							GHQ.stage().addUnit(new Player()).respawn((Point)ois.readObject());
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

	protected static final GameInputList gameInputs = new GameInputList();
	public static enum GameInputEnum {
		SHORTCUT0(KeyEvent.VK_0, true),
		SHORTCUT1(KeyEvent.VK_1, true),
		SHORTCUT2(KeyEvent.VK_2, true),
		SHORTCUT3(KeyEvent.VK_3, true),
		SHORTCUT4(KeyEvent.VK_4, true),
		SHORTCUT5(KeyEvent.VK_5, true),
		SHORTCUT6(KeyEvent.VK_6, true),
		SHORTCUT7(KeyEvent.VK_7, true),
		SHORTCUT8(KeyEvent.VK_8, true),
		SHORTCUT9(KeyEvent.VK_9, true),
		WALK_NORTH(KeyEvent.VK_W, true),
		WALK_SOUTH(KeyEvent.VK_S, true),
		WALK_WEST(KeyEvent.VK_A, true),
		WALK_EAST(KeyEvent.VK_D, true),
		FIRE(MouseEvent.BUTTON1, false),
		RELOAD(KeyEvent.VK_R, true),
		SUB(MouseEvent.BUTTON3, false),
		LAST_WEAPON(KeyEvent.VK_Q, true),
		SPRINT(KeyEvent.VK_SHIFT, true),
		ROLL(KeyEvent.VK_SPACE, true),
		INTERACT(KeyEvent.VK_E, true);
		
		private final GameInput input;
		private GameInputEnum(int code, boolean isKeyOrMouse) {
			if(isKeyOrMouse)
				this.input = new GameInput.Keyboard(this.name(), code);
			else
				this.input = new GameInput.Mouse(this.name(), code);
		}
		public GameInput input() {
			return input;
		}
	}
	static {
		for(GameInputEnum ver : GameInputEnum.values())
			gameInputs.addInput(ver.input());
	}
	
	//images
	
	//GUIParts
	private static DefaultStageEditor editor;
	private static GUIParts stageFieldGUI;
	private static GUIParts escMenu;
	private static UnitEditor unitEditor;
	private static Dialog dialog;
	private static QuickSlotViewer quickSlotViewer;
	
	//initialization
	@Override
	public String getTitleName() {
		return "NA";
	}
	public static void main(String args[]){
		new GHQ(new NAGame(), 1000, 600);
	}
	public NAGame() {
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
		//items
		/////////////////////////////////
		/////////////////////////////////
		//units
		/////////////////////////////////
		//friend
		GHQ.stage().addUnit(Unit.initialSpawn(controllingUnit = new Player(), GHQ.screenW()/2, GHQ.screenH() - 100));
		//action
		ActionInfo.clear();
		ActionInfo.addDstPlan(1000, GHQ.screenW() - 200, GHQ.screenH() + 100);
		ActionInfo.addDstPlan(1000, GHQ.screenW() + 200, GHQ.screenH() + 100);
		//final Action moveLeftToRight200 = new Action(this);
		//enemy
		testUnit = 
		GHQ.stage().addUnit(Unit.initialSpawn(new Boss_1(), 700, 20));
		GHQ.stage().addUnit(Unit.initialSpawn(new HumanGuard2(), 1200, 300));
		GHQ.stage().addUnit(Unit.initialSpawn(new HumanGuard2(), 1800, 700));
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
		new Knife().drop(702, 836);
		new ElectronShield().drop(702, 796);
		new FireBallChip().drop(650, 800);
		//stageDataSaver.doLoad(new File("stage/saveData1.txt"));
		/////////////////////////////////
		//GUI
		/////////////////////////////////
		//ESC menu
		GHQ.addGUIParts(new GUIParts() {
			{
				setName("BuffIcons");
				setBounds(250, 500, 500 ,50);
				setBGColor(Color.LIGHT_GRAY);
			}
			@Override
			public void idle() {
				super.idle();
				//show controllingUnit buffs
				int pos = 0;
				for(Buff buff : controllingUnit.buffs()) {
					buff.getRectPaint().rectPaint(point().intX() + pos*50, point().intY(), 50);
					++pos;
				}
				//show buff information
				if(isMouseEntered()) {
					final int MOUSE_POS = (GHQ.mouseScreenX() - point().intX())/50;
					if(MOUSE_POS < controllingUnit.buffs().size()) {
						final int X = point().intX() + MOUSE_POS*50;
						GHQ.getG2D(new Color(0, 0, 0, 100)).fillRect(X, point().intY() - 50, 250, 50);
						GHQ.getG2D(Color.WHITE).drawString(controllingUnit.buffs().get(MOUSE_POS).description(), X, point().intY());
					}
				}
			}
		});
		//GHQ.addGUIParts(dialog = new Dialog()).setBounds(50, 375, 900, 100);
		GHQ.addGUIParts(quickSlotViewer = new QuickSlotViewer(250, 550, 50, controllingUnit.quickSlot())).enable();
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
			public void idle() {
				super.idle();
				///////////////
				//HUD
				///////////////
				final Graphics2D G2 = GHQ.getG2D();
				//HP bars
				G2.setStroke(GHQ.stroke5);
				int barLength;
				barLength = (int)(400*controllingUnit.RED_BAR.getRate());
				if(barLength > 0) {
					G2.setColor(Color.RED);
					G2.drawLine(15, 15, 15 + barLength, 15);
				}
				int shieldBarLength = (int)(controllingUnit.getShield());
				if(shieldBarLength > 0) {
					G2.setColor(Color.CYAN);
					G2.drawLine(15 + barLength, 15, 15 + barLength + shieldBarLength, 15);
				}
				barLength = (int)(400*controllingUnit.BLUE_BAR.getRate());
				if(barLength > 0) {
					G2.setColor(Color.BLUE);
					G2.drawLine(15, 30, 15 + barLength, 30);
				}
				barLength = (int)(400*controllingUnit.GREEN_BAR.getRate());
				if(barLength > 0) {
					G2.setColor(Color.GREEN);
					G2.drawLine(15, 45, 15 + barLength, 45);
				}
				barLength = (int)(400*controllingUnit.ENERGY.getRate());
				if(barLength > 0) {
					G2.setColor(Color.WHITE);
					G2.drawLine(15, 60, 15 + barLength, 60);
				}
				//playerIcon
				int pos = 1;
				if(controllingUnit.personalIcon != null)
					controllingUnit.personalIcon.rectPaint(pos++*90 + 10, GHQ.screenH() - 40, 80, 30);
			}
			@Override
			public boolean clicked(MouseEvent e) {
				NAUnit.gameInputs().mousePressed(e);
				return true;
			}
			@Override
			public void released(MouseEvent e) {
				NAUnit.gameInputs().mouseReleased(e);
			}
			//stage field always does not invoke swap operation.
			@Override
			public void dragIn(GUIParts sourceUI, Object dropObject) {
				final double ANGLE = controllingUnit.point().angleToMouse();
				((ItemData)dropObject).drop((int)(controllingUnit.point().doubleX() + 50*Math.cos(ANGLE)), (int)(controllingUnit.point().doubleY() + 50*Math.sin(ANGLE)));
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
		cornerNavi.setGoalPoint(controllingUnit);
		Route route = cornerNavi.getRoot(testUnit);
		if(route != null)
			route.setDebugEffect(Color.RED, GHQ.stroke5);
	}
	//idle
	private int gameFrame;
	@Override
	public final void idle(Graphics2D g2,int stopEventKind) {
		if(controllingUnit == null || stageFieldGUI == null)
			return;
		gameFrame++;
		//stagePaint
		//background
		GHQ.stage().fill(new Color(112, 173, 71));
		//center point
		g2.setColor(Color.RED);
		g2.fillOval(controllingUnit.point().intX() - 2, controllingUnit.point().intY() - 2, 5, 5);
		////////////////
		final int MOUSE_X = GHQ.mouseX(),MOUSE_Y = GHQ.mouseY();
		if(stopEventKind == GHQ.NONE) {
			//others
			switch(nowStage) {
			case 0:
				//warp
				if(s_keyL.hasEvent(VK_TAB)){
					controllingUnit.point().setXY(MOUSE_X, MOUSE_Y);
				}
				break;
			}
		}
		//////////////////////////
		//idle
		//////////////////////////
		GHQ.stage().idle(GHQObjectType.UNIT);
		GHQ.stage().idle(GHQObjectType.BULLET);
		GHQ.stage().idle(GHQObjectType.EFFECT);
		GHQ.stage().idle(GHQObjectType.STRUCTURE);
		GHQ.stage().idle(GHQObjectType.VEGETATION);
		GHQ.stage().idle(GHQObjectType.ITEM);
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
		}else {
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
		//scroll
		///////////////
		if(stopEventKind == GHQ.NONE || editor.isEnabled()) {
			
			//scroll by mouse
			if(doScrollView) {
				GHQ.viewTargetTo((MOUSE_X + controllingUnit.point().intX())/2,(MOUSE_Y + controllingUnit.point().intY())/2);
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
		if(!GHQ.mouseHook.isEmpty() && e.getButton() == MouseEvent.BUTTON1 && GHQ.isMouseHoveredAnyUI()) {
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
			GHQ.doMouseClickUIEvent(e);
		}
	}
	@Override
	public void keyPressed(KeyEvent e) {
		NAUnit.gameInputs().keyPressed(e);
	}
	@Override
	public void keyReleased(KeyEvent e) {
		NAUnit.gameInputs().keyReleased(e);
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		super.mouseWheelMoved(e);
		if(controllingUnit != null) {
			if(e.getWheelRotation() > 0)
				controllingUnit.changeToNextWeapon();
			else
				controllingUnit.changeToPrevWeapon();
		}
	}
	//information
	public static GameInputList gameInputs() {
		return gameInputs;
	}
	public static boolean hasInput(GameInputEnum inputEnum) {
		return inputEnum.input().hasEvent();
	}
	public static boolean consumeInput(GameInputEnum inputEnum) {
		return inputEnum.input().consume();
	}
	public static NAUnit controllingUnit() {
		return controllingUnit;
	}
	public static Dialog dialog() {
		return dialog;
	}
	public static QuickSlotViewer quickSlotViewer() {
		return quickSlotViewer;
	}
	private boolean doScrollView = true;
}
