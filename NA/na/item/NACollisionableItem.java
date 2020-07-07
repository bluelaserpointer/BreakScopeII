package item;

import damage.HasDamageRes;
import damage.NADamage;
import paint.dot.DotPaint;
import stage.GHQStage;

public abstract class NACollisionableItem extends NAItem implements HasDamageRes {
	protected double healthPoint;
	public NACollisionableItem(DotPaint paint, double healthPoint) {
		super(paint);
		this.healthPoint = healthPoint;
	}
	@Override
	public void addedToStage(GHQStage stage) {
		stage.bulletCollisionGroup.add(this);
	}
	@Override
	public void idle() {
		super.idle();
		if(healthPoint <= 0.0 && !hasDeleteClaimFromStage()) {
			killed();
			claimDeleteFromStage();
		}
	}
	@Override
	public abstract double damageRes(NADamage damage);
	public void killed() {}
	public void reduceHealthPoint(double value) {
		healthPoint -= value;
		if(healthPoint <= 0.0) {
			killed();
			claimDeleteFromStage();
		}
	}
}
