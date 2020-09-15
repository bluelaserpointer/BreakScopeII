package ui;

import static java.awt.event.KeyEvent.VK_COMMA;
import static java.awt.event.KeyEvent.VK_PERIOD;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import core.GHQ;
import engine.NAGame;
import gui.GUIParts;
import item.ammo.AmmoType;
import item.ammo.enchant.AmmoEnchants;
import item.ammo.storage.AmmoBag;
import item.equipment.Equipment;
import item.equipment.weapon.NASubWeapon;
import item.equipment.weapon.NAWeaponEquipment;
import item.equipment.weapon.reloadRule.ReloadRuleSelecter;
import item.equipment.weapon.NAFirearms;
import paint.ImageFrame;
import physics.HasBoundingBox;
import physics.HasPoint;
import physics.Point;
import physics.stage.GridBitSet;
import preset.item.ItemData;
import preset.structure.Structure;
import preset.structure.Tile;
import preset.structure.Tile.TileHitShape;
import preset.unit.Unit;
import stage.NAStage;
import structure.NATile;
import unit.NAUnit;
import weapon.Weapon;

public class HUD extends GUIParts {
	//images
	private final ImageFrame cilinderIF;
	private final ImageFrame focusIF;
	private final ImageFrame knifeFocusIF;
	private final ImageFrame bulletHeadIF;
	
	//data
	public static NATile installTargetTile;
	public static int installTargetTilePos;
	public static int installTargetX;
	public static int installTargetY;
	
	//fixed ui
	private final ZoomSliderBar zoomSliderBar;
	
	//non fixed ui
	private final ItemRCMenu_ground itemRCMenu;
	private final UnitInfo unitInfo;
	
	public HUD() {
		//non fixed ui
		addLast(itemRCMenu = new ItemRCMenu_ground()).disable(); //TODO: find out why this menu cannot automatically close when click on it
		addLast(unitInfo = new UnitInfo()).disable();
		//fixed ui
		cilinderIF = ImageFrame.create("picture/hud/cilinder.png");
		focusIF = ImageFrame.create("picture/hud/Focus.png");
		knifeFocusIF = ImageFrame.create("picture/hud/KnifeFocus.png");
		bulletHeadIF = ImageFrame.create("picture/hud/BulletHead.png");
		addLast(zoomSliderBar = new ZoomSliderBar()).setBounds(GHQ.screenW() - 200, 200, 200, 20);
		cloneDown(new GUIParts() {
			@Override
			public boolean clicked(MouseEvent e) {
				final boolean consumed = super.clicked(e);
				if(NAGame.towerDefence.phasePrepareMode)
					NAGame.towerDefence.nextPhase();
				return consumed;
			}
			@Override
			public void paint() {
				super.paint();
				if(NAGame.towerDefence.phasePrepareMode) {
					GHQ.getG2D(Color.WHITE).fillRect(left(), top(), width(), height());
					GHQ.getG2D(Color.BLACK);
					GHQ.drawStringGHQ("Next>", cx(), cy());
				} else {
					GHQ.getG2D(Color.GRAY).fillRect(left(), top(), width(), height());
					GHQ.getG2D(Color.BLACK);
					GHQ.drawStringGHQ("Next>", cx(), cy());
				}
			}
		});
	}
	private static final int MINIMAP_X = GHQ.screenW() - 200, MINIMAP_Y = 0, MINIMAP_SIZE = 100;
	private static final double MINIMAP_RATE = 1.0/25.0;
	private void miniMapDrawRect(int x, int y, int w, int h) {
		GHQ.getG2D().drawRect(MINIMAP_X + (int)(x*MINIMAP_RATE), MINIMAP_Y + (int)(y*MINIMAP_RATE), (int)(w*MINIMAP_RATE), (int)(h*MINIMAP_RATE));
	}
	private void miniMapDrawRect(HasBoundingBox object) {
		miniMapDrawRect(object.point().intX(), object.point().intY(), object.width(), object.height());
	}
	private void miniMapDrawRect(HasPoint object, int sizePixels) {
		GHQ.getG2D().drawRect(MINIMAP_X + (int)(object.point().intX()*MINIMAP_RATE), MINIMAP_Y + (int)(object.point().intY()*MINIMAP_RATE), sizePixels, sizePixels);
	}
	private void miniMapFillRect(int x, int y, int w, int h) {
		GHQ.getG2D().fillRect(MINIMAP_X + (int)(x*MINIMAP_RATE), MINIMAP_Y + (int)(y*MINIMAP_RATE), (int)(w*MINIMAP_RATE), (int)(h*MINIMAP_RATE));
	}
	private void miniMapFillRect(HasBoundingBox object) {
		miniMapFillRect(object.point().intX(), object.point().intY(), object.width(), object.height());
	}
	private void miniMapFillRect(HasPoint object, int sizePixels) {
		GHQ.getG2D().fillRect(MINIMAP_X + (int)(object.point().intX()*MINIMAP_RATE), MINIMAP_Y + (int)(object.point().intY()*MINIMAP_RATE), sizePixels, sizePixels);
	}
	private static final int MINIMAP_ALPHA = 180;
	@Override
	public void idle() {
		super.idle();
		//peek camera update
		if(NAGame.peekCameraMode) {
			final int x = (GHQ.mouseScreenX() - (GHQ.screenW() - 200))*GHQ.stage().width()/200;
			final int y = GHQ.mouseScreenY()*GHQ.stage().height()/200;
			NAGame.peekCamera.dstPoint().setXY(x, y);
		}
		//changeZoomRate
		if(NAGame.s_keyL.hasEvent(VK_COMMA)) {
			zoomSliderBar.setSliderValue(zoomSliderBar.sliderValue() - 0.015);
			NAGame.playerCamera.zoom = zoomSliderBar.sliderValue()*1.5 + 0.5;
		}else if(NAGame.s_keyL.hasEvent(VK_PERIOD)) {
			zoomSliderBar.setSliderValue(zoomSliderBar.sliderValue() + 0.015);
			NAGame.playerCamera.zoom = zoomSliderBar.sliderValue()*1.5 + 0.5;
		}
	}
	@Override
	public void paint() {
		super.paint();
		final Graphics2D g2 = GHQ.getG2D();
		final NAUnit player = NAGame.controllingUnit();
		final int alpha = NAGame.controllingUnit().isBattleStance() ? MINIMAP_ALPHA : 255;
		//Minimap
		GHQ.setClip(MINIMAP_X, MINIMAP_Y, MINIMAP_SIZE, MINIMAP_SIZE);
		//Minimap-background
		g2.setColor(new Color(Color.DARK_GRAY.getRed(), Color.DARK_GRAY.getGreen(), Color.DARK_GRAY.getBlue(), alpha));
		g2.fillRect(MINIMAP_X, MINIMAP_Y, MINIMAP_SIZE, MINIMAP_SIZE);
		//Minimap-seenMark
		g2.setColor(new Color(Color.GRAY.getRed(), Color.GRAY.getGreen(), Color.GRAY.getBlue(), alpha));
		GridBitSet seenMark = ((NAStage)GHQ.stage()).seenMark();
		final int minimapGridSize = (int)(NATile.TILE_SIZE*MINIMAP_RATE);
		for(int i = 0; i < seenMark.xGrids(); ++i) {
			for(int j = 0; j < seenMark.yGrids(); ++j) {
				if(seenMark.get_cellPos(i, j, false)) {
					g2.fillRect(MINIMAP_X + i*minimapGridSize, MINIMAP_Y + j*minimapGridSize, minimapGridSize, minimapGridSize);
				}
			}
		}
		//Minimap-structure
		g2.setColor(new Color(255, 255, 255, alpha));
		for(Structure structure : GHQ.stage().structures) {
			if(structure instanceof Tile) {
				miniMapFillRect(structure);
			}
		}
		//Minimap-camera
		g2.setColor(new Color(Color.CYAN.getRed(), Color.CYAN.getGreen(), Color.CYAN.getBlue(), alpha));
		g2.setStroke(GHQ.stroke1);
		miniMapDrawRect(GHQ.fieldScreenLeft(), GHQ.fieldScreenTop(), GHQ.fieldScreenW(), GHQ.fieldScreenH());
		//Minimap-player
		g2.setColor(Color.RED);
		for(Unit unit : GHQ.stage().units) {
			if(((NAUnit)unit).isHostile(NAGame.controllingUnit()) && ((NAUnit)unit).isVisibleByControllingUnit()) {
				miniMapFillRect(unit);
			}
		}
		//Minimap-player
		g2.setColor(Color.CYAN);
		miniMapFillRect(NAGame.controllingUnit(), 6);
		GHQ.setClip();
		//HP bars
		g2.setFont(GHQ.basicFont);
		g2.setColor(new Color(80, 80, 80));
		g2.fillRect(8, 3, 375, 70);
		g2.setColor(new Color(100, 100, 100));
		g2.fillRect(10, 5, 370, 65);
		g2.setStroke(new BasicStroke(3f));
		g2.setColor(new Color(200, 200, 200));
		g2.drawRect(10, 5, 370, 65);
		g2.setStroke(new BasicStroke(9f));
		int barLength;
		barLength = (int)(120*player.RED_BAR.getRate());
		int shieldBarLength = (int)(120*player.getShield()/(double)player.getShieldSize());
		if(barLength > 0) {
			if(shieldBarLength > 0) {
				g2.setColor(Color.CYAN);
				g2.drawLine(35, 15, 35 + shieldBarLength, 15);
				g2.setColor(Color.BLACK);
				g2.drawString("SH: " + player.getShield(), 285, 21);
				g2.setColor(Color.CYAN);
				g2.drawString("SH: " + player.getShield(), 285, 20);
				g2.setStroke(GHQ.stroke3);
				g2.setColor(Color.RED);
				g2.drawLine(35, 15, 35 + barLength, 15);
				g2.setStroke(new BasicStroke(9f));
			} else {
				g2.setColor(Color.RED);
				g2.drawLine(35, 15, 35 + barLength, 15);
			}
		}
		g2.setColor(Color.BLACK);
		g2.drawString("HP: " + GHQ.DF0_0.format(player.RED_BAR.doubleValue()), 181, 21);
		g2.setColor(Color.RED);
		g2.drawString("HP: " + GHQ.DF0_0.format(player.RED_BAR.doubleValue()), 180, 20);
		cilinderIF.dotPaint(90, 15);
		barLength = (int)(120*player.BLUE_BAR.getRate());
		if(barLength > 0) {
			g2.setColor(Color.CYAN);
			g2.drawLine(35, 30, 35 + barLength, 30);
		}
		g2.setColor(Color.BLACK);
		g2.drawString("MP: " + GHQ.DF0_0.format(player.BLUE_BAR.doubleValue()), 181, 36);
		g2.setColor(Color.CYAN);
		g2.drawString("MP: " + GHQ.DF0_0.format(player.BLUE_BAR.doubleValue()), 180, 35);
		cilinderIF.dotPaint(90, 30);
		barLength = (int)(120*player.GREEN_BAR.getRate());
		if(barLength > 0) {
			g2.setColor(Color.GREEN);
			g2.drawLine(35, 45, 35 + barLength, 45);
		}
		g2.setColor(Color.BLACK);
		g2.drawString("ST: " + GHQ.DF0_0.format(player.GREEN_BAR.doubleValue()), 181, 51);
		g2.setColor(Color.GREEN);
		g2.drawString("ST: " + GHQ.DF0_0.format(player.GREEN_BAR.doubleValue()), 180, 50);
		cilinderIF.dotPaint(90, 45);
		barLength = (int)(120*player.WHITE_BAR.getRate());
		if(barLength > 0) {
			g2.setColor(Color.WHITE);
			g2.drawLine(35, 60, 35 + barLength, 60);
		}
		g2.setColor(Color.BLACK);
		g2.drawString("FO: " + GHQ.DF0_0.format(player.WHITE_BAR.doubleValue()), 181, 66);
		g2.setColor(Color.WHITE);
		g2.drawString("FO: " + GHQ.DF0_0.format(player.WHITE_BAR.doubleValue()), 180, 65);
		cilinderIF.dotPaint(90, 60);
		//current weapon
		final NAWeaponEquipment weaponEquipment = NAGame.controllingUnit().currentEquipment();
		if(weaponEquipment != null) {
			g2.setColor(new Color(255, 255, 255, 100));
			g2.fillRect(0, 100, 100, 200);
//			g2.fillRect(0, 100, 100, 25);
//			g2.fillRect(0, 150, 100, 25);
			g2.setColor(Color.WHITE);
			//weapon name & image
			GHQ.drawStringGHQ(weaponEquipment.name(), 0, 125);
			weaponEquipment.getDotPaint().dotPaint_capSize(50, 175, 100);
			//ammo item image
			if(weaponEquipment instanceof NAFirearms) {
				final AmmoType usingAmmoType = ((NAFirearms)weaponEquipment).usingAmmoType();
				GHQ.drawStringGHQ(usingAmmoType.name(), 0, 250);
				((NAFirearms)weaponEquipment).currentReloadRule().getDotPaint().dotPaint(50, 300);
				usingAmmoType.paint.dotPaint(100, 300);
			}
		}
		//playerIcon
		int pos = 1;
		if(player.personalIcon != null)
			player.personalIcon.rectPaint(pos++*90 + 10, GHQ.screenH() - 40, 80, 30);
		//focus and magazine preview
		if(player.isBattleStance()) {
			final Equipment equipment = player.currentEquipment();
			final Weapon weapon = player.currentWeapon();
			final int mouseX = GHQ.mouseX(), mouseY = GHQ.mouseY();
			//block install focus
			GHQ.translateForGUI(false);
			final double rate = 50.0/player.point().distance(mouseX, mouseY);
			GHQ.getG2D(Color.RED).fillOval(player.cx() + (int)(player.point().intDX(mouseX)*rate) - 2, player.cy() + (int)(player.point().intDY(mouseY)*rate) - 2, 4, 4);
			NAGame.stage().seenMark().drawGrid_fieldCod(GHQ.getG2D(Color.ORANGE, GHQ.stroke3), mouseX, mouseY);
			//for must installed to ground
			installTargetX = mouseX / Tile.TILE_SIZE * Tile.TILE_SIZE + Tile.TILE_SIZE/2;
			installTargetY = mouseY / Tile.TILE_SIZE * Tile.TILE_SIZE + Tile.TILE_SIZE/2;
			//for must installed to wall
			Structure targetStructure = GHQ.stage().structures.forMouseOver();
			if(targetStructure instanceof NATile) {
				installTargetTile = (NATile)targetStructure;
				if(installTargetTile != null)
					installTargetTilePos = ((TileHitShape)installTargetTile.hitShape()).tilePos(mouseX, mouseY);
			}
			GHQ.translateForGUI(true);
			//weapon focus
			if(equipment instanceof NAFirearms) {
				//focus
				final NAFirearms firearm = (NAFirearms)equipment;
				GHQ.translateForGUI(false);
				g2.setColor(new Color(200,120,10,100));
				g2.setStroke(GHQ.stroke3);
				g2.drawLine(player.point().intX(), player.point().intY(), mouseX, mouseY);
				if(weapon.isReloadFinished())
					focusIF.dotPaint(mouseX, mouseY);
				else
					focusIF.dotPaint_turn(mouseX, mouseY, GHQ.nowFrame()/10);
				GHQ.translateForGUI(true);
				//magazine preview
				final int infoX = width() - 240, infoY = height() - 100, infoW = 230, infoH = 100;
				g2.setColor(Color.GRAY);
				g2.fillRect(infoX, infoY, infoW, infoH);
				final int magazineSize = weapon.magazineSize();
				final double bulletIconW = (double)infoW/magazineSize;
				g2.setStroke(GHQ.stroke1);
				int ammoPos = -1;
				for(AmmoEnchants enchants : firearm.magazineContents()) {
					++ammoPos;
					bulletHeadIF.rectPaint(infoX + (int)(bulletIconW*ammoPos) + 1, infoY, (int)bulletIconW - 2, 30);
					g2.setColor(enchants.enchantsColor());
					g2.fillRect(infoX + (int)(bulletIconW*ammoPos) + 1, infoY + 30, (int)bulletIconW - 2, 70);
					g2.setColor(Color.BLACK);
					g2.drawRect(infoX + (int)(bulletIconW*ammoPos) + 1, infoY + 30, (int)bulletIconW - 2, 70);
				}
				//reload gauge
				if(!weapon.isReloadFinished()) {
					g2.setColor(new Color(200, 200, 200, 100));
					g2.fillRect(infoX, infoY, (int)(infoW*(double)weapon.getReloadProgress()/weapon.reloadTime()), infoH);
				}
				//reload rule
				if(player.openReloadRule()) {
					final int bOvalSize = 60;
					final int sOvalSize = 45;
					final ReloadRuleSelecter selecter = player.reloadRuleSelecter();
					final int infoCX = infoX + infoW/2;
					GHQ.getG2D(Color.GRAY).fillOval(infoCX - bOvalSize/2, infoY - 40 - bOvalSize/2, bOvalSize, bOvalSize);
					selecter.getRule(0).getDotPaint().dotPaint(infoCX, infoY - 40);
					GHQ.getG2D(Color.GRAY).fillOval(infoCX - 50 - sOvalSize/2, infoY - 30 - sOvalSize/2, sOvalSize, sOvalSize);
					selecter.getRule(-1).getDotPaint().dotPaint(infoCX - 50, infoY - 30);
					GHQ.getG2D(Color.GRAY).fillOval(infoCX + 50 - sOvalSize/2, infoY - 30 - sOvalSize/2, sOvalSize, sOvalSize);
					selecter.getRule(+1).getDotPaint().dotPaint(infoCX + 50, infoY - 30);
					GHQ.getG2D(Color.GRAY).fillOval(infoCX - 90 - sOvalSize/2, infoY - 15 - sOvalSize/2, sOvalSize, sOvalSize);
					selecter.getRule(-2).getDotPaint().dotPaint(infoCX - 90, infoY - 15);
					GHQ.getG2D(Color.GRAY).fillOval(infoCX + 90 - sOvalSize/2, infoY - 15 - sOvalSize/2, sOvalSize, sOvalSize);
					selecter.getRule(+2).getDotPaint().dotPaint(infoCX + 90, infoY - 15);
				}
			} else if(equipment instanceof NASubWeapon) {
				knifeFocusIF.dotPaint_turn(GHQ.mouseScreenX(), GHQ.mouseScreenY(), player.point().angleTo(mouseX, mouseY));
			}
		}
	}
	@Override
	public boolean clicked(MouseEvent e) {
		final boolean consumed = super.clicked(e);

		if(e.getButton() == MouseEvent.BUTTON3) {
			final NAUnit unit = (NAUnit)GHQ.stage().units.forMouseOver();
			if(unit != null) {
				//TODO: open enemy right click menu
				//return true;
				unitInfo.setTargetUnit(unit);
				unitInfo.enable();
				return true;
			}
			ItemData item = GHQ.stage().items.forMouseOver();
			if(item != null) {
				itemRCMenu.tryOpen(item);
				return true;
			}
		} else if(e.getButton() == MouseEvent.BUTTON1) {
			if(!NAGame.controllingUnit().isBattleStance() && GHQ.mouseScreenX() > GHQ.screenW() - 200 && GHQ.mouseScreenY() < 200) { //minimap
				final int x = (GHQ.mouseScreenX() - (GHQ.screenW() - 200))*GHQ.stage().width()/200;
				final int y = GHQ.mouseScreenY()*GHQ.stage().height()/200;
				NAGame.peekCamera.dstPoint().setXY(x, y);
				if(!NAGame.peekCameraMode) {
					NAGame.peekCameraMode = true;
					GHQ.setCamera(NAGame.peekCamera);
				}
				return true;
			}
		}
		NAUnit.gameInputs().mousePressed(e);
		return consumed;
	}
	@Override
	public void released(MouseEvent e) {
		if(NAGame.peekCameraMode) {
			NAGame.peekCameraMode = false;
			GHQ.setCamera(NAGame.playerCamera);
			return;
		}
		NAUnit.gameInputs().mouseReleased(e);
	}
	//stage field always does not invoke swap operation.
	@Override
	public void dragIn(GUIParts sourceUI, Object dropObject) {
		final Point playerPoint = NAGame.controllingUnit().point();
		final double ANGLE = playerPoint.angleToMouse();
		final int x = (int)(playerPoint.doubleX() + 50*Math.cos(ANGLE));
		final int y = (int)(playerPoint.doubleY() + 50*Math.sin(ANGLE));
		if(dropObject instanceof AmmoBag) {
			((AmmoBag)dropObject).ammo().drop(x, y);
			((AmmoBag)dropObject).removeFromOrigin();
		}
		if(dropObject instanceof ItemData)
			((ItemData)dropObject).drop(x, y);
	}
	@Override
	public boolean checkDragIn(GUIParts sourceUI, Object dropObject) { //item throw
		//only check this is a item.
		return dropObject instanceof ItemData || dropObject instanceof AmmoBag;
	}
}
