package damage;

import java.awt.Color;
import java.util.LinkedList;

import buff.ColdBuff;
import buff.LitBuff;
import buff.NABuff;
import buff.PoisonBuff;
import buff.ToughnessBroke;
import calculate.Damage;
import core.GHQ;
import core.GHQObject;
import effect.EffectLibrary;
import item.NACollisionableItem;
import item.NAItem;
import unit.NAUnit;

public class NADamage extends Damage {
	public static final NADamage NULL_DAMAGE = new NADamage();
	protected LinkedList<DamageComposition> compositions = new LinkedList<>();
	protected DamageComposition dealingComposition = null;
	private double knockbackRate;
	private boolean doSurpriseAttack = true;
	private boolean isCritical;
	private double criticalAddition = 0.0;
	public NADamage(DamageComposition...compositions) {
		for(DamageComposition composition : compositions) {
			this.compositions.add(composition);
		}
	}
	@SuppressWarnings("unchecked")
	public NADamage(NADamage sample) {
		super(sample);
		compositions = (LinkedList<DamageComposition>) sample.compositions.clone();
		knockbackRate = sample.knockbackRate;
		doSurpriseAttack = sample.doSurpriseAttack;
		isCritical = sample.isCritical;
		criticalAddition = sample.criticalAddition;
	}
	public NADamage(double value) {
		compositions.add(DamageMaterial.Phy.makeComposition(value));
	}
	//init
	public NADamage setKnockbackRate(double strengthRate) {
		knockbackRate = strengthRate;
		return this;
	}
	public NADamage setDoSurpriseAttack(boolean doSurpriseAttack) {
		this.doSurpriseAttack = doSurpriseAttack;
		return this;
	}
	//main role
	@Override
	public void doDamage(GHQObject object) {
		if(object.isUnit())
			this.doDamage((NAUnit)object);
		else if(object.isItemData())
			this.doDamage((NAItem)object);
	}
	protected void doDamage(NAItem item) {
		if(item instanceof NACollisionableItem) {
			final NACollisionableItem target = (NACollisionableItem)item;
			for (DamageComposition composition : compositions) {
				target.reduceHealthPoint(composition.value);
				//show dmg
				this.makeEffect(target, composition);
			}
		}
	}
	protected void doDamage(NAUnit targetUnit) {
		final NAUnit ATTACKER = (attacker instanceof NAUnit) ? (NAUnit)attacker : null;
		//tell attacker damaged target
		if(ATTACKER != null) {
			ATTACKER.damagedTarget(targetUnit, this);
		}
		targetUnit.setLastDamage(this);
		//general resistance
		for (DamageComposition composition : compositions) {
			double value = composition.value;
			final DamageMaterial dmgMaterial = composition.damageMaterial;
			final double res = targetUnit.damageRes(dmgMaterial);
			value *= 1.0 - res;
			//increase damage when the unit is not in battle stance
			if(doSurpriseAttack && !dmgMaterial.isReal() && !targetUnit.isBattleStance()) {
				value *= 3;
				//GHQ.stage().addEffect(new EffectLibrary.DamageNumberEF(targetUnit, "Surprise attack!", GHQ.basicFont.deriveFont(30F), Color.BLACK));
			}
			//resistance
			switch(dmgMaterial) {
			case Phy: //has critical damage
				//critical chance = CRI + underflowed REF + criticalAddition.+100% if it has ColdBuff
				//critical effect = CRI_EFFECT + overflowed CRI
				if(targetUnit.containsBuff(ColdBuff.class) || ATTACKER != null &&
						Math.random() < ATTACKER.CRI.doubleValue() + Math.max(0.0, -targetUnit.REF.doubleValue() + criticalAddition)) {
					value *= ATTACKER.CRI_EFFECT.doubleValue() + Math.max(0.0, ATTACKER.CRI.doubleValue() - 1.0);
					isCritical = true;
				}
				break;
			default:
				break;
			}
			//reduce hit point
			dealingComposition = composition;
			targetUnit.RED_BAR.clearLastSet();
			final double oldDmg = composition.damage();
			//show dmg
			composition.setDamage(targetUnit.RED_BAR.consume(value));
			this.makeEffect(targetUnit, composition);
			composition.setDamage(oldDmg);
			//after effect
			if(value > 0) {
				//reduce toughness except poison damage
				if(dmgMaterial.isCold() || !dmgMaterial.isPoison() && !targetUnit.containsBuff(ToughnessBroke.class)) {
					targetUnit.TOUGHNESS.consume(value);
					if(targetUnit.TOUGHNESS.isMin()) { //gain toughnessBroke at zero toughness
						targetUnit.addBuff(new ToughnessBroke(targetUnit));
						if(dmgMaterial.isCold()) //Gain ColdBuff if it was cold damage
							targetUnit.addBuff(new ColdBuff(targetUnit));
					}
				}
				switch(dmgMaterial) {
				case Heat:
					//Gain LitBuff if value is bigger than resistance*500
					if(value > res*500) {
						final NABuff buff = targetUnit.getBuff(LitBuff.class);
						if(buff != null)
							((LitBuff)buff).resetTime();
						else
							targetUnit.addBuff(new LitBuff(targetUnit));
					}
					break;
				case Cold:
					break;
				case Phy:
					targetUnit.point().addSpeed_DA(knockbackRate*value/targetUnit.WEIGHT.doubleValue()*(targetUnit.containsBuff(ToughnessBroke.class) ? 40 : 20), attackerBullet.point().moveAngle());		
					targetUnit.body().damaged.set();
					break;
				case Poi:
					//poison damage disappear when it became smaller then RES*100 = POW_FIXED
					if(value > res*100) //get down-leveled poison buff
						targetUnit.addBuff(new PoisonBuff(targetUnit, (int)value));
					else
						value = 0;
					break;
				case Rea:
					break;
				default:
					break;
				}
				//to battle stance
				targetUnit.setBattleStance(true);
			}
			//judge alive
			if(!targetUnit.isAlive()) {
				targetUnit.killed();
				break;
			}
		}
	}
	
	//control
	public NADamage setCriticalAddition(double addition) {
		criticalAddition = addition;
		return this;
	}
	public NADamage addDamageComposition(DamageComposition composition) {
		final DamageComposition duplicatedComposition = findComposition(composition.damageMaterial, composition.fromInside);
		if(duplicatedComposition != null) {
			duplicatedComposition.value += composition.value;
		} else {
			compositions.add(composition);
		}
		return this;
	}
	public NADamage addDamageComposition(NADamage damage) {
		for(DamageComposition composition : damage.compositions) {
			addDamageComposition(composition);
		}
		return this;
	}
	//information
	@Override
	public NADamage clone() {
		return new NADamage(this);
	}
	public DamageComposition dealingComposition() {
		return dealingComposition;
	}
	public double knockbackRate() {
		return knockbackRate;
	}
	public double criticalAddition() {
		return criticalAddition;
	}
	public boolean isCritical() {
		return isCritical;
	}
	public void makeEffect(GHQObject target, DamageComposition composition) {
		final double value = composition.value;
		if(value != 0.0) {
			final String text = value > 1.0 ? GHQ.DF0_0.format(-value) : String.valueOf((int)-value);
			if(isCritical)
				text.concat("!!");
			GHQ.stage().addEffect(new EffectLibrary.DamageNumberEF(target, text, isCritical ? GHQ.commentFont.deriveFont(50F) : GHQ.commentFont, isCritical ? Color.BLACK : composition.damageMaterial.color()));
		}
	}
	public void makeEffect(GHQObject target) {
		makeEffect(target, dealingComposition);
	}
	public DamageComposition findComposition(DamageMaterial material) {
		for(DamageComposition composition : compositions) {
			if(composition.material().equals(material)) {
				return composition;
			}
		}
		return null;
	}
	public DamageComposition findComposition(DamageMaterial material, boolean fromInside) {
		for(DamageComposition composition : compositions) {
			if(composition.material().equals(material) && composition.fromInside == fromInside) {
				return composition;
			}
		}
		return null;
	}
	public LinkedList<DamageComposition> compositions() {
		return compositions;
	}
}
