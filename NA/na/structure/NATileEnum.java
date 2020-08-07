package structure;

public enum NATileEnum {
	WOOD(0, 100), GLASS(100, 100), BLOCK(50, 500), CONCRETE(100, 1000), ARMOR(1000, 1000);
	
	public final double armor, hp;
	private NATileEnum(double armor, double hp) {
		this.armor = armor;
		this.hp = hp;
	}
}
