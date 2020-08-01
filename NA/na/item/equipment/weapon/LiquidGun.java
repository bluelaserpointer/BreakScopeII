package item.equipment.weapon;

import java.util.LinkedList;
import java.util.List;

import core.GHQ;
import core.GHQObject;
import damage.NADamage;
import engine.NAGame;
import item.equipment.weapon.gripStyle.RifleGrip;
import liquid.HasLiquid;
import liquid.Liquid;
import liquid.NALiquidState;
import liquid.Water;
import paint.ImageFrame;
import physics.HitGroup;
import preset.bullet.Bullet;
import unit.NAUnit;
import weapon.Weapon;

public class LiquidGun extends NAMainWeapon {
	public LiquidGun() {
		super(ImageFrame.create("picture/weapon/LiquidGun.png"));
		bulletPaint = ImageFrame.create("picture/bullet/WaterSplash.png");
		super.setGripStyle(new RifleGrip() {
			@Override
			public int[] handXPositions() {
				return new int[] {2, 22};
			}
			@Override
			public int[] handYPositions() {
				return new int[] {4, 3};
			}
		});
	}
	protected HasLiquid liquidSource;
	//main role
	private static final double FLOW_SPEED = 50;
	private final ImageFrame bulletPaint;
	@Override
	protected Weapon def_weapon() {
		return new Weapon() {
			{
				name = "LiquidGun";
			}
			@Override
			public List<Bullet> setBullets(GHQObject shooter, HitGroup standpoint) {
				liquidSource.divideLiquid(FLOW_SPEED);
				final int splashAmount = 10;
				final double waterDepth = FLOW_SPEED/splashAmount;
				final LinkedList<Bullet> firedBullets = new LinkedList<>();
				for(int i = 0; i < splashAmount; ++i) {
					firedBullets.add(GHQ.stage().addBullet(new Bullet(owner) {
						{
							name = "WaterSplash";
							setDamage(NADamage.NULL_DAMAGE);
							point().setSpeed_DA(15 + Math.random()*10, point().moveAngle() + Math.random()*1.0);
							point().addXY_allowsMoveAngle(0, owner.width());
							this.limitRange = 300 + Math.random()*300;
							paintScript = bulletPaint;
						}
						@Override
						public boolean hitObjectDeleteCheck(GHQObject object) {
							if(object.isUnit()) {
								final double weight = 20*((NAUnit)object).WEIGHT.doubleValue();
								object.point().addSpeed(point().xSpeed()/weight, point().ySpeed()/weight);
							}
							return super.hitObjectDeleteCheck(object);
						}
						@Override
						public void hitObject(GHQObject object) {
							super.hitObject(object);
							explode();
						}
						@Override
						public boolean outOfRange() {
							super.outOfRange();
							explode();
							return true;
						}
						private void explode() {
							point().addXY(point().xSpeed()*(1.0 + Math.random())*3, point().ySpeed()*(1.0 + Math.random())*3);
							NAGame.stage().addLiquid(point(), Water.FIXED_WATER_TAG, NALiquidState.WATER_SOLUABLE, waterDepth);
						}
					}));
				}
				return firedBullets;
			}
			@Override
			public double getLeftAmmo() {
				if(liquidSource != null) {
					final Liquid liquid = liquidSource.liquid();
					if(liquid != null)
						return liquid.depth();
				}
				return 0.0;
			}
			@Override
			public void consumeAmmo(double value) {}
		};
	}
	
	//control
	public void setLiquidSource(HasLiquid liquidSource) {
		this.liquidSource = liquidSource;
	}
	
	//information
	public HasLiquid liquidSource() {
		return liquidSource;
	}
	@Override
	public double weight() {
		return 3;
	}
}
