package item.ammo;

import java.awt.Color;

import item.NAItem;
import item.ammo.enchant.AmmoEnchant;
import item.ammo.enchant.AmmoEnchants;
import paint.dot.DotPaint;

public class Ammo extends NAItem {
	final AmmoType type;
	final AmmoEnchants enchants = new AmmoEnchants();
	public Ammo(AmmoType type, int amount) {
		super(type.paint);
		this.type = type;
		this.amount = amount;
	}
	
	//control
	public Ammo addEnchant(AmmoEnchant enchant, int level) {
		enchants.addEnchant(enchant, level);
		return this;
	}
	public void removeEnchant(AmmoEnchant enchant) {
		enchants.removeEnchant(enchant);
	}
	
	//information
	@Override
	public boolean keepEvenEmpty() {
		return false;
	}
	public AmmoEnchants enchants() {
		return enchants;
	}
	public DotPaint enchantsPaint() {
		return enchants.getDotPaint();
	}
	public Color enchantsColor() {
		return enchants.enchantsColor();
	}
	/**
	 * Returns weight per bullet.(kg)
	 * @return kg weight per bullet
	 */
	public double weightPerBullet() {
		return type.weight;
	}
	@Override
	public final double weight() {
		return weightPerBullet()*amount;
	}
	public AmmoType type() {
		return type;
	}
	public boolean isType(AmmoType type) {
		return this.type == type;
	}
}
