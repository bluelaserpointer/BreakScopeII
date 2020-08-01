package item.ammo.storage;

import java.awt.Color;
import java.util.LinkedList;

import core.GHQ;
import item.ammo.Ammo;
import item.ammo.enchant.AmmoEnchants;
import item.equipment.weapon.reloadRule.AcceptOneReloadRule;
import paint.ImageFrame;
import paint.dot.DotPaint;
import paint.dot.HasDotPaint;

/**
 * When the ammo contained inside is empty, this bag will automatically removed from its origin-list.
 * @author bluelaserpointer
 *
 */
public class AmmoBag implements HasDotPaint {
	public static final AmmoBag EMPTY_BAG = new AmmoBag(null, null) {
		@Override
		public void add(int amount) {}
		@Override
		public int consume(int amount) {
			return 0;
		}
		@Override
		public int getAmount() {
			return 0;
		}
		@Override
		public boolean isEmpty() {
			return true;
		}
		@Override
		public boolean stackable(Ammo ammo) {
			return false;
		}
		@Override
		public AmmoEnchants enchants() {
			return null;
		}
		@Override
		public DotPaint ammoEnchantsPaint() {
			return new DotPaint() {
				private final DotPaint bulletHeadPaint = ImageFrame.create("picture/icon/reloadRule/bulletHead.png");
				@Override
				public void dotPaint(int x, int y) {
					bulletHeadPaint.dotPaint(x, y - 20);
					final int BHW = bulletHeadPaint.width(), BHH = bulletHeadPaint.height();
					GHQ.getG2D(Color.WHITE).fillRect(x - BHW/2, y - 20 + BHH/2, BHW, 40);
					GHQ.getG2D(Color.BLACK, GHQ.stroke3).drawRect(x - BHW/2, y - 20 + BHH/2, BHW, 40);
					GHQ.getG2D(Color.BLACK).drawLine(x + BHW/2, y - 20 + BHH/2, x - BHW/2, y + 20 + BHH/2);
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
		}
	};
	
	private final LinkedList<AmmoBag> originList;
	private final Ammo ammo;
	private final AcceptOneReloadRule specialReloadRule; 
	public AmmoBag(LinkedList<AmmoBag> origin, Ammo ammo) {
		this.originList = origin;
		this.ammo = ammo;
		specialReloadRule = new AcceptOneReloadRule(this);
	}
	
	//control
	public void add(int amount) {
		ammo.add(amount);
	}
	public int consumeWithAutoRemoveIfEmpty(int amount) {
		final int consumed = consume(amount);
		if(ammo.isEmpty())
			originList.remove(this);
		return consumed;
	}
	public int consume(int amount) {
		return ammo.consume(amount);
	}
	
	//information
	public Ammo ammo() {
		return ammo;
	}
	public int getAmount() {
		return ammo.getAmount();
	}
	public boolean isEmpty() {
		return ammo.isEmpty();
	}
	public boolean stackable(Ammo ammo) {
		return ammo.stackable(ammo);
	}
	public AmmoEnchants enchants() {
		return ammo.enchants();
	}
	public DotPaint ammoEnchantsPaint() {
		return ammo.enchants().getDotPaint();
	}
	public AcceptOneReloadRule specialReloadRule() {
		return specialReloadRule;
	}

	@Override
	public DotPaint getDotPaint() {
		return ammo.getDotPaint();
	}
}
