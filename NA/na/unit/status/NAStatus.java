package unit.status;

import java.util.HashMap;

import calculate.Consumables;
import damage.DamageMaterial;
import damage.NADamage;
import unit.Status;

public abstract class NAStatus<StatusValue extends Consumables> extends Status<StatusType, StatusValue> {
	protected NADamage lastDamage;
	protected final HashMap<DamageMaterial, Double> damageResMap = new HashMap<DamageMaterial, Double>();
	{
		damageResMap.put(DamageMaterial.Heat, 0.0);
		damageResMap.put(DamageMaterial.Cold, 0.0);
		damageResMap.put(DamageMaterial.Phy, 0.0);
		damageResMap.put(DamageMaterial.Poi, 0.0);
	}
	//information
	public HashMap<DamageMaterial, Double> damageResMap() {
		return damageResMap;
	}
}
