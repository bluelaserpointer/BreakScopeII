package ui;

import java.awt.Color;
import java.util.HashMap;

import core.GHQ;
import gui.AutoResizeMenu;
import gui.GUIParts;
import gui.TableStorageViewer;
import item.ammo.enchant.AmmoEnchant;
import item.ammo.storage.AmmoBag;
import paint.ColorFilling;
import paint.ColorFraming;

public class AmmoBagViewer extends TableStorageViewer<AmmoBag> {
	protected AutoResizeMenu ammoEnchantsMenu;
	{
		this.backGroundPaint = new ColorFilling(Color.WHITE);
		this.cellPaint = new ColorFraming(Color.GRAY, GHQ.stroke1);
		addLast(ammoEnchantsMenu = new AutoResizeMenu(300, 20)).disable();
	}
	public AmmoBagViewer() {
		super(AmmoBag.class);
	}
	@Override
	public void mouseOver() {
		final AmmoBag ammoBag = this.getMouseHoveredElement();
		if(ammoBag != null) {
			ammoEnchantsMenu.enable();
			ammoEnchantsMenu.removeAll();
			final HashMap<AmmoEnchant, Integer> enchantLv = ammoBag.enchants().enchants();
			for(AmmoEnchant enchant : enchantLv.keySet()) {
				ammoEnchantsMenu.addNewLine(new GUIParts() {
					@Override
					public void paint() {
						super.paint();
						GHQ.getG2D(Color.GRAY);
						GHQ.drawStringGHQ(enchant.name, point().intX(), point().intY());
					}
				}).setBGColor(Color.LIGHT_GRAY);
			}
		}
	}
	@Override
	public void mouseOut() {
		super.mouseOut();
		ammoEnchantsMenu.disable();
	}
}
