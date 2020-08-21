package engine;

import static java.awt.event.KeyEvent.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Random;

import action.ActionSource;
import buff.Buff;
import camera.Camera;
import camera.FixChaseSmoothPeekCamera;
import core.CornerNavigation;
import core.GHQ;
import core.Game;
import gui.GUIParts;
import gui.ItemStorageViewer;
import gui.stageEditor.DefaultStageEditor;
import input.key.SingleKeyListener;
import input.key.SingleNumKeyListener;
import input.mouse.MouseListenerEx;
import item.ArmyBox;
import item.LiquidBarrel;
import item.NAUsable;
import item.ShieldCharger;
import item.ammo.AmmoType;
import item.ammo.enchant.Penetration;
import item.ammo.enchant.Poison;
import item.ammo.enchant.Scatter;
import item.defenceKit.AmmoBox;
import item.defenceKit.Turret;
import item.equipment.weapon.ElectronShield;
import item.equipment.weapon.Knife;
import item.equipment.weapon.LiquidGun;
import item.equipment.weapon.Type56;
import item.magicChip.FireBallChip;
import item.magicChip.WaterSplashChip;
import liquid.Flame;
import liquid.NALiquidState;
import liquid.Oil;
import liquid.PoisonusWater;
import liquid.Water;
import paint.ImageFrame;
import physics.Point;
import physics.stage.GHQStage;
import preset.item.ItemData;
import preset.unit.GameInput;
import preset.unit.GameInputList;
import preset.unit.Unit;
import saveLoader.SaveLoader;
import saveLoader.SaveLoaderV1_0;
import stage.NAStage;
import storage.TableStorage;
import structure.NATile;
import structure.NATileEnum;
import ui.Dialog;
import ui.EscMenu;
import ui.HUD;
import ui.DoubleInventoryViewer;
import ui.QuickSlotViewer;
import ui.StageViewer;
import ui.UnitEditor;
import unit.HumanGuard2;
import unit.NAUnit;
import unit.Player;
import vegetation.AmmoArea;
import vegetation.DownStair;
import vegetation.EnemyGate;

/**
 * The core class for game "NA"
 * @author bluelaserpointer
 * @version alpha1.0
 */

public class NAGame extends Game implements ActionSource {
	private static NAUnit controllingUnit;
	private static boolean lockControllingUnitAction;
	public static final CornerNavigation cornerNavi = new CornerNavigation(25);
	public static final FixChaseSmoothPeekCamera playerCamera = new FixChaseSmoothPeekCamera(null, 10.0);
	public static final Camera peekCamera = new Camera(new Point.DoublePoint());
	public static boolean peekCameraMode;
	
	@Override
	public Camera starterCamera() {
		return playerCamera;
	}
	
	public String getVersion() {
		return "alpha1.0.0";
	}
	//save&load
	private SaveLoader saveLoader = new SaveLoaderV1_0();
	
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
		VK_SHIFT,
		VK_SPACE,
		VK_CONTROL,
		VK_Z,
		VK_ESCAPE,
		VK_F6,
		VK_O,
		VK_P,
		VK_TAB,
		VK_COMMA,
		VK_PERIOD,
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
		INTERACT(KeyEvent.VK_E, true),
		SWITCH_BATTLE_STANCE(KeyEvent.VK_TAB, true);
		
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
	static int mouseWheelRotation;
	static {
		for(GameInputEnum ver : GameInputEnum.values())
			gameInputs.addInput(ver.input());
	}
	
	//stages
	public static final int STAGE_W = 2500, STAGE_H = 2500;
	private final static GHQStage initialTestStage = new NAStage(STAGE_W, STAGE_H);
	private final static GHQStage[] stages = new NAStage[15];
	private static int nowStage;
	
	//GUIParts
	private static DefaultStageEditor editor;
	private static GUIParts escMenu;
	private static DoubleInventoryViewer inventoryInvester;
	private static UnitEditor unitEditor;
	private static Dialog dialog;
	private static QuickSlotViewer quickSlotViewer;
	
	//stage
	public static final TowerDefence towerDefence = new TowerDefence();
	
	//initialization
	@Override
	public String getTitleName() {
		return "NA";
	}
	public static void main(String args[]) {
		new GHQ(new NAGame(), 1080, 720);
	}
	public NAGame() {
		super(null);
	}
	@Override
	public final GHQStage loadStage() {
		//GHQ.setStage(initialTestStage);
		try(BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("../stage/NAStageData1.txt")))){ //ファイル読み込み開始
			br.readLine();
			stages[0] = NAStage.generate(this.saveLoader.load(br));
		}catch(Exception e) {
		}
		for(int i = 1;i < stages.length; ++i)
			stages[i] = new NAStage(STAGE_W, STAGE_H);
		return initialTestStage;
	}
	@Override
	public final void loadResource() {
		/////////////////////////////////
		//units
		/////////////////////////////////
		//friend
		GHQ.stage().addUnit(Unit.initialSpawn(setControllingUnit(new Player()), GHQ.screenW()/2, GHQ.screenH() - 100));
		//utility
		new ArmyBox().drop(500, 200);
		new LiquidBarrel(stage().makeLiquid(Water.FIXED_WATER_TAG, NALiquidState.WATER_SOLUABLE, 300)).drop(500, 500);
		new LiquidBarrel(stage().makeLiquid(Oil.FIXED_OIL_TAG, NALiquidState.OIL_SOLUABLE, 300)).drop(508, 500);
		new LiquidBarrel(stage().makeLiquid(new PoisonusWater(5), NALiquidState.WATER_SOLUABLE, 300)).drop(517, 500);
		//enemy
		GHQ.stage().addVegetation(new EnemyGate(() -> Unit.initialSpawn(new HumanGuard2(), 1200, 300), 1200, 300)).addAmount(1);
		//GHQ.stage().addUnit(Unit.initialSpawn(new HumanGuard(ENEMY), 400, GHQ.random2(100, 150)));
		/////////////////////////////////
		//vegetation
		/////////////////////////////////
		GHQ.stage().addVegetation(towerDefence.protectTarget = new DownStair()).point().setXY(700, 700);
		GHQ.stage().addVegetation(new AmmoArea(400, 700));
		AmmoType._9mm.generate(10).drop(822, 886);
		AmmoType._45acp.generate(10).drop(862, 896);
		AmmoType._7d62.generate(100).drop(812, 896);
		AmmoType._7d62.generate(100).addEnchant(new Scatter(), 1).addEnchant(new Penetration(1), 1).drop(812, 796);
		AmmoType._7d62.generate(29).addEnchant(new Poison(), 1).drop(852, 796);
		GHQ.stage().addStructure(new NATile(NATileEnum.WOOD, 100, 100, 1, 10));
		GHQ.stage().addStructure(new NATile(NATileEnum.WOOD, 125, 100, 9, 1));
		GHQ.stage().addStructure(new NATile(NATileEnum.WOOD, 125, 350, 9, 1));
		GHQ.stage().addStructure(new NATile(NATileEnum.WOOD, 350, 125, 1, 9));
		GHQ.stage().addItem(new Turret()).installToWall((NATile)stage().structures.get(2), 1);
		new Turret().drop(400, 400);
		new AmmoBox().drop(400, 450);
		new ShieldCharger(1000).drop(600, 800);
		new Type56().drop(702, 796);
		new LiquidGun().drop(652, 796);
		new Knife().drop(702, 836);
		new ElectronShield(500).drop(702, 796);
		new FireBallChip().drop(650, 800);
		new WaterSplashChip().drop(750, 800);
		//stageDataSaver.doLoad(new File("stage/saveData1.txt"));
		/////////////////////////////////
		//GUI
		/////////////////////////////////
		//ESC menu
		GHQ.addGUIParts(new GUIParts() {
			{
				setName("BuffIcons");
				setBounds(250, GHQ.screenH() - 100, 500, 50);
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
		GHQ.addGUIParts((quickSlotViewer = new QuickSlotViewer() {
			@Override
			public void idle() {
				super.idle();
				final Iterator<NAUsable> iterator = storage.iterator();
				while(iterator.hasNext()) {
					final NAUsable usable = iterator.next();
					if(usable instanceof ItemData && ((ItemData)usable).owner() != controllingUnit())
						iterator.remove();
				}
			}
		}).setCellSize(50).setTableStorage(controllingUnit.quickSlot())).enable().point().setXY(250, GHQ.screenH() - 50);
		GHQ.addGUIParts(escMenu = new EscMenu()).disable();
		GHQ.addGUIParts(inventoryInvester = new DoubleInventoryViewer()).disable();
		inventoryInvester.setLeftInventoryViewer((ItemStorageViewer)(new ItemStorageViewer().setCellPaint(ImageFrame.create("picture/gui/Bag_item.png"))));
		inventoryInvester.setRightInventoryViewer((ItemStorageViewer)(new ItemStorageViewer().setCellPaint(ImageFrame.create("picture/gui/Bag_item.png"))));
		GHQ.addGUIParts(editor = new DefaultStageEditor("EDITER_GROUP") {
			@Override
			public void saveStage() {
				//stageDataSaver.doSave(new File("stage/saveData1.txt"));
				//TODO: save & load event
				try(BufferedWriter bw = new BufferedWriter(new FileWriter("stage/saveData1.txt"))) {
					bw.write(saveLoader.save());
					bw.flush();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}).disable();
		editor.addFirst(unitEditor = new UnitEditor()).disable();

		///////////////
		//Stage
		///////////////
		//GHQ.addGUIParts(new StageViewer());
		GHQ.addGUIParts(new HUD());
		cornerNavi.defaultCornerCollect();
		cornerNavi.startCornerLink();
		/////////////////////////////////
		//liquids
		/////////////////////////////////
		//camera
		GHQ.setCamera(playerCamera);
	}
	//idle
	@Override
	public final void idle(Graphics2D g2, int stopEventKind) {
		if(controllingUnit == null)
			return;
		////////////
		//Stage
		////////////
		NAGame.stage().idle();
		towerDefence.idle();
		///////////////
		//Key test area
		///////////////
		if(stopEventKind == GHQ.NONE) {
			//changeStage
			if(s_keyL.hasEvent(VK_O)) {
				GHQ.setStage(stages[0]);
			}
			//returnInitialTestStage
			if(s_keyL.hasEvent(VK_P)) {
				GHQ.setStage(initialTestStage);
			}
			if(s_keyL.pullEvent(VK_TAB)) {
				stage().addLiquid(controllingUnit().point(), Water.FIXED_WATER_TAG, NALiquidState.WATER_SOLUABLE, 236);
			}
			if(s_keyL.pullEvent(VK_SHIFT)) {
				stage().addLiquid(controllingUnit().point(), Oil.FIXED_OIL_TAG, NALiquidState.OIL_SOLUABLE, 236);
			}
			if(s_keyL.pullEvent(VK_CONTROL)) {
				stage().addLiquid(controllingUnit().point(), new PoisonusWater(1), NALiquidState.WATER_SOLUABLE, 236);
			}
			if(s_keyL.pullEvent(VK_Z)) {
				stage().addLiquid(controllingUnit().point(), Flame.FIXED_FLAME_TAG, NALiquidState.GAS, 236);
			}
		}
		////////////
		//editor
		////////////
		if(s_keyL.pullEvent(VK_F6)) {
			editor.flip();
			if(editor.isEnabled())
				escMenu.disable();
		}
		if(editor.isEnabled()){ //editor GUI
			if(s_mouseL.pullButton3Event()) {
				unitEditor.tryOpen(GHQ.stage().units.forMouseOver());
			}
		}else {
			if(s_keyL.pullEvent(VK_ESCAPE)) {
				if(inventoryInvester.isEnabled()) {
					closeInventoryInvester();
				}else if(escMenu.isEnabled()) {
					escMenu.disable();
				}else {
					escMenu.enable();
				}
			}
			if(inventoryInvester.isEnabled() && gameInputs.consume("INTERACT")) {
				closeInventoryInvester();
			}
		}
		/////////////////////////////////
		//test
		/////////////////////////////////
		cornerNavi.setGoalPoint(towerDefence.protectTarget);
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
		mouseWheelRotation = e.getWheelRotation();
	}
	public static int pullMouseWheelRotation() {
		final int rotation = NAGame.mouseWheelRotation;
		NAGame.mouseWheelRotation = 0;
		return rotation;
	}
	//////////////
	//control
	//////////////
	public static NAUnit setControllingUnit(NAUnit unit) {
		NAGame.controllingUnit = unit;
		playerCamera.setChaseTarget(unit);
		return unit;
	}
	public static void openInventoryInvester(TableStorage<ItemData> storage) {
		inventoryInvester.enable();
		inventoryInvester.setLeftInventory(controllingUnit);
		inventoryInvester.setRightInventory(storage);
		lockControllingUnitAction(true);
	}
	public static void closeInventoryInvester() {
		inventoryInvester.disable();
		lockControllingUnitAction(false);
	}
	public static void lockControllingUnitAction(boolean b) {
		lockControllingUnitAction = b;
	}
	//stair
	public static void downFloor() {
		if(GHQ.stage() == initialTestStage)
			changeFloor(stages[nowStage = 0]);
		else {
			changeFloor(stages[++nowStage]);
		}
	}
	public static void upFloor() {
		if(GHQ.stage() == initialTestStage)
			changeFloor(stages[nowStage = stages.length - 1]);
		else {
			changeFloor(stages[--nowStage]);
		}
	}
	public static void changeFloor(GHQStage newStage) {
		final GHQStage currentStage = GHQ.stage();
		GHQ.setStage(newStage);
		//move player to new floor
		if(controllingUnit != null) {
			currentStage.units.remove(controllingUnit);
			newStage.units.add(controllingUnit);
		}
	}
	//information
	public static NAStage stage() {
		return (NAStage)GHQ.stage();
	}
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
	public static boolean controllingUnitActionLocked() {
		return lockControllingUnitAction;
	}
	public static boolean inventoryInvesterOpened() {
		return inventoryInvester.isEnabled();
	}
	public static Dialog dialog() {
		return dialog;
	}
	public static QuickSlotViewer quickSlotViewer() {
		return quickSlotViewer;
	}
}
