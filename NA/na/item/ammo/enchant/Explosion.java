package item.ammo.enchant;

import java.awt.Color;

import core.GHQObject;
import preset.bullet.Bullet;

public class Explosion extends AmmoEnchant {
	public Explosion() {
		super("Explosion", Color.RED);
	}
	@Override
	public void whenHit(Bullet bullet, GHQObject target, int level) {
		
	}
	@Override
	public AmmoEnchant clone() {
		return new Explosion();
	}
};