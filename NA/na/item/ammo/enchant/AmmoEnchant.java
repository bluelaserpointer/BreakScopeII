package item.ammo.enchant;

import java.awt.Color;

import bullet.Bullet;
import core.GHQObject;
import damage.DamageMaterial;
import damage.NADamage;

public enum AmmoEnchant {
	Flame("Flame", Color.RED) {
		@Override
		public void whenApply(NADamage damage, int level) {
			damage.addDamageComposition(DamageMaterial.Heat.makeComposition(level*10));
		}
	},
	Poison("Poison", Color.GREEN) {
		@Override
		public void whenApply(NADamage damage, int level) {
			damage.addDamageComposition(DamageMaterial.Poi.makeComposition(level*10));
		}
		@Override
		public void whenHit(Bullet bullet, GHQObject target, int level) {
			
		}
	},
	Penetration("Penetration", Color.CYAN) {
		int time = 10;
		@Override
		public void whenHit(Bullet bullet, GHQObject target, int level) {
			if(--time > 0)
				bullet.clone_NWay(10, new double[] {0.0}, bullet.point().speed());
		}
	},
	Reflection("Reflection", Color.GREEN),
	Scatter("Scatter", Color.LIGHT_GRAY) {
		@Override
		public void whenFire(Bullet bullet, int level) {
			bullet.clone_NWay(10, new double[] {-0.2, -0.1, 0.1, 0.2}, bullet.point().speed());
		}
	},
	Splitt("Splitt", Color.DARK_GRAY) {
		int time = 10;
		@Override
		public void whenHit(Bullet bullet, GHQObject target, int level) {
			if(--time > 0)
				bullet.clone_NWay(10, new double[] {-Math.PI/2, Math.PI/2}, bullet.point().speed());
		}
	},
	Explosion("Explosion", Color.RED) {
		@Override
		public void whenHit(Bullet bullet, GHQObject target, int level) {
			
		}
	};
	
	public final String name;
	public final Color color;
	private AmmoEnchant(String name, Color color) {
		this.name = name;
		this.color = color;
	}
	public void whenApply(NADamage bullet, int level) {}
	public void whenFire(Bullet bullet, int level) {}
	public void whenHit(Bullet bullet, GHQObject target, int level) {}
}
