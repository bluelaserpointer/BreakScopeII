package item.ammo.enchant;

import java.awt.Color;
import java.util.HashMap;

import core.GHQ;
import core.GHQObject;
import damage.NADamage;
import paint.ImageFrame;
import paint.dot.DotPaint;
import paint.dot.HasDotPaint;
import preset.bullet.Bullet;

public class AmmoEnchants implements HasDotPaint {
	public static final AmmoEnchants NO_ENCHANTS = new AmmoEnchants();
	final HashMap<AmmoEnchant, Integer> enchants = new HashMap<AmmoEnchant, Integer>();
	NADamage damageAdd = new NADamage();
	Color enchantsColor = Color.WHITE;
	public AmmoEnchants(AmmoEnchants sample) {
		for(AmmoEnchant enchant : sample.enchants.keySet())
			enchants.put(enchant.clone(), sample.enchants.get(enchant));
		damageAdd = sample.damageAdd.clone();
		enchantsColor = sample.enchantsColor;
	}
	public AmmoEnchants() {}
	private final DotPaint iconPaint = new DotPaint() {
		private final DotPaint bulletHeadPaint = ImageFrame.create("picture/icon/reloadRule/bulletHead.png");
		@Override
		public void dotPaint(int x, int y) {
			bulletHeadPaint.dotPaint(x, y - 20);
			final int BHW = bulletHeadPaint.width(), BHH = bulletHeadPaint.height();
			GHQ.getG2D(enchantsColor).fillRect(x - BHW/2, y - 20 + BHH/2, BHW, 40);
			GHQ.getG2D(Color.BLACK, GHQ.stroke3).drawRect(x - BHW/2, y - 20 + BHH/2, BHW, 40);
		}
		@Override
		public int width() {
			return 0;
		}
		@Override
		public int height() {
			return 0;
		}
	};
	
	//main role
	public void applyFireEffect(Bullet bullet) {
		for(AmmoEnchant enchant : enchants.keySet()) {
			enchant.whenFire(bullet, enchants.get(enchant));
		}
	}
	public void applyHitObjectEffect(Bullet bullet, GHQObject target) {
		for(AmmoEnchant enchant : enchants.keySet())
			enchant.whenHit(bullet, target, enchants.get(enchant));
	}
	
	//control
	public void addEnchant(AmmoEnchant enchant, int level) {
		if(enchants.put(enchant, level) == null)
			updateEnchantsColor();
		enchant.whenApply(damageAdd, level);
	}
	public void removeEnchant(AmmoEnchant enchant) {
		if(enchants.remove(enchant) != null)
			updateEnchantsColor();
	}
	
	//tool
	protected void updateEnchantsColor() {
		int sumR = 0, sumG = 0, sumB = 0;
		final int total = enchants.size();
		if(total == 0) {
			enchantsColor = Color.WHITE;
		} else {
			for(AmmoEnchant enchant : enchants.keySet()) {
				final Color color = enchant.color;
				sumR += color.getRed();
				sumG += color.getGreen();
				sumB += color.getBlue();
			}
			enchantsColor = new Color(sumR/total, sumG/total, sumB/total);
		}
	}
	
	//information
	public AmmoEnchants clone() {
		return new AmmoEnchants(this);
	}
	public HashMap<AmmoEnchant, Integer> enchants() {
		return enchants;
	}
	public Color enchantsColor() {
		return enchantsColor;
	}
	public boolean equals(AmmoEnchants ammoEnchants) {
		return enchants.equals(ammoEnchants.enchants);
	}
	@Override
	public DotPaint getDotPaint() {
		return iconPaint;
	}
	public NADamage damageAdd() {
		return damageAdd;
	}
}
