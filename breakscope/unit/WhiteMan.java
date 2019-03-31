package unit;

import core.GHQ;
import paint.ImageFrame;
import physicis.Dynam;
import physicis.HasDynam;
import unit.Unit;

public class WhiteMan extends BSUnit{
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
	
	//Dynam
	private final Dynam dynam = new Dynam();
	@Override
	public final Dynam getDynam() {
		return dynam;
	}
	
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaPaint = new ImageFrame("thhimage/WhiteBall.png");
		bulletPaint[0] = new ImageFrame("thhimage/LightBallA.png");
	}
	@Override
	public void activeCons() {
		final Unit blackManAdress = GHQ.getChara("BlackMan");
		if(blackManAdress == null)
			return;
		final Dynam TARGET_DYNAM = blackManAdress.getDynam();
		charaDstX = TARGET_DYNAM.getX();
		charaDstY = TARGET_DYNAM.getY();
		final Status TARGET_STATUS = ((BlackMan)blackManAdress).status;
		if(TARGET_STATUS.isBigger0(HP) && TARGET_STATUS.isSmaller(HP,10000) && dynam.getDistance(charaDstX, charaDstY) < 200){
			TARGET_STATUS.add(HP, 100);
		}
		dynam.approach(charaDstX, charaDstY, charaSpeed);
	}
	@Override
	public void setEffect(int kind,HasDynam source) {}
	@Override
	public void setBullet(int kind,HasDynam source) {}
}
