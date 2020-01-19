package buff;

import core.GHQ;
import damage.DamageResourceType;
import damage.NADamage;
import damage.DamageMaterialType;
import paint.ImageFrame;
import paint.rect.RectPaint;
import unit.NAUnit;

public class PoisonBuff extends NABuff {
	private static final long serialVersionUID = -7662848025097282710L;
	private static final RectPaint rectPaint = ImageFrame.create("picture/buff/PoisonBuff.png");
	private int nowDamage;
	public PoisonBuff(NAUnit owner, int value) {
		super(owner, rectPaint);
		nowDamage = value;
	}
	@Override
	public void idle() {
		if(GHQ.getSPF()*GHQ.passedFrame(super.INITIAL_FRAME) >= 1.0) {
			owner.damage(new NADamage(nowDamage, DamageMaterialType.Poi, DamageResourceType.Inner));
			removeFromOwner();
		}
	}
}
