package damage;

import java.awt.Color;
import java.awt.Font;

import buff.ColdBuff;
import buff.LitBuff;
import buff.NABuff;
import buff.PoisonBuff;
import buff.ToughnessBroke;
import calculate.Damage;
import core.GHQ;
import effect.EffectLibrary;
import unit.NAUnit;
import unit.Unit;

public class NADamage implements Damage {
	public static final NADamage NULL_DAMAGE = new NADamage(0, DamageMaterialType.Rea, DamageResourceType.Bullet);
	private double value;
	private DamageMaterialType materialType;
	private DamageResourceType resourceType;
	private double knockbackRate = 1.0;
	private double criticalAddition = 0.0;
	public NADamage(double value, DamageMaterialType materialType, DamageResourceType resourceType) {
		this.value = value;
		this.materialType = materialType;
		this.resourceType = resourceType;
	}
	public NADamage(double value, DamageMaterialType materialType) {
		this.value = value;
		this.materialType = materialType;
		this.resourceType = DamageResourceType.Bullet;
	}
	public NADamage(double value) {
		this.value = value;
		this.materialType = DamageMaterialType.Phy;
		this.resourceType = DamageResourceType.Bullet;
	}
	//main role
	@Override
	public void doDamage(Unit unit, Unit attacker) {
		final NAUnit UNIT = (NAUnit)unit;
		final NAUnit ATTACKER = (NAUnit)attacker;
		//general resistance
		double res = UNIT.damageRes(this);
		value *= 1.0 - res;
		//damage text
		String textTail = "";
		//damage text font
		Font damageTextFont = GHQ.basicFont;
		//damage text color
		Color damageTextColor = materialType.color();
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
			if(value > res*500) {
				final NABuff buff = UNIT.getBuff(LitBuff.class);
				if(buff != null)
					((LitBuff)buff).resetTime();
				else
					UNIT.addBuff(new LitBuff(UNIT));
			}
			break;
		case Ice:
			break;
		case Phy: //has critical damage
			//critical chance = CRI + underflowed REF + criticalAddition.+100% if it has ColdBuff
			//critical effect = CRI_EFFECT + overflowed CRI
			if(UNIT.containsBuff(ColdBuff.class) ||
					Math.random() < ATTACKER.CRI.doubleValue() + Math.max(0.0, -UNIT.REF.doubleValue() + criticalAddition)) {
				value *= ATTACKER.CRI_EFFECT.doubleValue() + Math.max(0.0, ATTACKER.CRI.doubleValue() - 1.0);
				textTail = "!!";
				damageTextFont = damageTextFont.deriveFont(50F);
				damageTextColor = Color.BLACK;
			}
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
		//increase damage when the target unit is not in battle stance
		if(materialType != DamageMaterialType.Rea && !((NAUnit)unit).isBattleStance()) {
			value *= 3;
			GHQ.stage().addEffect(new EffectLibrary.DamageNumberEF(UNIT, "Surprise attack!", GHQ.basicFont.deriveFont(30F), Color.BLACK));
		}
		//reduce hit point
		UNIT.RED_BAR.consume(value);
		//show dmg
		GHQ.stage().addEffect(new EffectLibrary.DamageNumberEF(UNIT, GHQ.DF0_00.format(value) + textTail, damageTextFont, damageTextColor));
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
	public NADamage setKnockbackRate(double rate) {
		knockbackRate = rate;
		return this;
	}
	public NADamage setCriticalAddition(double addition) {
		criticalAddition = addition;
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
	public double knockbackRate() {
		return knockbackRate;
	}
	public double criticalAddition() {
		return criticalAddition;
	}
}
