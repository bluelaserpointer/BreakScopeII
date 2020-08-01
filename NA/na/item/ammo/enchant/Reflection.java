package item.ammo.enchant;

import java.awt.Color;

public class Reflection extends AmmoEnchant {
	public Reflection() {
		super("Reflection", Color.GREEN);
	}

	@Override
	public AmmoEnchant clone() {
		return new Reflection();
	}
}