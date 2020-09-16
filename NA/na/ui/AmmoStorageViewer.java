package ui;

import java.awt.Color;

import core.GHQ;
import gui.AutoResizeMenu;
import gui.TableStorageViewer;
import item.ammo.AmmoType;
import item.ammo.storage.AmmoBag;
import item.ammo.storage.AmmoStorage;
import paint.ColorFraming;

public class AmmoStorageViewer extends TableStorageViewer<AmmoBag> {
	protected AutoResizeMenu ammoEnchantsMenu;
	{
		this.cellPaint = new ColorFraming(Color.GRAY, GHQ.stroke1);
		//addLast(ammoEnchantsMenu = new AutoResizeMenu(300, 20)).disable();
	}
	private AmmoStorage ammoStorage;
	
	//init
	public AmmoStorageViewer() {
		super(AmmoBag.class);
	}
	public AmmoStorageViewer setAmmoType(AmmoType ammoType) {
		super.setStorage(ammoStorage().ammoBagList(ammoType));
		return this;
	}
	
	//event
	@Override
	public void mouseOver() {
		final AmmoBag ammoBag = this.getMouseHoveredElement();
		if(ammoBag != null) {
//			ammoEnchantsMenu.enable();
//			ammoEnchantsMenu.removeAll();
//			final HashMap<AmmoEnchant, Integer> enchantLv = ammoBag.enchants().enchants();
//			for(AmmoEnchant enchant : enchantLv.keySet()) {
//				ammoEnchantsMenu.addNewLine(new GUIParts() {
//					@Override
//					public void paint() {
//						super.paint();
//						GHQ.getG2D(Color.GRAY);
//						GHQ.drawStringGHQ(enchant.name, point().intX(), point().intY());
//					}
//				}).setBGColor(Color.LIGHT_GRAY);
//			}
		}
	}
	@Override
	public void mouseOut() {
		super.mouseOut();
//		ammoEnchantsMenu.disable();
	}
	
	//info
	public AmmoStorage ammoStorage() {
		return ammoStorage;
	}
}
