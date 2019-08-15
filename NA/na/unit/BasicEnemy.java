package unit;

import core.GHQ;

public abstract class BasicEnemy extends BasicUnit{
	private static final long serialVersionUID = -5866340512027239900L;

	public BasicEnemy(int charaSize, int initialGroup) {
		super(charaSize, initialGroup);
	}
	
	@Override
	public void paint(boolean doAnimation) {
		super.paint(doAnimation);
		GHQ.paintHPArc(dynam, 20, RED_BAR.intValue(), RED_BAR.getDefault().intValue());
	}
}
