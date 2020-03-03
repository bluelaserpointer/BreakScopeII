package ui;

import gui.GUIParts;
import gui.ItemStorageViewer;
import unit.NAUnit;

public class DoubleInventoryViewer extends GUIParts {
	private ItemStorageViewer leftInventoryViewer;
	private ItemStorageViewer rightInventoryViewer;
	
	//init
	public DoubleInventoryViewer setLeftInventoryViewer(ItemStorageViewer inventoryViewer) {
		if(leftInventoryViewer != null)
			super.remove(leftInventoryViewer);
		leftInventoryViewer = inventoryViewer;
		super.addLast(leftInventoryViewer);
		return this;
	}
	public DoubleInventoryViewer setRightInventoryViewer(ItemStorageViewer inventoryViewer) {
		if(rightInventoryViewer != null)
			super.remove(rightInventoryViewer);
		rightInventoryViewer = inventoryViewer;
		super.addLast(rightInventoryViewer);
		return this;
	}
	//control
	public void setLeftInventory(NAUnit unit) {
		leftInventoryViewer.setTableStorage(unit.inventory);
	}
	//information
}
