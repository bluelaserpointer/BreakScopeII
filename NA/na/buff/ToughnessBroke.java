package buff;

import calculate.Setter;
import paint.ImageFrame;
import paint.rect.RectPaint;
import unit.NAUnit;

public class ToughnessBroke extends NABuff{
	private static final long serialVersionUID = -3341059350704317027L;
	private static final RectPaint rectPaint = ImageFrame.create("picture/buff/ToughnessBroke.png");
	public ToughnessBroke(NAUnit owner) {
		super(owner, rectPaint);
		owner.SPEED_PPS.getValueWithCalculation_value().addCalculation(Setter.ZERO_SETTER);
	}

	@Override
	public void idle() {
		if(((NAUnit)owner).TOUGHNESS.isMax())
			removeFromOwner();
	}
	@Override
	public void removed() {
		((NAUnit)owner).SPEED_PPS.getValueWithCalculation_value().removeCalculation(Setter.ZERO_SETTER);
	}
}
