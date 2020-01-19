package ui;

import java.awt.Color;
import java.awt.Graphics2D;

import core.GHQ;
import engine.Engine_NA;
import gui.AutoResizeMenu;
import gui.GUIParts;
import gui.GUIPartsSwitcher;
import gui.InventorySystem;
import gui.InventoryViewer;
import gui.TextButton;
import gui.TitledLabel;
import item.NAItem;
import item.ItemData;
import item.weapon.Equipment;
import item.weapon.MainSlot;
import item.weapon.MelleSlot;
import item.weapon.SubSlot;
import paint.ColorFraming;
import paint.ImageFrame;
import storage.TableStorage;

public class ESC_menu extends GUIPartsSwitcher{
	private static TitledLabel mainWeaponLabel, subWeaponLabel, meleeWeaponLabel;
	private static final int INVENTORY = 0, PARA = 1, CRAFT = 2, SYSTEM = 3, MEMO = 4, KEY_SHORTCUT = 5;
	public ESC_menu() {
		super(6, INVENTORY);
		//top tab
		this.appendLast(new AutoResizeMenu(0, 0, GHQ.screenW(), 70) {
			final GUIParts 
				inventoryScrBtn = getSwitcherButton(INVENTORY).setBGPaint(new ColorFraming(Color.GRAY, GHQ.stroke3)).setName("inventoryScrBtn"),
				paraScrBtn = getSwitcherButton(PARA).setBGPaint(new ColorFraming(Color.GRAY, GHQ.stroke3)).setName("paraScrBtn"),
				craftScrBtn = getSwitcherButton(CRAFT).setBGPaint(new ColorFraming(Color.GRAY, GHQ.stroke3)).setName("craftScrBtn"),
				sysScrBtn = getSwitcherButton(SYSTEM).setBGPaint(new ColorFraming(Color.GRAY, GHQ.stroke3)).setName("sysScrBtn"),
				memoScrBtn = getSwitcherButton(MEMO).setBGPaint(new ColorFraming(Color.GRAY, GHQ.stroke3)).setName("memoScrBtn"),
				keyShortCutBtn = getSwitcherButton(KEY_SHORTCUT).setBGPaint(new ColorFraming(Color.GRAY, GHQ.stroke3)).setName("keyShortCutBtn");
			{
				this.addNewLine(paraScrBtn, inventoryScrBtn, craftScrBtn, sysScrBtn, memoScrBtn, keyShortCutBtn);
				this.setBGColor(Color.WHITE);
				this.setName("ESC_MENU_TOP_TAB");
			}
		});
		//INVENTORY
		set(INVENTORY, new GUIParts() {
			{
				setName("INVENTORY");
				addLast(new InventorySystem(
						new InventoryViewer(ImageFrame.create("picture/gui/slot.png"), 50, 100, 70, (TableStorage<ItemData>)Engine_NA.player().inventory.items),
						new ItemRCMenu()
						)
				);
				addLast(mainWeaponLabel = new TitledLabel("mainWeapon") {
					@Override
					public void clicked() {
						if(Engine_NA.player().hasMainEquip())
							GHQ.mouseHook.hook(Engine_NA.player().mainEquip(), this);
					}
					@Override
					public boolean doLinkDrag() {
						return true;
					}
					@Override
					public boolean checkDragIn(GUIParts sourceUI, Object dropObject) {
						return dropObject instanceof NAItem && dropObject instanceof MainSlot;
					}
					@Override
					public void dragIn(GUIParts sourceUI, Object dropObject) {
						((Equipment)dropObject).equipToOwner();
					}
					@Override
					public Object peekDragObject() {
						return Engine_NA.player().mainEquip();
					}
					@Override
					public boolean checkDragOut(GUIParts targetUI, Object dropObject) {
						return true;
					}
					@Override
					public void dragOut(GUIParts sourceUI, Object dropObject, Object swapObject) {
						if(swapObject == null)
							((Equipment)dropObject).dequipFromOwner();
						else
							((Equipment)swapObject).equipToOwner();
					}
				}).setName("mainWeaponLabel").setBGColor(Color.WHITE).setBounds(550, 130, 400, 40);
				addLast(subWeaponLabel = new TitledLabel("subWeapon") {
					@Override
					public void clicked() {
						if(Engine_NA.player().hasSubEquip())
							GHQ.mouseHook.hook(Engine_NA.player().subEquip(), this);
					}
					@Override
					public boolean doLinkDrag() {
						return true;
					}
					@Override
					public boolean checkDragIn(GUIParts sourceUI, Object dropObject) {
						return dropObject instanceof Equipment && dropObject instanceof SubSlot;
					}
					@Override
					public void dragIn(GUIParts sourceUI, Object dropObject) {
						((Equipment)dropObject).equipToOwner();
					}
					@Override
					public Object peekDragObject() {
						return Engine_NA.player().subEquip();
					}
					@Override
					public boolean checkDragOut(GUIParts targetUI, Object dropObject) {
						return true;
					}
					@Override
					public void dragOut(GUIParts sourceUI, Object dropObject, Object swapObject) {
						if(swapObject == null)
							((Equipment)dropObject).dequipFromOwner();
						else
							((Equipment)swapObject).equipToOwner();
					}
				}).setName("subWeaponLabel").setBGColor(Color.WHITE).setBounds(550, 200, 400, 40);
				addLast(meleeWeaponLabel = new TitledLabel("meleeWeapon") {
					@Override
					public void clicked() {
						if(Engine_NA.player().hasMelleEquip())
							GHQ.mouseHook.hook(Engine_NA.player().melleEquip(), this);
					}
					@Override
					public boolean doLinkDrag() {
						return true;
					}
					@Override
					public boolean checkDragIn(GUIParts sourceUI, Object dropObject) {
						return dropObject instanceof NAItem && dropObject instanceof MelleSlot;
					}
					@Override
					public void dragIn(GUIParts sourceUI, Object dropObject) {
						((Equipment)dropObject).equipToOwner();
					}
					@Override
					public Object peekDragObject() {
						return Engine_NA.player().melleEquip();
					}
					@Override
					public boolean checkDragOut(GUIParts targetUI, Object dropObject) {
						return true;
					}
					@Override
					public void dragOut(GUIParts sourceUI, Object dropObject, Object swapObject) {
						if(swapObject == null)
							((Equipment)dropObject).dequipFromOwner();
						else
							((Equipment)swapObject).equipToOwner();
					}
				}).setName("meleeWeaponLabel").setBGColor(Color.WHITE).setBounds(550, 270, 400, 40);
			}
			@Override
			public void idle() {
				//framing
				final Graphics2D G2 = GHQ.getG2D(Color.GRAY, GHQ.stroke3);
				G2.drawRect(30, 80, 400, 450);
				G2.drawRect(520, 80, 450, 450);
				//weaponName update
				mainWeaponLabel.setText(Engine_NA.player().mainEquip().name());
				subWeaponLabel.setText(Engine_NA.player().subEquip().name());
				meleeWeaponLabel.setText(Engine_NA.player().melleEquip().name());
				//item description
				G2.setColor(Color.GRAY);
				G2.setStroke(GHQ.stroke3);
				G2.drawRect(50, 325, 350, 180);
				G2.setColor(Color.WHITE);
				G2.fillRect(50, 325, 350, 180);
				super.idle();
			}
		});
		//PARA
		set(PARA, new GUIParts() {
			@Override
			public void idle() {
				super.idle();
				//BG
				GHQ.getG2D(Color.GRAY, GHQ.stroke3).drawRect(30, 80, 400, 450);
				GHQ.getG2D(Color.GRAY, GHQ.stroke3).drawRect(520, 80, 450, 450);
				//triangle
				final int xs = 200, ys = 400, d = 25;
				final double d1 = Engine_NA.player().POW_FIXED.doubleValue(),
						d2 = Engine_NA.player().AGI_FIXED.doubleValue(),
						d3 = Engine_NA.player().INT_FIXED.doubleValue();
				int x1, y1, x2, y2, x3, y3;
				x1 = 0;
				y1 = (int)(-d1*d);
				x2 = (int)(-d2*0.86*d);
				y2 = (int)(+d2*0.5*d);
				x3 = (int)(+d3*0.86*d);
				y3 = (int)(+d3*0.5*d);
				final Graphics2D G2 = GHQ.getG2D(Color.BLACK, GHQ.stroke7);
				G2.drawPolygon(new int[] {xs + x1, xs + x2, xs + x3}, new int[] {ys + y1, ys + y2, ys + y3}, 3);
				G2.setColor(Color.WHITE);
				G2.fillPolygon(new int[] {xs + x1, xs + x2, xs + x3}, new int[] {ys + y1, ys + y2, ys + y3}, 3);
				//ruler
				G2.setColor(new Color(50, 50, 50, 100));
				G2.setStroke(GHQ.stroke5);
				for(int i = 1;i <= 5;++i) {
					x1 = 0;
					y1 = (int)(-i*d);
					x2 = (int)(-i*0.86*d);
					y2 = (int)(+i*0.5*d);
					x3 = (int)(+i*0.86*d);
					y3 = (int)(+i*0.5*d);
					G2.drawPolygon(new int[] {xs + x1, xs + x2, xs + x3}, new int[] {ys + y1, ys + y2, ys + y3}, 3);
				}
				//ruler2
				G2.setStroke(GHQ.stroke1);
				G2.setColor(Color.RED);
				G2.drawLine(xs, ys, xs, ys - 5*d);
				G2.setColor(Color.GREEN);
				G2.drawLine(xs, ys, xs - (int)(5*0.86*d), ys + (int)(5*0.5*d));
				G2.setColor(Color.BLUE);
				G2.drawLine(xs, ys, xs + (int)(5*0.86*d), ys + (int)(5*0.5*d));
				//outer ruler
				G2.setStroke(GHQ.stroke1);
				G2.setColor(Color.GRAY);
				G2.drawOval(xs - d*5, ys - d*5, d*10, d*10);
				//status text preview
				G2.setFont(GHQ.initialFont.deriveFont(22F));
				G2.setColor(Color.RED);
				G2.drawString("力量: " + Engine_NA.player().POW_FLOAT.intValue() + "(" + Engine_NA.player().POW_FIXED.intValue() + ")", 545, 115);
				G2.setColor(Color.BLUE);
				G2.drawString("智力: " + Engine_NA.player().INT_FLOAT.intValue() + "(" + Engine_NA.player().INT_FIXED.intValue() + ")", 545, 140);
				G2.setColor(Color.GREEN);
				G2.drawString("敏捷: " + Engine_NA.player().AGI_FLOAT.intValue() + "(" + Engine_NA.player().AGI_FIXED.intValue() + ")", 545, 165);
				
				G2.setColor(Color.RED);
				G2.drawString("HP: " + Engine_NA.player().RED_BAR.intValue() + "/" + Engine_NA.player().RED_BAR.max().intValue(), 545, 200);
				G2.setColor(Color.BLUE);
				G2.drawString("MP: " + Engine_NA.player().BLUE_BAR.intValue() + "/" + Engine_NA.player().BLUE_BAR.max().intValue(), 545, 225);
				G2.setColor(Color.GREEN);
				G2.drawString("STAMINA: " + Engine_NA.player().GREEN_BAR.intValue() + "/" + Engine_NA.player().GREEN_BAR.max().intValue(), 545, 250);
				G2.setColor(Color.WHITE);
				G2.drawString("FOOD: " + Engine_NA.player().ENERGY.intValue() + "/" + Engine_NA.player().ENERGY.max().intValue(), 545, 275);
			
				G2.setColor(Color.WHITE);
				G2.drawString("SPD: " + Engine_NA.player().SPEED_PPS.intValue(), 545, 310);
				G2.drawString("SENSE: " + Engine_NA.player().SENSE.intValue(), 545, 335);
				G2.drawString("TOUGHNESS: " + Engine_NA.player().TOUGHNESS.intValue(), 545, 360);
				G2.drawString("TOUGHNESS_REG: " + Engine_NA.player().TOUGHNESS_REG.intValue(), 545, 385);
				G2.drawString("CRI: " + Engine_NA.player().CRI.doubleValue(), 545, 410);
				G2.drawString("AVD: " + Engine_NA.player().AVD.doubleValue(), 545, 435);
				G2.drawString("REF: " + Engine_NA.player().REF.doubleValue(), 545, 460);
				G2.drawString("SUCK: " + Engine_NA.player().SUCK.doubleValue(), 545, 485);
			}
		});
		//Craft
		set(CRAFT, new GUIParts() {
			@Override
			public void paint() {
				super.paint();
				GHQ.getG2D(Color.GRAY).drawRect(190, 140, 200, 370);
				GHQ.getG2D(Color.GRAY).drawRect(440, 140, 200, 200);
				GHQ.getG2D(Color.GRAY).drawRect(730, 140, 200, 200);
			}
		});
		//System
		set(SYSTEM, new GUIParts() {
			{
				
			}
		});
		//Memo
		set(MEMO, new GUIParts() {
			{
				super.addLast(new AutoResizeMenu(30, 80, 400, 60, 10))
					.addNewLine(new TextButton("ENEMY CATALOG", Color.WHITE))
					.addNewLine(new TextButton("EQUIPMENT CATALOG", Color.WHITE))
					.addNewLine(new TextButton("MATERIAL CATALOG", Color.WHITE))
					.addNewLine(new TextButton("TALKING RECORD", Color.WHITE))
					.addNewLine(new TextButton("BLUEPRINT AND RECEIPE", Color.WHITE))
					;
			}
			@Override
			public void paint() {
				super.paint();
				GHQ.getG2D(Color.GRAY, GHQ.stroke3).drawRect(30, 80, 400, 450);
				GHQ.getG2D(Color.GRAY, GHQ.stroke3).drawRect(520, 80, 450, 450);
			}
		});
		//KEY_SHORTCUT
		set(KEY_SHORTCUT, new GUIParts() {
			{
			}
		});
	}
	@Override
	public void idle() {
		super.idle();
		GHQ.getG2D(Color.GRAY);
		GHQ.drawStringGHQ("Parameter", (int)(GHQ.screenW()*0.2*0) + 15, 30, GHQ.basicFont.deriveFont(40F));
		GHQ.drawStringGHQ("Inventory", (int)(GHQ.screenW()*0.2*1) + 15, 30, GHQ.basicFont.deriveFont(40F));
		GHQ.drawStringGHQ("Craft", (int)(GHQ.screenW()*0.2*2) + 15, 30, GHQ.basicFont.deriveFont(40F));
		GHQ.drawStringGHQ("System", (int)(GHQ.screenW()*0.2*3) + 15, 30, GHQ.basicFont.deriveFont(40F));
		GHQ.drawStringGHQ("Memo", (int)(GHQ.screenW()*0.2*4) + 15, 30, GHQ.basicFont.deriveFont(40F));
	}
}
