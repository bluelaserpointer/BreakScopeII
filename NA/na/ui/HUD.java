package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import core.GHQ;
import engine.NAGame;
import item.ammo.enchant.AmmoEnchants;
import item.equipment.Equipment;
import item.equipment.weapon.NASubWeapon;
import item.equipment.weapon.reloadRule.ReloadRuleSelecter;
import item.equipment.weapon.NAFirearms;
import paint.ImageFrame;
import paint.rect.RectPaint;
import unit.NAUnit;
import weapon.Weapon;

public class HUD extends RectPaint {
	private final ImageFrame cilinderIF;
	private final ImageFrame focusIF;
	private final ImageFrame knifeFocusIF;
	private final ImageFrame bulletHeadIF;
	public HUD() {
		cilinderIF = ImageFrame.create("picture/hud/cilinder.png");
		focusIF = ImageFrame.create("picture/hud/Focus.png");
		knifeFocusIF = ImageFrame.create("picture/hud/KnifeFocus.png");
		bulletHeadIF = ImageFrame.create("picture/hud/BulletHead.png");
	}
	@Override
	public void rectPaint(int x, int y, int w, int h) {
		final Graphics2D G2 = GHQ.getG2D();
		final NAUnit player = NAGame.controllingUnit();
		//HP bars
		G2.setFont(GHQ.basicFont);
		G2.setColor(new Color(80, 80, 80));
		G2.fillRect(8, 3, 375, 70);
		G2.setColor(new Color(100, 100, 100));
		G2.fillRect(10, 5, 370, 65);
		G2.setStroke(new BasicStroke(3f));
		G2.setColor(new Color(200, 200, 200));
		G2.drawRect(10, 5, 370, 65);
		G2.setStroke(new BasicStroke(9f));
		int barLength;
		barLength = (int)(120*player.RED_BAR.getRate());
		int shieldBarLength = (int)(120*player.getShield()/(double)player.getShieldSize());
		if(barLength > 0) {
			if(shieldBarLength > 0) {
				G2.setColor(Color.CYAN);
				G2.drawLine(35, 15, 35 + shieldBarLength, 15);
				G2.setColor(Color.BLACK);
				G2.drawString("SH: " + player.getShield(), 285, 21);
				G2.setColor(Color.CYAN);
				G2.drawString("SH: " + player.getShield(), 285, 20);
				G2.setStroke(GHQ.stroke3);
				G2.setColor(Color.RED);
				G2.drawLine(35, 15, 35 + barLength, 15);
				G2.setStroke(new BasicStroke(9f));
			} else {
				G2.setColor(Color.RED);
				G2.drawLine(35, 15, 35 + barLength, 15);
			}
			G2.setColor(Color.BLACK);
			G2.drawString("HP: " + GHQ.DF0_0.format(player.RED_BAR.doubleValue()), 181, 21);
			G2.setColor(Color.RED);
			G2.drawString("HP: " + GHQ.DF0_0.format(player.RED_BAR.doubleValue()), 180, 20);
		}
		cilinderIF.dotPaint(90, 15);
		barLength = (int)(120*player.BLUE_BAR.getRate());
		if(barLength > 0) {
			G2.setColor(Color.CYAN);
			G2.drawLine(35, 30, 35 + barLength, 30);
			G2.setColor(Color.BLACK);
			G2.drawString("MP: " + GHQ.DF0_0.format(player.BLUE_BAR.doubleValue()), 181, 36);
			G2.setColor(Color.CYAN);
			G2.drawString("MP: " + GHQ.DF0_0.format(player.BLUE_BAR.doubleValue()), 180, 35);
		}
		cilinderIF.dotPaint(90, 30);
		barLength = (int)(120*player.GREEN_BAR.getRate());
		if(barLength > 0) {
			G2.setColor(Color.GREEN);
			G2.drawLine(35, 45, 35 + barLength, 45);
			G2.setColor(Color.BLACK);
			G2.drawString("ST: " + GHQ.DF0_0.format(player.GREEN_BAR.doubleValue()), 181, 51);
			G2.setColor(Color.GREEN);
			G2.drawString("ST: " + GHQ.DF0_0.format(player.GREEN_BAR.doubleValue()), 180, 50);
		}
		cilinderIF.dotPaint(90, 45);
		barLength = (int)(120*player.WHITE_BAR.getRate());
		if(barLength > 0) {
			G2.setColor(Color.WHITE);
			G2.drawLine(35, 60, 35 + barLength, 60);
			G2.setColor(Color.BLACK);
			G2.drawString("FO: " + GHQ.DF0_0.format(player.WHITE_BAR.doubleValue()), 181, 66);
			G2.setColor(Color.WHITE);
			G2.drawString("FO: " + GHQ.DF0_0.format(player.WHITE_BAR.doubleValue()), 180, 65);
		}
		cilinderIF.dotPaint(90, 60);
		//playerIcon
		int pos = 1;
		if(player.personalIcon != null)
			player.personalIcon.rectPaint(pos++*90 + 10, GHQ.screenH() - 40, 80, 30);
		//focus and magazine preview
		if(player.isBattleStance()) {
			final Equipment equipment = player.currentEquipment();
			final Weapon weapon = player.currentWeapon();
			final int mouseX = GHQ.mouseX(), mouseY = GHQ.mouseY();
			if(equipment instanceof NAFirearms) {
				//focus
				final NAFirearms firearm = (NAFirearms)equipment;
				GHQ.translateForGUI(false);
				G2.setColor(new Color(200,120,10,100));
				G2.setStroke(GHQ.stroke3);
				G2.drawLine(player.point().intX(), player.point().intY(), mouseX, mouseY);
				if(weapon.isReloadFinished())
					focusIF.dotPaint(mouseX, mouseY);
				else
					focusIF.dotPaint_turn(mouseX, mouseY, GHQ.nowFrame()/10);
				GHQ.translateForGUI(true);
				//magazine preview
				final int infoX = w - 240, infoY = h - 100, infoW = 230, infoH = 100;
				G2.setColor(Color.GRAY);
				G2.fillRect(infoX, infoY, infoW, infoH);
				final int magazineSize = weapon.magazineSize();
				final double bulletIconW = (double)infoW/magazineSize;
				G2.setStroke(GHQ.stroke1);
				int ammoPos = -1;
				for(AmmoEnchants enchants : firearm.magazineContents()) {
					++ammoPos;
					bulletHeadIF.rectPaint(infoX + (int)(bulletIconW*ammoPos) + 1, infoY, (int)bulletIconW - 2, 30);
					G2.setColor(enchants.enchantsColor());
					G2.fillRect(infoX + (int)(bulletIconW*ammoPos) + 1, infoY + 30, (int)bulletIconW - 2, 70);
					G2.setColor(Color.BLACK);
					G2.drawRect(infoX + (int)(bulletIconW*ammoPos) + 1, infoY + 30, (int)bulletIconW - 2, 70);
				}
				//reload gauge
				if(!weapon.isReloadFinished()) {
					G2.setColor(new Color(200, 200, 200, 100));
					G2.fillRect(infoX, infoY, (int)(infoW*(double)weapon.getReloadProgress()/weapon.reloadTime()), infoH);
				}
				//reload rule
				if(player.openReloadRule()) {
					final int bOvalSize = 60;
					final int sOvalSize = 45;
					final ReloadRuleSelecter selecter = player.reloadRuleSelecter();
					final int infoCX = infoX + infoW/2;
					GHQ.getG2D(Color.GRAY).fillOval(infoCX - bOvalSize/2, infoY - 40 - bOvalSize/2, bOvalSize, bOvalSize);
					selecter.getRule(0).getDotPaint().dotPaint(infoCX, infoY - 40);
					GHQ.getG2D(Color.GRAY).fillOval(infoCX - 50 - sOvalSize/2, infoY - 30 - sOvalSize/2, sOvalSize, sOvalSize);
					selecter.getRule(-1).getDotPaint().dotPaint(infoCX - 50, infoY - 30);
					GHQ.getG2D(Color.GRAY).fillOval(infoCX + 50 - sOvalSize/2, infoY - 30 - sOvalSize/2, sOvalSize, sOvalSize);
					selecter.getRule(+1).getDotPaint().dotPaint(infoCX + 50, infoY - 30);
					GHQ.getG2D(Color.GRAY).fillOval(infoCX - 90 - sOvalSize/2, infoY - 15 - sOvalSize/2, sOvalSize, sOvalSize);
					selecter.getRule(-2).getDotPaint().dotPaint(infoCX - 90, infoY - 15);
					GHQ.getG2D(Color.GRAY).fillOval(infoCX + 90 - sOvalSize/2, infoY - 15 - sOvalSize/2, sOvalSize, sOvalSize);
					selecter.getRule(+2).getDotPaint().dotPaint(infoCX + 90, infoY - 15);
				}
			} else if(equipment instanceof NASubWeapon) {
				knifeFocusIF.dotPaint_turn(GHQ.mouseScreenX(), GHQ.mouseScreenY(), player.point().angleTo(mouseX, mouseY));
			}
		}
	}
}
