package item.defenceKit;

import java.awt.Color;

import core.GHQ;
import engine.NAGame;
import item.equipment.weapon.NAWeaponEquipment;
import paint.ImageFrame;
import physics.Angle;
import physics.Point;
import preset.item.ItemData;
import preset.unit.Unit;
import storage.TableStorage;
import unit.NAUnit;
import unit.UnitGroup;

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
			this.ammoStorage = AmmoBox.ammoStorage;
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
		final ItemData item = storage.get(0);
		if(item instanceof NAWeaponEquipment) {
			if(!item.hasOwner()) {
				System.out.println("controlled a firearm.");
				item.setOwner(entity);
			}
			final Unit rawUnit = GHQ.stage().getNearstVisibleEnemy(lastOwner);
			if(rawUnit != null) {
				final NAUnit unit = (NAUnit)rawUnit;
				GHQ.getG2D(Color.RED, GHQ.stroke1).drawLine(point().intX(), point().intY(), unit.point().intX(), unit.point().intY());
				final NAWeaponEquipment weapon = (NAWeaponEquipment)item;
				this.angle().set(this.point().angleTo(unit));
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
	}
}
