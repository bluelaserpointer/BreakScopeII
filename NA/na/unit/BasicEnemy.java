package unit;

import core.GHQ;
import engine.Engine_NA;

public abstract class BasicEnemy extends NAUnit{
	private static final long serialVersionUID = -5866340512027239900L;

	int lastDetectedFrame;
	public BasicEnemy(int charaSize, int initialGroup) {
		super(charaSize, initialGroup);
	}
	@Override
	public NAUnit respawn(int x, int y) {
		lastDetectedFrame = 0;
		return super.respawn(x, y);
	}
	@Override
	public void paint() {
		if(Engine_NA.player().isVisible(this)) {
			lastDetectedFrame = GHQ.nowFrame();
			super.paint();
			GHQ.paintHPArc(point(), 20, RED_BAR.intValue(), RED_BAR.defaultValue().intValue());
		}else {
			final double passedTime = GHQ.passedFrame(lastDetectedFrame)*GHQ.getSPF();
			final double KEEP_TIME = 3.0;
			if(passedTime < KEEP_TIME) {
				GHQ.setImageAlpha((float)(1F - passedTime/KEEP_TIME));
				super.paint();
				GHQ.setImageAlpha();
			}
		}
	}
}
