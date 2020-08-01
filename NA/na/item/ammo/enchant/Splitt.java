package item.ammo.enchant;

import java.awt.Color;

import core.GHQ;
import core.GHQObject;
import physics.Point;
import preset.bullet.Bullet;

public class Splitt extends AmmoEnchant {
	int time = 10;
	public Splitt() {
		super("Splitt", Color.DARK_GRAY);
	}
	@Override
	public void whenHit(Bullet bullet, GHQObject target, int level) {
		if(--time > 0)
			Point.split_NWay(() -> GHQ.stage().addBullet(bullet.clone()), 10, new double[] {-Math.PI/2, Math.PI/2}, bullet.point().speed());
	}
	@Override
	public AmmoEnchant clone() {
		return new Splitt();
	}
}