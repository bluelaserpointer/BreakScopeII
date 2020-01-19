package buff;

import paint.rect.RectPaint;
import unit.NAUnit;

public abstract class NABuff extends Buff{
	private static final long serialVersionUID = -4637077357034459713L;
	private final RectPaint rectPaint;
	public NABuff(NAUnit owner, RectPaint rectPaint) {
		super(owner);
		this.rectPaint = rectPaint;
	}
	@Override
	public abstract void idle();
	@Override
	public RectPaint getRectPaint() {
		return rectPaint;
	}
	public final void removeFromOwner() {
		((NAUnit)owner).removeOneBuff(this);
	}
}
