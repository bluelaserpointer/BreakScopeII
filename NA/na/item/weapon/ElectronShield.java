package item.weapon;

import core.GHQ;
import paint.ImageFrame;
import unit.BasicUnit;
import weapon.Weapon;

public class ElectronShield extends Equipment implements SubSlot{
	private static final long serialVersionUID = -8064058001166040739L;
	private int lastDamaged = 0;
	public ElectronShield() {
		super(ImageFrame.create("picture/FreezeEffect.png"));
	}
	@Override
	public String name() {
		return "ELECTRON_SHIELD";
	}
	@Override
	protected Weapon def_weapon() {
		return new Weapon() {
			private static final long serialVersionUID = -3255234899242748103L;
			{
				name = "ELECTRON_SHIELD";
				magazineSize = 500;
				magazine = 500;
			}
			@Override
			public void idle() {
				super.idle();
				if(GHQ.isExpired_dynamicSeconds(lastDamaged, 10.0)) {
					damaged();
					magazine += magazineSize/10;
					if(magazine > magazineSize)
						magazine = magazineSize;
				}
			}
			@Override
			public int getLeftAmmo() {
				return ((BasicUnit)owner).BLUE_BAR.intValue();
			}
			@Override
			public void consumeAmmo(int value) {
				((BasicUnit)owner).BLUE_BAR.consume(value);
			}
		};
	}
	public void damaged() {
		lastDamaged = GHQ.nowFrame();
	}
}
