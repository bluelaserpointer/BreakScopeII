package damage;

import java.awt.Color;

public enum DamageMaterialType {
	Phy, Heat, Ice, Poi, Rea;
	@Override
	public String toString() {
		switch(this) {
		case Heat:
			return "Heat";
		case Ice:
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
			return Color.ORANGE;
		case Ice:
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
}
