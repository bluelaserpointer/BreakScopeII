package talent;

import core.HasName;
import item.NAUsable;
import paint.dot.DotPaint;
import unit.NAUnit;

public class Talent implements HasName, NAUsable {
	public static final Talent NULL_TALENT = new Talent("NullTalent", DotPaint.BLANK_SCRIPT, NAUnit.NULL_NAUnit);
	private final String name;
	protected NAUnit owner;
	protected int nowLevel;
	protected final DotPaint iconImage;
	public Talent(String name, DotPaint iconImage, NAUnit owner) {
		this.name = name;
		this.iconImage = iconImage;
		this.owner = owner;
	}
	//main role
	public void levelUp() {
		++nowLevel;
	};
	//information
	@Override
	public String name() {
		return name;
	}
	public NAUnit owner() {
		return owner;
	}
	@Override
	public DotPaint getDotPaint() {
		return iconImage;
	}
	@Override
	public void use() {}
	public String description() {
		return "- No Description -";
	}
}
