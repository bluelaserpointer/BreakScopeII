package ui;

import gui.GUIParts;
import gui.ItemStorageViewer;
import item.ItemData;
import storage.TableStorage;
import unit.NAUnit;

public class DoubleInventoryViewer extends GUIParts {
	private ItemStorageViewer leftInventoryViewer;
	private ItemStorageViewer rightInventoryViewer;
	
	//init
	public DoubleInventoryViewer setLeftInventoryViewer(ItemStorageViewer inventoryViewer) {
		if(leftInventoryViewer != null)
			super.remove(leftInventoryViewer);
		leftInventoryViewer = inventoryViewer;
		super.addLast(leftInventoryViewer).point().setXY(50, 100);
		leftInventoryViewer.setCellSize(70);
		return this;
	}
	public DoubleInventoryViewer setRightInventoryViewer(ItemStorageViewer inventoryViewer) {
		if(rightInventoryViewer != null)
			super.remove(rightInventoryViewer);
		rightInventoryViewer = inventoryViewer;
		super.addLast(rightInventoryViewer).point().setXY(550, 100);
		rightInventoryViewer.setCellSize(70);
		return this;
	}
	//control
	public void setLeftInventory(NAUnit unit) {
		leftInventoryViewer.setTableStorage(unit.inventory);
	}
	public void setRightInventory(NAUnit unit) {
		rightInventoryViewer.setTableStorage(unit.inventory);
	}
	public void setLeftInventory(TableStorage<ItemData> inventory) {
		leftInventoryViewer.setTableStorage(inventory);
	}
	public void setRightInventory(TableStorage<ItemData> inventory) {
		rightInventoryViewer.setTableStorage(inventory);
	}
	//information
}
