package unit;

import core.GHQ;
import engine.Engine_NA;

public abstract class BasicEnemy extends BasicUnit{
	private static final long serialVersionUID = -5866340512027239900L;

	int lastDetectedFrame;
	public BasicEnemy(int charaSize, int initialGroup) {
		super(charaSize, initialGroup);
	}
	@Override
	public BasicUnit respawn(int x, int y) {
		lastDetectedFrame = 0;
		return super.respawn(x, y);
	}
	@Override
	public void paint() {
		if(Engine_NA.getPlayer().isVisible(this)) {
			lastDetectedFrame = GHQ.nowFrame();
			super.paint();
			GHQ.paintHPArc(point(), 20, RED_BAR.intValue(), RED_BAR.getDefault().intValue());
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
