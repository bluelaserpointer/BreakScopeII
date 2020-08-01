package item.ammo.enchant;

import java.awt.Color;

import core.GHQ;
import physics.Point;
import preset.bullet.Bullet;

public class Scatter extends AmmoEnchant {
	public Scatter() {
		super("Scatter", Color.LIGHT_GRAY);
	}
	@Override
	public void whenFire(Bullet bullet, int level) {
		Point.split_NWay(() -> GHQ.stage().addBullet(bullet.clone()), 10, new double[] {-0.2, -0.1, 0.1, 0.2}, bullet.point().speed());
	}
	@Override
	public AmmoEnchant clone() {
		return new Scatter();
	}
}