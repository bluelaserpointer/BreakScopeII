package damage;

import java.awt.Color;

public enum DamageMaterial { //真实->闪电->低温->物理->高温->毒液
	Phy, Heat, Cold, Poi, Rea;
	public boolean isPhysical() {
		return this == Phy;
	}
	public boolean isHeat() {
		return this == Heat;
	}
	public boolean isCold() {
		return this == Cold;
	}
	public boolean isPoison() {
		return this == Poi;
	}
	public boolean isReal() {
		return this == Rea;
	}
	@Override
	public String toString() {
		switch(this) {
		case Heat:
			return "Heat";
		case Cold:
			return "Ice";
		case Phy:
			return "Physical";
		case Poi:
			return "Poison";
		case Rea:
			return "Real";
		default:
			return null;
		}
	}
	public Color color() {
		switch(this) {
		case Heat:
			return new Color(234, 162, 0);
		case Cold:
			return Color.BLUE;
		case Phy:
			return Color.RED;
		case Poi:
			return Color.GREEN;
		case Rea:
			return Color.WHITE;
		default:
			return null;
		}
	}
	public DamageComposition makeComposition(double value, boolean fromInside) {
		return new DamageComposition(this, value, fromInside);
	}
	public DamageComposition makeComposition(double value) {
		return makeComposition(value, DamageComposition.OUTSIDE);
	}
	public NADamage makeDamage(double value, boolean fromInside) {
		return new NADamage(makeComposition(value, fromInside));
	}
	public NADamage makeDamage(double value) {
		return makeDamage(value, DamageComposition.OUTSIDE);
	}
}
