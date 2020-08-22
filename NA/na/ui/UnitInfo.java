package ui;

import java.awt.Color;
import java.awt.event.MouseEvent;

import core.GHQ;
import gui.BasicButton;
import gui.GUIParts;
import gui.ItemStorageViewer;
import gui.TableStorageViewer;
import item.ammo.AmmoType;
import math.SquareCellArranger;
import paint.ImageFrame;
import paint.text.StringPaint;
import preset.item.ItemData;
import storage.TableStorage;
import unit.NAUnit;

/**
 * Shows unit info.
 * Right click to close.
 * @author bluelaserpointer
 * @since alpha1.0
 */
public class UnitInfo extends GUIParts {
	public NAUnit targetUnit;
	protected TableStorageViewer<ItemData> itemStorageViewer;
	protected AmmoStorageViewer ammoStorageViewer;
	{
		this.setBounds(0, 0, GHQ.screenW(), GHQ.screenH());
		this.setBGColor(new Color(100, 100, 100, 128));
		//stun attack, talk
		this.addLast(new BasicButton(new StringPaint("钝击", GHQ.getG2D().getFont().deriveFont(40F), Color.BLACK)) {
			{
				setBounds(0, 100, GHQ.screenW()/2, 75);
			}
			@Override
			public void paint() {
				GHQ.getG2D(Color.WHITE).fillRect(cx(), cy(), width(), height());
				GHQ.getG2D(Color.GRAY, GHQ.stroke3).drawRect(cx(), cy(), width(), height());
				super.paint();
			}
		});
		this.addLast(new BasicButton(new StringPaint("对话", GHQ.getG2D().getFont().deriveFont(40F), Color.BLACK)) {
			{
				setBounds(GHQ.screenW()/2, 100, GHQ.screenW()/2, 75);
			}
			@Override
			public void paint() {
				GHQ.getG2D(Color.WHITE).fillRect(cx(), cy(), width(), height());
				GHQ.getG2D(Color.GRAY, GHQ.stroke3).drawRect(cx(), cy(), width(), height());
				super.paint();
			}
		});
		//icon with name, status
		this.addLast(new GUIParts() {
			{
				setBounds(20, 200, 160, 100);
				this.setBGColor(Color.WHITE);
			}
			@Override
			public void paint() {
				super.paint();
			}
		});
		this.addLast(new GUIParts() {
			{
				setBounds(200, 200, GHQ.screenW()/2, 100);
				this.setBGColor(Color.WHITE);
			}
			@Override
			public void paint() {
				super.paint();
				if(targetUnit == null)
					return;
				final int lineH = 20;
				int i = 1;
				GHQ.getG2D(Color.BLACK);
				GHQ.drawStringGHQ("HP: " + targetUnit.RED_BAR.doubleValue(), cx(), cy() + lineH*i++);
				GHQ.drawStringGHQ("MP: " + targetUnit.BLUE_BAR.doubleValue(), cx(), cy() + lineH*i++);
				GHQ.drawStringGHQ("SP: " + targetUnit.GREEN_BAR.doubleValue(), cx(), cy() + lineH*i++);
				GHQ.drawStringGHQ("WP: " + targetUnit.WHITE_BAR.doubleValue(), cx(), cy() + lineH*i++);
			}
		});
		//icon with name, status
		addLast(itemStorageViewer = new ItemStorageViewer().setRCMenu(new ItemRCMenu_inventory()).setCellPaint(ImageFrame.create("picture/gui/Bag_item.png")).setCellSize(70))
		.point().setXY(20, 320);
//		addLast(ammoStorageViewer = new AmmoStorageViewer(460, 320, new SquareCellArranger(1, 50, 50*AmmoType.TYPE_AMOUNT, 1, AmmoType.TYPE_AMOUNT)));

	}
	@Override
	public boolean clicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3)
			super.disable();
		return super.clicked(e);
	}
	public void setTargetUnit(NAUnit unit) {
		this.targetUnit = unit;
		itemStorageViewer.setStorage((TableStorage<ItemData>)targetUnit.inventory);
//		ammoStorageViewer.setTargetUnit(unit);
	}
}
