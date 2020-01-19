package damage;

import buff.ColdBuff;
import buff.LitBuff;
import buff.PoisonBuff;
import buff.ToughnessBroke;
import calculate.Damage;
import core.GHQ;
import effect.EffectLibrary;
import unit.NAUnit;
import unit.Unit;

public class NADamage implements Damage {
	private double value;
	private DamageMaterialType materialType;
	private DamageResourceType resourceType;
	public NADamage(double value, DamageMaterialType materialType, DamageResourceType resourceType) {
		this.value = value;
		this.materialType = materialType;
		this.resourceType = resourceType;
	}
	//main role
	@Override
	public void doDamage(Unit unit) {
		final NAUnit UNIT = (NAUnit)unit;
		//general resistance
		double res = UNIT.damageRes(this);
		value *= 1.0 - res;
		//reduce toughness except poison damage
		if(materialType != DamageMaterialType.Poi && !UNIT.containsBuff(ToughnessBroke.class) && value >= 0) {
			UNIT.TOUGHNESS.consume(value);
			//gain toughnessBroke when zero
			if(UNIT.TOUGHNESS.isMin()) {
				UNIT.addBuff(new ToughnessBroke(UNIT));
				//Gain ColdBuff if toughness is zero.
				if(materialType == DamageMaterialType.Ice)
					UNIT.addBuff(new ColdBuff(UNIT));
			}
		}
		//original effect
		switch(materialType) {
		case Heat:
			//Gain LitBuff if value is bigger than resistance*500
			if(value > res*500)
				UNIT.addBuff(new LitBuff(UNIT));
			break;
		case Ice:
			break;
		case Phy: //has critical damage
			//critical chance = CRI + underflowed REF or 100% if it has ColdBuff
			//critical effect = CRI_EFFECT + overflowed CRI
			if(UNIT.containsBuff(ColdBuff.class) ||
					Math.random() < UNIT.CRI.doubleValue() + Math.max(0.0, -UNIT.REF.doubleValue()))
				value *= UNIT.CRI_EFFECT.doubleValue() + Math.max(0.0, UNIT.CRI.doubleValue() - 1.0);
			break;
		case Poi:
			//poison damage disappear when it became smaller then RES*100 = POW_FIXED
			if(value > res*100) //get down-leveled poison buff
				UNIT.addBuff(new PoisonBuff(UNIT, (int)value));
			else
				value = 0;
			break;
		case Rea:
			break;
		}
		//reduce hit point
		final double DAMAGE_VALUE = UNIT.RED_BAR.consume(value);
		//show dmg
		GHQ.stage().addEffect(new EffectLibrary.DamageNumberEF(UNIT, DAMAGE_VALUE, materialType.color()));
	}
	//control
	public NADamage setDamage(double value) {
		this.value = value;
		return this;
	}
	public NADamage setMaterial(DamageMaterialType materialType) {
		this.materialType = materialType;
		return this;
	}
	public NADamage setMaterial(DamageResourceType resourceType) {
		this.resourceType = resourceType;
		return this;
	}
	//information
	public double damage() {
		return value;
	}
	public DamageMaterialType materialType() {
		return materialType;
	}
	public DamageResourceType resourceType() {
		return resourceType;
	}
}
