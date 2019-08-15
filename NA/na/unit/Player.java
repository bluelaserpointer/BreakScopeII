package unit;

import static java.awt.event.KeyEvent.*;

import java.awt.Color;

import core.GHQ;
import engine.Engine_NA;
import item.ItemData;
import paint.ImageFrame;
import paint.dot.DotPaintMultiple;
import storage.ItemStorage;
import storage.TableStorage;

public class Player extends BasicPlayer{
	private static final long serialVersionUID = 8121281285749873895L;
	
	public Player(int initialGroup) {
		super(20, initialGroup);
	}

	@Override
	public final String getName() {
		return "Player";
	}
	@Override
	public ItemStorage def_inventory() {
		return new ItemStorage(new TableStorage<ItemData>(5,3));
	}
	
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaPaint = new DotPaintMultiple(ImageFrame.create("picture/human2-1.png"));
		iconPaint = ImageFrame.create("thhimage/MarisaIcon.png");
	}
	
	//idle
	@Override
	public void idle() {
		super.idle();
		////////////
		//aim
		////////////
		baseAngle.set(dynam.angleToMouse());
		////////////
		//reload
		////////////
		if(Engine_NA.s_keyL.pullEvent(VK_R))
			mainSlot.reloadIfEquipment();
		////////////
		//itemPick
		////////////
		final ItemData item = GHQ.stage().items.forIntersects(this);
		if(item != null) {
			if(Engine_NA.s_keyL.pullEvent(VK_E))
				super.inventory.items.add(item.pickup(this));
			else {
				GHQ.getGraphics2D(Color.WHITE);
				GHQ.drawStringGHQ(item.getName(), item.point().intX(), item.point().intY() - 20);
			}
		}
		////////////
		//talk
		////////////
		if(Engine_NA.s_keyL.pullEvent(VK_SPACE)) {
			final Unit npc = GHQ.stage().getNearstVisibleEnemy(this);
			if(npc instanceof BasicNPC && npc.dynam.inRange(this.dynam,240)) {
				((BasicNPC) npc).startTalk();
			}
		}
	}
}
