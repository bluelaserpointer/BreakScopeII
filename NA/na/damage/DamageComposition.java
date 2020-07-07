package damage;

/**
 * 伤害成分
 *
 */
public class DamageComposition {
	protected DamageMaterial damageMaterial;
	protected double value;
	protected boolean fromInside;
	public static final boolean INSIDE = true;
	public static final boolean OUTSIDE = false;
	public DamageComposition(DamageMaterial damageMaterial, double value) {
		this.damageMaterial = damageMaterial;
		this.value = value;
		this.fromInside = false;
	}
	public DamageComposition(DamageMaterial damageMaterial, double value, boolean fromInside) {
		this.damageMaterial = damageMaterial;
		this.value = value;
		this.fromInside = fromInside;
	}
	public DamageComposition setDamage(double value) {
		this.value = value;
		return this;
	}
	public DamageComposition setMaterial(DamageMaterial materialType) {
		this.damageMaterial = materialType;
		return this;
	}
	public DamageComposition setfromInside(boolean fromInside) {
		this.fromInside = fromInside;
		return this;
	}
	public DamageMaterial material() {
		return damageMaterial;
	}
	public double damage() {
		return value;
	}
	public boolean fromInside() {
		return fromInside;
	}
}
