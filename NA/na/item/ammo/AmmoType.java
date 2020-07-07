package item.ammo;

import calculate.Filter;
import item.ItemData;
import paint.ImageFrame;
import paint.dot.DotPaint;

public enum AmmoType {
	_45acp	("Ammo_45acp"		, 0.015		, ImageFrame.create("picture/HandgunBullet.png")),
	_7d62	("Ammo_7.62*39mm"	, 0.0148	, ImageFrame.create("picture/ammo/7d62.png")),
	_9mm	("Ammo_9mm"			, 0.00745	, ImageFrame.create("picture/AssaultRifleBullet.png")),
	_12Gauge("Ammo_12Gauge"		, 0.045		, ImageFrame.create("picture/Shot.png"));
	
	public static final int TYPE_AMOUNT = AmmoType.values().length;
	public final String name;
	public final double weight;
	public final DotPaint paint;
	public final Filter<ItemData> filter;
	private AmmoType(String name, double weight, DotPaint paint) {
		this.name = name;
		this.weight = weight;
		this.paint = paint;
		this.filter = new Filter<ItemData>() {
			@Override
			public boolean judge(ItemData object) {
				return object instanceof Ammo && ((Ammo)object).isType(AmmoType.this);
			}
		};
	}
	public Ammo generate(int amount) {
		return new Ammo(this, amount);
	}
}
