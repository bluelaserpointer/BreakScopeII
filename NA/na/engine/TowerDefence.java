package engine;

import java.awt.Color;

import core.GHQ;
import preset.unit.Unit;
import unit.NAUnit;
import vegetation.DownStair;

public class TowerDefence {
	public DownStair protectTarget;
	public boolean phasePrepareMode = true;
	public int nowPhase;
	public int phaseStartedFrame = -1;
	public int phaseClearedFrame = -1;
	
	public void nextPhase() {
		phaseStartedFrame = GHQ.nowFrame();
		phasePrepareMode = false;
		switch(++nowPhase) {
		case 0:
			break;
		case 1:
			break;
		}
	}
	public void phaseClear() {
		phasePrepareMode = true;
		phaseClearedFrame = GHQ.nowFrame();
	}
	public void idle() {
		final int phasePassedFrame = this.phasePassedFrame();
		if(phaseStartedFrame != -1 && phasePassedFrame < 100) {
			GHQ.getG2D(Color.RED, GHQ.stroke3);
			GHQ.translateForGUI(true);
			GHQ.drawStringGHQ("Phase " + nowPhase, GHQ.screenW()/2, GHQ.screenH()/2, 30F);
			GHQ.translateForGUI(false);
		} else if(phaseClearedFrame != -1 && GHQ.passedFrame(phaseClearedFrame) < 100 && nowPhase != 0) {
			GHQ.getG2D(Color.GREEN, GHQ.stroke3);
			GHQ.translateForGUI(true);
			GHQ.drawStringGHQ("Phase " + nowPhase + " Clear", GHQ.screenW()/2, GHQ.screenH()/2, 30F);
			GHQ.translateForGUI(false);
		}
		//game clear check
		if(phasePassedFrame >= 100) {
			boolean phaseClear = true;
			for(Unit rawUnit : GHQ.stage().units) {
				final NAUnit unit = (NAUnit)rawUnit;
				if(unit.isHostile(NAGame.controllingUnit())) {
					phaseClear = false;
					break;
				}
			}
			if(phaseClear) {
				this.phaseClear();
			}
		}
//		switch(nowPhase) {
//		default:
//			
//			break;
//		}
	}
	
	//information;
	public int phasePassedFrame() {
		return GHQ.passedFrame(phaseStartedFrame);
	}
}
