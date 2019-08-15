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
		final int mouseX = GHQ.mouseX(), mouseY = GHQ.mouseY();
		////////////
		//main
		////////////
		// dodge
		if (dodgeOrder)
			dodge(mouseX, mouseY);
		// attack
		if (attackOrder)
			mainSlot.use();
		// spell
		if (spellOrder) {
			spellWeapon.use();
		}
	}
}
