package unit;

import core.GHQ;

public abstract class BasicPlayer extends BasicUnit{
	private static final long serialVersionUID = -5939573262134727671L;
	public BasicPlayer(int charaSize, int initialGroup) {
		super(charaSize, initialGroup);
	}
	@Override
	public void idle() {
		super.idle();
		final int mouseX = GHQ.getMouseX(), mouseY = GHQ.getMouseY();
		////////////
		//main
		////////////
		// dodge
		if (dodgeOrder)
			dodge(mouseX, mouseY);
		// attack
		if (attackOrder) {
			mainWeapon.trigger(this);
		}
		// spell
		if (spellOrder) {
			spellWeapon.trigger(this);
		}
		dynam.approachIfNoObstacles(this, dstPoint, charaSpeed);
	}
}
