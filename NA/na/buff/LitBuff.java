package buff;

import core.GHQ;
import damage.DamageMaterial;
import paint.ImageFrame;
import paint.rect.RectPaint;
import unit.NAUnit;

public class LitBuff extends NABuff {
	private static final long serialVersionUID = 7541690679927693327L;
	private static final RectPaint rectPaint = ImageFrame.create("picture/buff/LitBuff.png");
	private int initialFrame;
	private int passedSeconds = 0;
	public LitBuff(NAUnit owner) {
		super(owner, rectPaint);
		initialFrame = super.INITIAL_FRAME;
		owner.addDamageRes(DamageMaterial.Cold, 0.25);
	}
	@Override
	public void idle() {
		if(GHQ.getSPF()*GHQ.passedFrame(initialFrame) >= 1.0) {
			owner.damage(DamageMaterial.Heat.makeDamage(((NAUnit)owner).RED_BAR.max().doubleValue()*0.2));
			initialFrame = GHQ.nowFrame();
			if(++passedSeconds == 3)
				removeFromOwner();
		}
	}
	public void resetTime() {
		passedSeconds = 0;
	}
	@Override
	public void removed() {
		((NAUnit)owner).addDamageRes(DamageMaterial.Cold, -0.25);
	}
	@Override
	public String description() {
		return "每秒受生命值上限20%的【高温伤害】";
	}
}
