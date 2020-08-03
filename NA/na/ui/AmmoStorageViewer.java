package ui;

import java.awt.Color;

import core.GHQ;
import gui.ArrangedButtons;
import gui.BasicButton;
import gui.TableStorageViewer;
import item.ammo.AmmoType;
import item.ammo.storage.AmmoBag;
import math.CellArranger;
import paint.ImageFrame;
import storage.TableStorage;
import unit.NAUnit;

public class AmmoStorageViewer extends ArrangedButtons<AmmoType> {
	protected AmmoType openedAmmoType = null;
	public TableStorageViewer<AmmoBag> ammoStorageViewer;
	protected NAUnit targetUnit;
	public AmmoStorageViewer(int x, int y, CellArranger arranger) {
		super(x, y, arranger);
	}
	{
		final AmmoType types[] = AmmoType.values();
		for(int i = 0; i < types.length; ++i) {
			super.appendButton(types[i], ImageFrame.create("picture/gui/Bag_item.png"), 0, types.length - 1 - i);
		}
		addLast(ammoStorageViewer = new AmmoBagViewer()).disable().point().setXY(500, 100);
	}
	@Override
	protected void buttonClicked(AmmoType buttonValue) { //TODO: open UI info about the ammoBagList
		if(openedAmmoType != buttonValue) {
			openedAmmoType = buttonValue;
			ammoStorageViewer.setTableStorage(new TableStorage<AmmoBag>(targetUnit.ammoStorage.ammoBagList(buttonValue), 5, AmmoBag.EMPTY_BAG));
			ammoStorageViewer.enable();
		} else {
			ammoStorageViewer.disable();
		}
	}
	@Override
	protected void buttonExtendPaint(AmmoType buttonValue, BasicButton button) {
		buttonValue.paint.dotPaint(button.point().intX(), button.point().intY());
		GHQ.getG2D(Color.GRAY);
		GHQ.drawStringGHQ(String.valueOf(targetUnit.ammoStorage.countByType(buttonValue)), button.point().intX() + 20, button.point().intY() + 40);
	}
	protected AmmoStorageViewer setTargetUnit(NAUnit unit) {
		this.targetUnit = unit;
		return this;
	}
}
