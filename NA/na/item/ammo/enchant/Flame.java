package item.ammo.enchant;

import java.awt.Color;

import damage.DamageMaterial;
import damage.NADamage;

public class Flame extends AmmoEnchant {
	public Flame() {
		super("Flame", Color.RED);
	}
	@Override
	public void whenApply(NADamage damage, int level) {
		damage.addDamageComposition(DamageMaterial.Heat.makeComposition(level*10));
	}
	@Override
	public AmmoEnchant clone() {
		return new Flame();
	}
}