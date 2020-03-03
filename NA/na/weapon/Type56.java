package weapon;

import java.awt.Color;
import java.awt.Graphics2D;

import bullet.Bullet;
import bullet.BulletLibrary;
import calculate.Filter;
import core.GHQ;
import core.GHQObject;
import damage.NADamage;
import item.ItemData;
import paint.ImageFrame;
import paint.dot.DotPaint;
import physics.Point;
import physics.HitGroup;
import unit.NAUnit;
import weapon.Weapon;
import weapon.gripStyle.RifleGrip;

public class Type56 extends Equipment {
	public interface ACCAR_AMMO {};
	private final Filter<ItemData> AMMO_FILTER = new Filter<ItemData>() {
		@Override
		public boolean judge(ItemData item) {
			return item instanceof ACCAR_AMMO;
		}
	};
	public Type56() {
		super(new DotPaint() {
			final ImageFrame if56 = ImageFrame.create("picture/weapon/56.png");
			@Override
			public void dotPaint(int x, int y) {
				if56.dotPaint_rate(x, y, 1.2);
			}
			@Override
			public int width() {
				return if56.width();
			}
			@Override
			public int height() {
				return if56.height();
			}
		}, NAUnit.BodyPartsTypeLibrary.MAIN_WEAPON);
		super.setFocusPaint(ImageFrame.create("thhimage/focus.png"));
		super.setGripStyle(new RifleGrip() {
			@Override
			public int[] handXPositions() {
				return new int[] {2, 22};
			}
			@Override
			public int[] handYPositions() {
				return new int[] {4, 3};
			}
		});
	}
	@Override
	public Weapon def_weapon() {
		return new Weapon() {
			{
				name = "ACCAR";
				coolTime = 50;
				magazineSize = 10;
				reloadTime = 150;
			}
			@Override
			public void setBullets(GHQObject shooter, HitGroup standpoint) {
				final Bullet bullet = GHQ.stage().addBullet(new BulletLibrary.ACCAR(this, shooter, standpoint));
				final NADamage naDamage = (NADamage)bullet.damage;
				naDamage.setDamage(naDamage.damage()*20);
				naDamage.setKnockbackRate(0.3);
				final Point BULLET_DYNAM = bullet.point();
				BULLET_DYNAM.setSpeed(10);
				bullet.point().addXY_allowsMoveAngle(0, shooter.width());
			}
			@Override
			public int getLeftAmmo() {
				if(!hasOwner())
					return 0;
				int result = 0;
				for(ItemData item : ((NAUnit)owner).inventory) {
					if(item instanceof ACCAR_AMMO) {
						result += item.getAmount();
					}
				}
				return result;
			}
			@Override
			public void consumeAmmo(int value) {
				ItemData.removeInInventory(((NAUnit)owner).inventory, AMMO_FILTER, value);
			}
		};
	}
	@Override
	protected void paintFocus(int x, int y) {
		final Graphics2D G2 = GHQ.getG2D();
		G2.setColor(new Color(200,120,10,100));
		G2.setStroke(GHQ.stroke3);
		G2.drawLine(owner().point().intX(), owner().point().intY(), x, y);
		if(weapon.isReloadFinished())
			focusPaint.dotPaint(x, y);
		else
			focusPaint.dotPaint_turn(x, y, GHQ.nowFrame()/10);
		G2.setColor(weapon.canFire() ? Color.WHITE : Color.RED);
		final int UNFIRED = weapon.getMagazineFilledSpace(), LEFT_AMMO = weapon.getLeftAmmo();
		G2.drawString(UNFIRED != GHQ.MAX ? String.valueOf(UNFIRED) : "-", x - 25, y);
		G2.drawString(LEFT_AMMO != GHQ.MAX ? String.valueOf(LEFT_AMMO) : "-", x, y);
		G2.setColor(weapon.isCoolFinished() ? Color.WHITE : Color.RED);
		G2.drawString(weapon.getCoolProgress() + "/" + 50, x - 25, y + 25);
		G2.setColor(weapon.isReloadFinished() ? Color.WHITE : Color.RED);
		G2.drawString(weapon.getReloadProgress() + "/" + 150, x + 35, y + 25);
	}
	@Override
	public String name() {
		return weapon.name;
	}
}
