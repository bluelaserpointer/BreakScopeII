package buff;

import core.GHQ;
import damage.DamageMaterialType;
import damage.DamageResourceType;
import damage.NADamage;
import paint.ImageFrame;
import paint.rect.RectPaint;
import unit.NAUnit;

public class LitBuff extends NABuff {
	private static final long serialVersionUID = 7541690679927693327L;
	private static final RectPaint rectPaint = ImageFrame.create("picture/buff/LitBuff.png");
	public LitBuff(NAUnit owner) {
		super(owner, rectPaint);
		owner.addDamageRes(DamageMaterialType.Ice, 0.25);
	}
	@Override
	public void idle() {
		if(GHQ.getSPF()*GHQ.passedFrame(super.INITIAL_FRAME) >= 1.0) {
			owner.damage(new NADamage(((NAUnit)owner).RED_BAR.max().doubleValue()*0.2,
					DamageMaterialType.Heat, DamageResourceType.Inner));
			removeFromOwner();
		}
	}
	@Override
	public void removed() {
		((NAUnit)owner).addDamageRes(DamageMaterialType.Ice, -0.25);
	}
}
