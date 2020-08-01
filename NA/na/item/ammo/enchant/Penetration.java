package item.ammo.enchant;

import java.awt.Color;

import bullet.BulletLibrary;
import core.GHQ;
import core.GHQObject;
import effect.EffectLibrary;
import physics.Point;
import preset.bullet.Bullet;

public class Penetration extends AmmoEnchant {
	int time;
	public Penetration(int time) {
		super("Penetration", Color.CYAN);
		this.time = time;
	}
	public Penetration(Penetration sample) {
		super("Penetration", Color.CYAN);
		time = sample.time;
	}
	@Override
	public void whenHit(Bullet bullet, GHQObject target, int level) {
		for(int i = 0; i < 5; ++i)
			GHQ.stage().addEffect(new EffectLibrary.PenetratedEF(bullet));
		if(--time > 0) {
			((BulletLibrary)bullet).suspendSparkEF = true;
			Point.split_NWay(() -> GHQ.stage().addBullet(bullet.clone()), 10, new double[] {0.0}, bullet.point().speed());
		}
	}
	@Override
	public AmmoEnchant clone() {
		return new Penetration(this);
	}
}