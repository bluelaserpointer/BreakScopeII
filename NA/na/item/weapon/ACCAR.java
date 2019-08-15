package item.weapon;

import bullet.BulletLibrary;
import calculate.Filter;
import core.GHQ;
import item.ItemData;
import paint.ImageFrame;
import physics.Dynam;
import physics.HasAnglePoint;
import physics.Standpoint;
import unit.BasicUnit;
import weapon.Weapon;

public class ACCAR extends Equipment implements MainSlot{
	private static final long serialVersionUID = 6160992296259886036L;
	public interface ACCAR_AMMO {};
	private final Filter<ItemData> AMMO_FILTER = new Filter<ItemData>() {
		@Override
		public boolean judge(ItemData item) {
			return item instanceof ACCAR_AMMO;
		}
	};
	public ACCAR() {
		super(ImageFrame.create("picture/AK.png"));
	}
	@Override
	public String getName() {
		return weapon.name;
	}
	@Override
	public Weapon def_weapon() {
		return new Weapon() {
			private static final long serialVersionUID = -1692674505068462831L;
			{
				name = "ACCAR";
				coolTime = 50;
				magazineSize = 10;
				reloadTime = 150;
			}
			@Override
			public void setBullets(HasAnglePoint shooter, Standpoint standpoint) {
				final Dynam BULLET_DYNAM = GHQ.stage().addBullet(new BulletLibrary.ACCAR(this, shooter, standpoint)).dynam();
				BULLET_DYNAM.setSpeed(10);
				BULLET_DYNAM.addXY_allowsMoveAngle(0, 18);
			}
			@Override
			public int getLeftAmmo() {
				if(!hasOwner())
					return 0;
				int result = 0;
				for(ItemData item : ((BasicUnit)owner).inventory.items) {
					if(item instanceof ACCAR_AMMO) {
						result += item.getAmount();
					}
				}
				return result;
			}
			@Override
			public void consumeAmmo(int value) {
				ItemData.removeInInventory(((BasicUnit)owner).inventory.items, AMMO_FILTER, value);
			}
		};
	}
}
