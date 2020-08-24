package item.defenceKit;

import java.awt.Color;

import core.GHQ;
import engine.NAGame;
import item.ammo.Ammo;
import item.ammo.AmmoType;
import item.ammo.storage.AmmoStorage;
import item.equipment.weapon.NAFirearms;
import paint.ImageFrame;
import physics.Point;
import preset.item.ItemData;
import preset.unit.Unit;
import storage.TableStorage;
import unit.NAUnit;
import unit.UnitGroup;
import vegetation.AmmoArea;

public class Turret extends DefenceKitOnWall {
	public Turret() {
		super(ImageFrame.create("picture/player_change_1.png"), ImageFrame.create("picture/human1-1.png"));
		super.lastOwner = NAGame.controllingUnit();
	}
	static class TurretEntity extends NAUnit {
		final Turret turret;
		public TurretEntity(Turret turret) {
			super(0);
			this.inventory = AmmoBox.storage;
			this.ammoStorage = new AmmoStorage(AmmoType.values());
			this.turret = turret;
		}
		@Override
		public UnitGroup unitGroup() {
			return ((NAUnit)turret.owner).unitGroup();
		}
		@Override
		public Point point() {
			return turret.point();
		}
	}
	final TurretEntity entity = new TurretEntity(this);
	//equipments
	protected final TableStorage<ItemData> storage = new TableStorage<>(1, 1, null);
	@Override
	public void idle() {
		super.idle();
		if(!installed())
			return;
		final ItemData item = getHoldingItem();
		if(item instanceof NAFirearms) {
			if(!item.hasOwner()) {
				System.out.println("controlled a firearm.");
				item.setOwner(entity);
			}
			final NAFirearms weapon = (NAFirearms)item;
			final AmmoType usingAmmoType = weapon.usingAmmoType();
			if(GHQ.nowFrame() % 10 == 0 && entity.ammoStorage.countByType(usingAmmoType) < 100) {
				for(AmmoArea ammoArea : AmmoArea.ammoAreaList) {
					for(Ammo ammo : ammoArea.dropItems()) {
						if(ammo.type() == usingAmmoType) {
							//TODO: also judge enchants type
							entity.addItemToStorage(ammo, true);
						}
					}
				}
			}
			weapon.weapon().idle();
			final Unit rawUnit = GHQ.stage().getNearstVisibleEnemy(lastOwner);
			if(rawUnit != null) {
				final NAUnit unit = (NAUnit)rawUnit;
				GHQ.getG2D(Color.RED, GHQ.stroke1).drawLine(point().intX(), point().intY(), unit.point().intX(), unit.point().intY());
				final double targetAngle = this.point().angleTo(unit);
				this.angle().set(targetAngle);
				entity.angle().set(targetAngle);
				weapon.use();
			}
		}
	}
	@Override
	public double weight() {
		return 100;
	}
	@Override
	public void openInfoUI() {
		NAGame.openInventoryInvester(storage);
	}
	@Override
	public void paint() {
		if(super.installed())
			paintScript.dotPaint_turn(point(), angle().get());
		else
			super.paint();
		//show it is reloading
		final NAFirearms arm = getFirearmIfHolding();
		if(arm != null && !arm.weapon().isReloadFinished()) {
			final int nowFrame = GHQ.nowFrame();
			GHQ.getG2D(Color.CYAN, GHQ.stroke3).drawArc(cx() - 25, cy() - 25, 50, 50, nowFrame*5, 60);
		}
	}
	
	//info
	public ItemData getHoldingItem() {
		return storage.get(0);
	}
	public NAFirearms getFirearmIfHolding() {
		final ItemData holdingItem = this.getHoldingItem();
		return holdingItem instanceof NAFirearms ? (NAFirearms)holdingItem : null;
	}
}
