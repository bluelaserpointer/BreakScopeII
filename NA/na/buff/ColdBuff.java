package buff;

import calculate.Mul;
import calculate.Setter;
import damage.DamageMaterial;
import paint.ImageFrame;
import paint.rect.RectPaint;
import unit.NAUnit;

public class ColdBuff extends NABuff {
	private static final long serialVersionUID = 7541690679927693327L;
	private static final Mul halfer = new Mul(0.5);
	private static final RectPaint rectPaint = ImageFrame.create("picture/buff/ColdBuff.png");
	public ColdBuff(NAUnit owner) {
		super(owner, rectPaint);
		owner.TOUGHNESS_REG.getValueWithCalculation_value().addCalculation(halfer);
		owner.AVD.getValueWithCalculation_value().addCalculation(Setter.ZERO_SETTER);
		owner.addDamageRes(DamageMaterial.Heat, 0.25);
	}
	@Override
	public void idle() {
		if(((NAUnit)owner).TOUGHNESS.isMax())
			removeFromOwner();
	}
	@Override
	public void removed() {
		((NAUnit)owner).TOUGHNESS_REG.getValueWithCalculation_value().removeCalculation(halfer);
		((NAUnit)owner).AVD.getValueWithCalculation_value().removeCalculation(Setter.ZERO_SETTER);
		((NAUnit)owner).addDamageRes(DamageMaterial.Heat, -0.25);
	}
	@Override
	public String description() {
		return "闪避率=0%, 必定被暴击";
	}
}