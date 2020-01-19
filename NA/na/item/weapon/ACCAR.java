package item.weapon;

import bullet.Bullet;
import bullet.BulletLibrary;
import calculate.Filter;
import core.GHQ;
import core.GHQObject;
import damage.NADamage;
import item.ItemData;
import paint.ImageFrame;
import physics.Point;
import physics.HitGroup;
import unit.NAUnit;
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
	public String name() {
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
			public void setBullets(GHQObject shooter, HitGroup standpoint) {
				final Bullet BULLET = GHQ.stage().addBullet(new BulletLibrary.ACCAR(this, shooter, standpoint));
				final NADamage NA_BALLET = (NADamage)BULLET.damage;
				NA_BALLET.setDamage(NA_BALLET.damage()*20);
				final Point BULLET_DYNAM = BULLET.point();
				BULLET_DYNAM.setSpeed(10);
				BULLET_DYNAM.addXY_allowsMoveAngle(0, 18);
			}
			@Override
			public int getLeftAmmo() {
				if(!hasOwner())
					return 0;
				int result = 0;
				for(ItemData item : ((NAUnit)owner).inventory.items) {
					if(item instanceof ACCAR_AMMO) {
						result += item.getAmount();
					}
				}
				return result;
			}
			@Override
			public void consumeAmmo(int value) {
				ItemData.removeInInventory(((NAUnit)owner).inventory.items, AMMO_FILTER, value);
			}
		};
	}
}
