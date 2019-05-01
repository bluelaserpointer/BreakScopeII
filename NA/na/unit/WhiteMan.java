package unit;

import core.GHQ;
import paint.ImageFrame;
import physicis.Dynam;
import status.StatusWithDefaultValue;
import unit.Unit;

public class WhiteMan extends BasicNPC{
	private static final long serialVersionUID = -3224085275647002850L;
	public WhiteMan(int initialGroup) {
		super(120, initialGroup);
	}
	{
		charaSpeed = 1;
	}
	@Override
	public final String getName() {
		return "WhiteMan";
	}
	
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaPaint = new ImageFrame("thhimage/WhiteBall.png");
	}
	@Override
	public void activeCons() {
		final Unit blackManAdress = GHQ.getUnit("BlackMan");
		if(blackManAdress == null)
			return;
		final Dynam TARGET_DYNAM = blackManAdress.getDynam();
		charaDstX = TARGET_DYNAM.getX();
		charaDstY = TARGET_DYNAM.getY();
		final StatusWithDefaultValue TARGET_STATUS = ((BlackMan)blackManAdress).status;
		if(TARGET_STATUS.isBigger0(RED_BAR) && TARGET_STATUS.isSmaller(RED_BAR,10000) && dynam.getDistance(charaDstX, charaDstY) < 200){
			TARGET_STATUS.add(RED_BAR, 100);
		}
		dynam.approach(charaDstX, charaDstY, charaSpeed);
	}
	@Override
	public void startTalk() {
		GHQ.addMessage(this,"This is a prototype stage.Press enter key to continue the chat.");
		GHQ.addMessage(this,"I'm a NPC, stand close to me and press interact key can talk to.");
	}
}
