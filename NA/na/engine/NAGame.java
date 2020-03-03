package engine;

import static java.awt.event.KeyEvent.*;

import java.awt.BasicStroke;
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
import item.ShieldCharger;
import item.ammo.Ammo_45acp;
import item.ammo.Ammo_9mm;
import item.magicChip.FireBallChip;
import paint.ImageFrame;
import physics.Route;
import saveLoader.SaveLoader;
import saveLoader.SaveLoaderV1_0;
import stage.GHQStage;
import stage.GridBitSet;
import stage.GridPainter;
import stage.NAStage;
import storage.Storage;
import ui.Dialog;
import ui.ESC_menu;
import ui.DoubleInventoryViewer;
import ui.QuickSlotViewer;
import ui.UnitEditor;
import ui.ZoomSliderBar;
import unit.HumanGuard2;
import unit.NAUnit;
import unit.Player;
import unit.ArmyBox;
import unit.Boss_1;
import unit.GameInput;
import unit.GameInputList;
import unit.Unit;
import vegetation.DownStair;
import vegetation.Vegetation;
import weapon.Type56;
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
		VK_TAB,
		VK_SHIFT,
		VK_SPACE,
		VK_ESCAPE,
		VK_F6,
		VK_O,
		VK_P,
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
	
	//stages
	public static final int STAGE_W = 15000, STAGE_H = 15000;
	private final static GHQStage initialTestStage = new NAStage(STAGE_W, STAGE_H);
	private final static GHQStage[] stages = new NAStage[15];
	private static int nowStage;
	//images
	
	//GUIParts
	private static DefaultStageEditor editor;
	private static GUIParts stageFieldGUI;
	private static GUIParts escMenu;
	private static DoubleInventoryViewer inventoryInvester;
	private static UnitEditor unitEditor;
	private static Dialog dialog;
	private static QuickSlotViewer quickSlotViewer;
	private static ZoomSliderBar zoomSliderBar;
	
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
		//GHQ.setStage(initialTestStage);
		try(BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("../stage/saveData1.txt")))){ //ファイル読み込み開始
			br.readLine();
			stages[0] = NAStage.generate(this.saveLoader.load(br));
		}catch(IOException e) {
		}
		for(int i = 1;i < stages.length; ++i)
			stages[i] = new NAStage(STAGE_W, STAGE_H);
		return initialTestStage;
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
		//utility
		GHQ.stage().addUnit(Unit.initialSpawn(new ArmyBox(), 500, 200));
		//enemy
		testUnit = 
		GHQ.stage().addUnit(Unit.initialSpawn(new Boss_1(), 1660, 1240));
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
		GHQ.stage().addVegetation(new DownStair()).point().setXY(100, 100);
		new Ammo_9mm(10).drop(822, 886);
		new Ammo_45acp(10).drop(862, 896);
		new ShieldCharger(1000).drop(600, 800);
		new Type56().drop(702, 796);
		new Knife().drop(702, 836);
		new ElectronShield(500).drop(702, 796);
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
		GHQ.addGUIParts((quickSlotViewer = new QuickSlotViewer()).setCellSize(50).setTableStorage(controllingUnit.quickSlot())).enable().point().setXY(250, 550);
		GHQ.addGUIParts(zoomSliderBar = new ZoomSliderBar() {
			@Override
			public void paint() {
				super.paint();
				GHQ.getG2D(Color.WHITE);
				GHQ.drawStringGHQ(GHQ.DF0_00.format(sliderValue*1.5 + 0.5), point().intX(), point().intY());
			}
		}).setBounds(880, 550, 210, 50);
		GHQ.addGUIParts(escMenu = new ESC_menu()).disable();
		GHQ.addGUIParts(inventoryInvester = new DoubleInventoryViewer()).disable();
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
		
		GHQ.addGUIParts(stageFieldGUI = new GUIParts() {
			{
				setName("stageFieldGUI");
			}
			private ImageFrame cilinderIF = ImageFrame.create("picture/hud/cilinder.png");
			@Override
			public void idle() {
				super.idle();
				///////////////
				//HUD
				///////////////
				final Graphics2D G2 = GHQ.getG2D();
				//HP bars
				G2.setFont(GHQ.basicFont);
				G2.setColor(new Color(80, 80, 80));
				G2.fillRect(8, 3, 375, 70);
				G2.setColor(new Color(100, 100, 100));
				G2.fillRect(10, 5, 370, 65);
				G2.setStroke(new BasicStroke(3f));
				G2.setColor(new Color(200, 200, 200));
				G2.drawRect(10, 5, 370, 65);
				G2.setStroke(new BasicStroke(9f));
				int barLength;
				barLength = (int)(120*controllingUnit.RED_BAR.getRate());
				int shieldBarLength = (int)(120*controllingUnit.getShield()/(double)controllingUnit.getShieldSize());
				if(barLength > 0) {
					if(shieldBarLength > 0) {
						G2.setColor(Color.CYAN);
						G2.drawLine(35, 15, 35 + shieldBarLength, 15);
						G2.setColor(Color.BLACK);
						G2.drawString("SH: " + controllingUnit.getShield(), 285, 21);
						G2.setColor(Color.CYAN);
						G2.drawString("SH: " + controllingUnit.getShield(), 285, 20);
						G2.setStroke(GHQ.stroke3);
						G2.setColor(Color.RED);
						G2.drawLine(35, 15, 35 + barLength, 15);
						G2.setStroke(new BasicStroke(9f));
					} else {
						G2.setColor(Color.RED);
						G2.drawLine(35, 15, 35 + barLength, 15);
					}
					G2.setColor(Color.BLACK);
					G2.drawString("HP: " + GHQ.DF0_0.format(controllingUnit.RED_BAR.doubleValue()), 181, 21);
					G2.setColor(Color.RED);
					G2.drawString("HP: " + GHQ.DF0_0.format(controllingUnit.RED_BAR.doubleValue()), 180, 20);
				}
				cilinderIF.dotPaint(90, 15);
				/*G2.setClip(90 - caseFluidIF.width()/2, 15 + caseFluidIF.height()/2 - shieldBarLength, caseFluidIF.width(), shieldBarLength);
				caseFluidIF.dotPaint(90, 15);
				G2.setClip(0, 0, GHQ.screenW(), GHQ.screenH());
				caseIF.dotPaint(90, 15);*/
				//G2.drawRect(90 - caseFluidIF.width()/2, 15 + caseFluidIF.height()/2 - shieldBarLength, caseFluidIF.width(), shieldBarLength);
				barLength = (int)(120*controllingUnit.BLUE_BAR.getRate());
				if(barLength > 0) {
					G2.setColor(Color.CYAN);
					G2.drawLine(35, 30, 35 + barLength, 30);
					G2.setColor(Color.BLACK);
					G2.drawString("MP: " + GHQ.DF0_0.format(controllingUnit.BLUE_BAR.doubleValue()), 181, 36);
					G2.setColor(Color.CYAN);
					G2.drawString("MP: " + GHQ.DF0_0.format(controllingUnit.BLUE_BAR.doubleValue()), 180, 35);
				}
				cilinderIF.dotPaint(90, 30);
				barLength = (int)(120*controllingUnit.GREEN_BAR.getRate());
				if(barLength > 0) {
					G2.setColor(Color.GREEN);
					G2.drawLine(35, 45, 35 + barLength, 45);
					G2.setColor(Color.BLACK);
					G2.drawString("ST: " + GHQ.DF0_0.format(controllingUnit.GREEN_BAR.doubleValue()), 181, 51);
					G2.setColor(Color.GREEN);
					G2.drawString("ST: " + GHQ.DF0_0.format(controllingUnit.GREEN_BAR.doubleValue()), 180, 50);
				}
				cilinderIF.dotPaint(90, 45);
				barLength = (int)(120*controllingUnit.ENERGY.getRate());
				if(barLength > 0) {
					G2.setColor(Color.WHITE);
					G2.drawLine(35, 60, 35 + barLength, 60);
					G2.setColor(Color.BLACK);
					G2.drawString("FO: " + GHQ.DF0_0.format(controllingUnit.ENERGY.doubleValue()), 181, 66);
					G2.setColor(Color.WHITE);
					G2.drawString("FO: " + GHQ.DF0_0.format(controllingUnit.ENERGY.doubleValue()), 180, 65);
				}
				cilinderIF.dotPaint(90, 60);
				//playerIcon
				int pos = 1;
				if(controllingUnit.personalIcon != null)
					controllingUnit.personalIcon.rectPaint(pos++*90 + 10, GHQ.screenH() - 40, 80, 30);
				//zoomSliderBar
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
		//for(Unit unit : GHQ.stage().units) {
			//unit.angle().set(0.8);
		//}
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
	private ImageFrame tileIF = ImageFrame.create("picture/map/Tile.png");
	@Override
	public final void idle(Graphics2D g2, int stopEventKind) {
		if(controllingUnit == null || stageFieldGUI == null)
			return;
		gameFrame++;
		final int MOUSE_X = GHQ.mouseX(), MOUSE_Y = GHQ.mouseY();
		//////////////////////////
		//idle
		//////////////////////////
		//
		//background
		final int TILE_SIZE = 50;
		final int startX = Math.max(GHQ.getScreenLeftX_stageCod()/TILE_SIZE - 2, 0);
		final int startY = Math.max(GHQ.getScreenTopY_stageCod()/TILE_SIZE - 2, 0);
		final int endX = startX + GHQ.getScreenW_stageCod()/TILE_SIZE + 4;
		final int endY = startY + GHQ.getScreenH_stageCod()/TILE_SIZE + 4;
		for(int xi = startX;xi < endX;xi++) {
			for(int yi = startY;yi < endY;yi++) {
				tileIF.dotPaint(xi*TILE_SIZE + 50, yi*TILE_SIZE + 50);
				//tileIF.rectPaint(xi*TILE_SIZE, yi*TILE_SIZE, TILE_SIZE);
			}
		}
		final NAStage nowStage = (NAStage)GHQ.stage();
		//sight marking and fog system
		GridBitSet enemySeenMark = nowStage.enemySeenMark();
		GridBitSet playerSeenMark = nowStage.playerSeenMark();
		GridPainter gridPainter = nowStage.gridPainter();
		////////////
		//enlightVisibleArea
		////////////
		enemySeenMark.clear();
		playerSeenMark.clear();
		for(Unit ver : GHQ.stage().units) {
			NAUnit unit = (NAUnit)ver;
			if(unit.isControllingUnit() || controllingUnit().isVisible(unit)) {
				final int xStart = gridPainter.screenLeftXPos(), yStart = gridPainter.screenTopYPos();
				final int xEnd = xStart + gridPainter.gridsToFillScreenWidth(), yEnd = yStart + gridPainter.gridsToFillScreenHeight();
				for(int xPos = xStart;xPos < xEnd;++xPos) {
					for(int yPos = yStart;yPos < yEnd;++yPos) {
						if(unit.isControllingUnit()) {
							if(unit.isVisible(gridPainter.getPosPoint(xPos, yPos))) {
								nowStage.seenMark().set_cellPos(xPos, yPos);
								nowStage.playerSeenMark().set_cellPos(xPos, yPos);
							}
						}else if(unit.isHostile(controllingUnit) && nowStage.seenMark().get_cellPos(xPos, yPos, false) && unit.isVisible(gridPainter.getPosPoint(xPos, yPos))) {
							nowStage.enemySeenMark().set_cellPos(xPos, yPos);
						}
					}
				}
			}
		}
		//objects idle
		GHQ.stage().idle(GHQObjectType.VEGETATION);
		GHQ.stage().idle(GHQObjectType.ITEM);
		GHQ.stage().idle(GHQObjectType.STRUCTURE);
		GHQ.stage().idle(GHQObjectType.UNIT);
		GHQ.stage().idle(GHQObjectType.BULLET);
		GHQ.stage().idle(GHQObjectType.EFFECT);
		//notSeenPlaceFog
		GridBitSet seenMark = nowStage.seenMark();
		final int xStart = gridPainter.screenLeftXPos(), yStart = gridPainter.screenTopYPos();
		final int xEnd = xStart + gridPainter.gridsToFillScreenWidth(), yEnd = yStart + gridPainter.gridsToFillScreenHeight();
		final Color enemySeenColor = new Color(1F, 0F, 0F, 0.5F);
		final Color playerNotSeenColor = new Color(0F, 0F, 0F, 0.5F);
		final Color combinedColor = new Color(170, 0, 0, 128);
		for(int xPos = xStart;xPos < xEnd;++xPos) {
			for(int yPos = yStart;yPos < yEnd;++yPos) {
				if(seenMark.get_cellPos(xPos, yPos, false)) {
					final boolean isGrayMarked = !playerSeenMark.get_cellPos(xPos, yPos, false);
					final boolean isRedMarked = enemySeenMark.get_cellPos(xPos, yPos, false);
					if(isRedMarked && isGrayMarked)
						gridPainter.fillGrid(GHQ.getG2D(combinedColor), xPos, yPos);
					else if(isRedMarked && !isGrayMarked) //fill transparent red area that in an enemy's sight
						gridPainter.fillGrid(GHQ.getG2D(enemySeenColor), xPos, yPos);
					else if(!isRedMarked && isGrayMarked) //fill transparent gray area that in the player's sight
						gridPainter.fillGrid(GHQ.getG2D(playerNotSeenColor), xPos, yPos);
				}else { //fill black area that never seen before
					if((xPos - yPos) % 2 == 0)
						gridPainter.fillGrid(GHQ.getG2D(Color.BLACK), xPos, yPos);
					else
						gridPainter.fillGrid(GHQ.getG2D(new Color(0.1F, 0.1F, 0.1F)), xPos, yPos);
					//final int size = NAStage.SEEN_CELL_SIZE;
					//GHQ.getG2D(Color.GRAY);
					//GHQ.drawStringGHQ("?", xPos*size, yPos*size);
					//GHQ.getG2D(Color.LIGHT_GRAY, GHQ.stroke1).drawLine(xPos*size, yPos*size, xPos*size + size, yPos*size + size);
				}
			}
		}
		///////////////
		//key test area
		///////////////
		if(stopEventKind == GHQ.NONE) {
			//warp
			if(s_keyL.hasEvent(VK_TAB)) {
				controllingUnit.point().setXY(MOUSE_X, MOUSE_Y);
			}
			//changeZoomRate
			if(s_keyL.hasEvent(VK_COMMA)) {
				zoomSliderBar.setSliderValue(zoomSliderBar.sliderValue() - 0.015);
				GHQ.setStageZoomRate(zoomSliderBar.sliderValue()*1.5 + 0.5);
			}else if(s_keyL.hasEvent(VK_PERIOD)) {
				zoomSliderBar.setSliderValue(zoomSliderBar.sliderValue() + 0.015);
				GHQ.setStageZoomRate(zoomSliderBar.sliderValue()*1.5 + 0.5);
			}
			//changeStage
			if(s_keyL.hasEvent(VK_O)) {
				GHQ.setStage(stages[0]);
			}
			//returnInitialTestStage
			if(s_keyL.hasEvent(VK_P)) {
				GHQ.setStage(initialTestStage);
			}
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
				GHQ.viewTargetTo((MOUSE_X + controllingUnit.point().intX())/2, (MOUSE_Y + controllingUnit.point().intY())/2);
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
				controllingUnit.body().changeToNextWeapon();
			else
				controllingUnit.body().changeToPrevWeapon();
		}
	}
	//////////////
	//control
	//////////////
	public static void openInventoryInvester(Storage<ItemData> storage) {
		inventoryInvester.enable();
	}
	public static void closeInventoryInvester() {
		inventoryInvester.disable();
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
