package vegetation;

import java.util.function.Supplier;

import core.GHQ;
import engine.NAGame;
import paint.ImageFrame;
import preset.vegetation.Vegetation;
import unit.NAUnit;

public class EnemyGate extends Vegetation {
	protected final Supplier<NAUnit> supplier;
	protected int summonLeft;
	public EnemyGate(Supplier<NAUnit> supplier, int x, int y) {
		super(ImageFrame.create("picture/map/orangeTarget.png"), x, y);
		this.supplier = supplier;
	}
	@Override
	public void idle() {
		super.idle();
		if(NAGame.towerDefence.phasePrepareMode)
			return;
		if(summonLeft > 0 && GHQ.nowFrame() % 50 == 0) {
			--summonLeft;
			GHQ.stage().addUnit(supplier.get());
		}
	}
	public EnemyGate addAmount(int amount) {
		summonLeft += amount;
		return this;
	}
//	public EnemyGate doSummon() {
//		return this.doSummon(true);
//	}
//	public EnemyGate stopSummon() {
//		return this.doSummon(false);
//	}
}
