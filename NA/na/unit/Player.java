package unit;

import static java.awt.event.KeyEvent.*;

import engine.NAGame;
import item.equipment.weapon.ElectronShield;
import paint.ImageFrame;
import paint.dot.DotPaintMultiple;
import talent.AllUp;

public class Player extends NAUnit {
	
	public Player() {
		super(20);
		this.addTalent(new AllUp(this));
	}
	@Override
	public final Player respawn(int x, int y) {
		super.respawn(x, y);
		equip(addItemToStorage(new ElectronShield(5000)));
		this.setBattleStance(true);
		return this;
	}

	@Override
	public final String name() {
		return "Player";
	}
	@Override
	public final UnitGroup unitGroup() { //TODO change unitGroup upon current coat
		return UnitGroup.PRISONER;
	}
	
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaPaint = new DotPaintMultiple(ImageFrame.create("picture/human2-1.png"));
		personalIcon = ImageFrame.create("thhimage/MarisaIcon.png");
	}
	
	//idle
	@Override
	public void idle() {
		super.idle();
		//buff testing space
		if(NAGame.s_keyL.hasEvent(VK_SPACE)) {
			//this.damage(new NADamage(50, DamageMaterialType.Heat, DamageResourceType.Bullet));
			//System.out.println(this.TOUGHNESS.doubleValue());
			
		}
		if(NAGame.s_keyL.hasEvent(VK_SHIFT)) {
			//this.damage(new NADamage(50, DamageMaterialType.Ice, DamageResourceType.Bullet));
		}
		if(NAGame.s_keyL.hasEvent(VK_R)) {
			RED_BAR.consume(-100);
		}
		if(NAGame.s_keyL.hasEvent(VK_F)) {
			//this.damage(new NADamage(1, DamageMaterialType.Phy, DamageResourceType.Bullet));
		}
	}
}
