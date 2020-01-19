package unit;

import static java.awt.event.KeyEvent.*;

import java.awt.Color;

import core.GHQ;
import damage.DamageResourceType;
import damage.NADamage;
import damage.DamageMaterialType;
import engine.Engine_NA;
import item.ItemData;
import paint.ImageFrame;
import paint.dot.DotPaintMultiple;
import stage.Gridder;
import storage.ItemStorage;
import storage.TableStorage;

public class Player extends BasicPlayer{
	private static final long serialVersionUID = 8121281285749873895L;
	
	public Player(int initialGroup) {
		super(20, initialGroup);
	}

	@Override
	public final String name() {
		return "Player";
	}
	@Override
	public ItemStorage def_inventory() {
		return new ItemStorage(new TableStorage<ItemData>(5, 3, ItemData.BLANK_ITEM));
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
		//buff testing space
		if(Engine_NA.s_keyL.pullEvent(VK_SPACE)) {
			this.damage(new NADamage(50, DamageMaterialType.Heat, DamageResourceType.Bullet));
			//System.out.println(this.TOUGHNESS.doubleValue());
		}
		if(Engine_NA.s_keyL.pullEvent(VK_SHIFT)) {
			this.damage(new NADamage(50, DamageMaterialType.Ice, DamageResourceType.Bullet));
		}
		if(Engine_NA.s_keyL.pullEvent(VK_R)) {
			RED_BAR.consume(-100);
		}
		////////////
		//aim
		////////////
		angle().set(point().angleToMouse());
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
				GHQ.getG2D(Color.WHITE);
				GHQ.drawStringGHQ(item.name(), item.point().intX(), item.point().intY() - 20);
			}
		}
		////////////
		//talk
		////////////
		if(Engine_NA.s_keyL.pullEvent(VK_E)) {
			final Unit npc = GHQ.stage().getNearstVisibleEnemy(this);
			if(npc instanceof BasicNPC && npc.point().inRange(this.point(), 240)) {
				((BasicNPC) npc).startTalk();
			}
		}
		////////////
		//enlightVisibleArea
		////////////
		Gridder gridder = new Gridder(50, 50);
		for(int xPos = 0;xPos < gridder.W_DIV;++xPos) {
			for(int yPos = 0;yPos < gridder.H_DIV;++yPos) {
				if(super.isVisible(gridder.getPosPoint(xPos, yPos))) {
					gridder.drawGrid(GHQ.getG2D(Color.RED, 1F), xPos, yPos);
				}
			}
		}
	}
}
