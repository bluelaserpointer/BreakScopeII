package item.ammo.enchant;

import java.awt.Color;

import core.GHQObject;
import damage.DamageMaterial;
import damage.NADamage;
import preset.bullet.Bullet;

public class Poison extends AmmoEnchant {
	public Poison() {
		super("Poison", Color.GREEN);
	}
	@Override
	public void whenApply(NADamage damage, int level) {
		damage.addDamageComposition(DamageMaterial.Poi.makeComposition(level*10));
	}
	@Override
	public void whenHit(Bullet bullet, GHQObject target, int level) {
		
	}
	@Override
	public AmmoEnchant clone() {
		return new Poison();
	}
}