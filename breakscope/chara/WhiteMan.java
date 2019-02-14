package chara;

import core.DynamInteractable;
import core.GHQ;
import unit.Unit;
import unit.THHUnit;

public class WhiteMan extends THHUnit{
	{
		charaSize = 120;
		charaSpeed = 1;
	}
	private final int bulletIID[] = new int[10];
	@Override
	public final String getName() {
		return "WhiteMan";
	}
	
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaIID = GHQ.loadImage("WhiteBall.png");
		bulletIID[0] = GHQ.loadImage("LightBallA.png");
	}
	@Override
	public void activeCons() {
		final Unit blackManAdress = GHQ.getChara("BlackMan");
		if(blackManAdress == null)
			return;
		charaDstX = blackManAdress.dynam.getX();
		charaDstY = blackManAdress.dynam.getY();
		if(0 < blackManAdress.getHP() && blackManAdress.getHP() < 10000 && dynam.getDistance(charaDstX, charaDstY) < 200){
			blackManAdress.setHP(blackManAdress.getHP() + 100);
		}
		dynam.approach(charaDstX, charaDstY, charaSpeed);
	}
	@Override
	public void setEffect(int kind,DynamInteractable source) {}
	@Override
	public void setBullet(int kind,DynamInteractable source) {}
}
