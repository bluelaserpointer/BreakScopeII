package item.ammo.enchant;

import java.awt.Color;

import core.GHQObject;
import damage.NADamage;
import preset.bullet.Bullet;

public abstract class AmmoEnchant {
	public final String name;
	public final Color color;
	public AmmoEnchant(String name, Color color) {
		this.name = name;
		this.color = color;
	}
	public void whenApply(NADamage bullet, int level) {}
	public void whenFire(Bullet bullet, int level) {}
	public void whenHit(Bullet bullet, GHQObject target, int level) {}
	public abstract AmmoEnchant clone();
}
