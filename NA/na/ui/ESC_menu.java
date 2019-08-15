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
import gui.TitledLabel;
import item.ItemData;
import paint.ImageFrame;
import storage.TableStorage;

public class ESC_menu extends GUIPartsSwitcher{
	private static TitledLabel mainWeaponLabel, subWeaponLabel, meleeWeaponLabel;
	private static final int INVENTORY = 0, PARA = 1, CRAFT = 2, SYSTEM = 3, MEMO = 4;
	public ESC_menu() {
		super(5, INVENTORY);
		//INVENTORY
		set(INVENTORY, new GUIParts() {
			{
				setName("INVENTORY");
				addLast(mainWeaponLabel = new TitledLabel("mainWeapon")).setName("mainWeaponLabel").setBGColor(Color.WHITE).setBounds(500, 70, 400, 40);
				addLast(subWeaponLabel = new TitledLabel("subWeapon")).setName("subWeaponLabel").setBGColor(Color.WHITE).setBounds(500, 140, 400, 40);
				addLast(meleeWeaponLabel = new TitledLabel("meleeWeapon")).setName("meleeWeaponLabel").setBGColor(Color.WHITE).setBounds(500, 210, 400, 40);
			}
			@Override
			public void idle() {
				super.idle();
				//weaponName update
				mainWeaponLabel.setText(Engine_NA.getPlayer().mainSlot.getName());
				subWeaponLabel.setText(Engine_NA.getPlayer().subSlot.getName());
				meleeWeaponLabel.setText(Engine_NA.getPlayer().meleeSlot.getName());
			}
		})
		.appendLast(new AutoResizeMenu(0, 0, GHQ.screenW(), 70) {
			final GUIParts 
				inventoryScrBtn = getSwitcherButton(INVENTORY),
				paraScrBtn = getSwitcherButton(PARA),
				craftScrBtn = getSwitcherButton(CRAFT),
				sysScrBtn = getSwitcherButton(SYSTEM),
				memoScrBtn = getSwitcherButton(MEMO);
			{
				this.addNewLine(paraScrBtn, inventoryScrBtn, craftScrBtn, sysScrBtn, memoScrBtn);
			}
		})
		.appendLast(
				new InventorySystem(
					new InventoryViewer(ImageFrame.create("picture/gui/slot.png"), 50, 70, 70, (TableStorage<ItemData>)Engine_NA.getPlayer().inventory.items) {
						@Override
						public void outsideReleased() {
							super.outsideReleased();
							final ItemData hookingObject = itemMouseHook.get();
							if(hookingObject != null) {
								Engine_NA.getPlayer().removedItem(hookingObject);
								final double ANGLE = Engine_NA.getPlayer().dynam.angleToMouse();
								hookingObject.drop((int)(Engine_NA.getPlayer().dynam.doubleX() + 50*Math.cos(ANGLE)), (int)(Engine_NA.getPlayer().dynam.doubleY() + 50*Math.sin(ANGLE)));
							}
							itemMouseHook.hook(ItemData.BLANK_ITEM);
						}
					},
					new ItemRCMenu()
					)
			);
		//PARA
		set(PARA, new GUIParts() {
			@Override
			public void idle() {
				super.idle();
				final int xs = 200, ys = 400, d = 25;
				final double d1 = Engine_NA.getPlayer().POW_FIXED.doubleValue(),
						d2 = Engine_NA.getPlayer().AGI_FIXED.doubleValue(),
						d3 = Engine_NA.getPlayer().INT_FIXED.doubleValue();
				final int x1, y1, x2, y2, x3, y3;
				x1 = 0;
				y1 = (int)-d1*d;
				x2 = (int)(-d2/1.4*d);
				y2 = (int)(+d2/1.4*d);
				x3 = (int)(+d3/1.4*d);
				y3 = (int)(+d3/1.4*d);
				final Graphics2D G2 = GHQ.getGraphics2D(Color.GRAY);
				G2.drawPolygon(new int[] {xs + x1, xs + x2, xs + x3}, new int[] {ys + y1, ys + y2, ys + y3}, 3);
				G2.setColor(Color.WHITE);
				G2.fillPolygon(new int[] {xs + x1, xs + x2, xs + x3}, new int[] {ys + y1, ys + y2, ys + y3}, 3);
			}
		});
	}
}
