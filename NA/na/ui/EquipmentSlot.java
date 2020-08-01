package ui;

import java.awt.event.MouseEvent;

import core.GHQ;
import gui.GHQTextArea;
import gui.GUIParts;
import item.NAItem;
import item.equipment.Equipment;
import paint.ImageFrame;
import preset.item.ItemData;
import preset.unit.BodyParts;
import unit.NAUnit;

public abstract class EquipmentSlot extends GHQTextArea {
	protected abstract BodyParts targetBodyParts();
	{
		textArea().setFont(GHQ.initialFont.deriveFont(20F));
	}
	protected final Equipment equipment() {
		return (Equipment)targetBodyParts().equipment();
	}
	private final ImageFrame slotIF = ImageFrame.create("picture/gui/Bag_item.png");
	@Override
	public void paint() {
		slotIF.rectPaint(point().intX(), point().intY(), width(), height());
		super.paint();
		final Equipment equipment = equipment();
		if(equipment != null)
			equipment.getDotPaint().dotPaint_capSize(point().intX() + width()/2, point().intY() + height()/2, Math.min(width(), height()));
	}
	@Override
	public boolean clicked(MouseEvent e) {
		super.clicked(e);
		if(targetBodyParts().hasEquipment())
			GHQ.mouseHook.hook(equipment(), this);
		return true;
	}
	@Override
	public boolean doLinkDrag() {
		return true;
	}
	@Override
	public boolean checkDragIn(GUIParts sourceUI, Object dropObject) {
		return dropObject instanceof NAItem && dropObject instanceof Equipment
				&& ((ItemData)dropObject).canEquipTo(targetBodyParts());
	}
	@Override
	public void dragIn(GUIParts sourceUI, Object dropObject) {
		final NAUnit unit = (NAUnit)((Equipment)dropObject).owner();
		unit.body().equip((Equipment)dropObject, targetBodyParts());
		unit.setBattleStance(true);
	}
	@Override
	public Object peekDragObject() {
		return equipment();
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
	@Override
	public EquipmentSlot setName(String name) {
		super.setName(name);
		textArea().setText(name);
		return this;
	}
}
