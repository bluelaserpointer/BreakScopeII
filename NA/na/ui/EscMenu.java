package ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import core.GHQ;
import engine.NAGame;
import gui.ArrangedButtons;
import gui.AutoResizeMenu;
import gui.BasicButton;
import gui.GHQTextArea;
import gui.GUIParts;
import gui.GUIPartsSwitcher;
import gui.ItemStorageViewer;
import gui.ScrollBar;
import gui.TableStorageViewer;
import gui.TextButton;
import item.ammo.AmmoType;
import item.ammo.enchant.AmmoEnchant;
import item.ammo.storage.AmmoBag;
import math.SquareCellArranger;
import paint.ColorFilling;
import paint.ColorFraming;
import paint.ImageFrame;
import paint.dot.HasDotPaint;
import paint.rect.RectPaint;
import preset.item.ItemData;
import preset.unit.BodyParts;
import storage.TableStorage;
import talent.Talent;
import unit.body.HumanBody;

public class EscMenu extends GUIPartsSwitcher {
	private static final int INVENTORY = 0, STATUS = 1, CRAFT = 2, SYSTEM = 3, MEMO = 4, TALENT = 5;
	private final ImageFrame defaultSlotPaint, buttonPaint;
	private final RectPaint bgPaint;
	public EscMenu() {
		super(6, INVENTORY);
		defaultSlotPaint = ImageFrame.create("picture/gui/Bag_item.png");
		buttonPaint = ImageFrame.create("picture/gui/Button_Mid_up.png");
		bgPaint = new RectPaint() {
			private final ImageFrame
				bg1 = ImageFrame.create("picture/gui/Bag_background_93.png"),
				bg2 = ImageFrame.create("picture/gui/Bag_decoration.png");
			@Override
			public void rectPaint(int x, int y, int w, int h) {
				bg1.rectPaint(x, y, w, h);
				bg2.rectPaint(x, y, w, h);
			}
		};
		this.setBGPaint(bgPaint);
		//top tab
		this.appendLast(new AutoResizeMenu(0, 0, GHQ.screenW(), 70) {
			final GUIParts 
				inventoryScrBtn = getSwitcherButton(INVENTORY).setBGPaint(buttonPaint).setName("inventoryScrBtn"),
				talentScrBtn = getSwitcherButton(TALENT).setBGPaint(buttonPaint).setName("talentScrBtn"),
				statusScrBtn = getSwitcherButton(STATUS).setBGPaint(buttonPaint).setName("paraScrBtn"),
				craftScrBtn = getSwitcherButton(CRAFT).setBGPaint(buttonPaint).setName("craftScrBtn"),
				sysScrBtn = getSwitcherButton(SYSTEM).setBGPaint(buttonPaint).setName("sysScrBtn"),
				memoScrBtn = getSwitcherButton(MEMO).setBGPaint(buttonPaint).setName("memoScrBtn");
			{
				this.addNewLine(statusScrBtn, talentScrBtn, inventoryScrBtn, craftScrBtn, sysScrBtn, memoScrBtn);
				this.setName("ESC_MENU_TOP_TAB");
			}
		});
		//INVENTORY
		set(INVENTORY, new GUIParts() {
			final ImageFrame humanBodyIF = ImageFrame.create("picture/humanbody/FullBody.png");
			AmmoType openedAmmoType = null;
			TableStorageViewer<AmmoBag> ammoStorageViewer;
			AutoResizeMenu ammoEnchantsMenu;
			{
				setName("INVENTORY");
				//item storage
				addLast(new ItemStorageViewer().setRCMenu(new ItemRCMenu_inventory()).setTableStorage((TableStorage<ItemData>)NAGame.controllingUnit().inventory).setCellPaint(defaultSlotPaint).setCellSize(70))
				.point().setXY(145, 185);
				//ammo storage
				addLast(new ArrangedButtons<AmmoType>(530, 200, new SquareCellArranger(1, 50, 50*AmmoType.TYPE_AMOUNT, 1, AmmoType.TYPE_AMOUNT)) {
					{
						final AmmoType types[] = AmmoType.values();
						for(int i = 0; i < types.length; ++i) {
							super.appendButton(types[i], defaultSlotPaint, 0, types.length - 1 - i);
						}
					}
					@Override
					protected void buttonClicked(AmmoType buttonValue) { //TODO: open UI info about the ammoBagList
						if(openedAmmoType != buttonValue) {
							openedAmmoType = buttonValue;
							ammoStorageViewer.setTableStorage(new TableStorage<AmmoBag>(NAGame.controllingUnit().ammoStorage.ammoBagList(buttonValue), 5, AmmoBag.EMPTY_BAG));
							ammoStorageViewer.enable();
						} else {
							ammoStorageViewer.disable();
						}
					}
					@Override
					protected void buttonExtendPaint(AmmoType buttonValue, BasicButton button) {
						buttonValue.paint.dotPaint(button.point().intX(), button.point().intY());
						GHQ.getG2D(Color.GRAY);
						GHQ.drawStringGHQ(String.valueOf(NAGame.controllingUnit().ammoStorage.countByType(buttonValue)), button.point().intX() + 20, button.point().intY() + 40);
					}
				});
				addLast(ammoStorageViewer = new TableStorageViewer<AmmoBag>() {
					{
						point().setXY(500, 100);
						this.backGroundPaint = new ColorFilling(Color.WHITE);
						this.cellPaint = new ColorFraming(Color.GRAY, GHQ.stroke1);
						addLast(ammoEnchantsMenu = new AutoResizeMenu(300, 20)).disable();
					}
					@Override
					public void mouseOver() {
						final AmmoBag ammoBag = this.getMouseHoveredElement();
						if(ammoBag != null) {
							ammoEnchantsMenu.enable();
							ammoEnchantsMenu.removeAll();
							final HashMap<AmmoEnchant, Integer> enchantLv = ammoBag.enchants().enchants();
							for(AmmoEnchant enchant : enchantLv.keySet()) {
								ammoEnchantsMenu.addNewLine(new GUIParts() {
									@Override
									public void paint() {
										super.paint();
										GHQ.getG2D(Color.GRAY);
										GHQ.drawStringGHQ(enchant.name, point().intX(), point().intY());
									}
								}).setBGColor(Color.LIGHT_GRAY);
							}
						}
					}
					@Override
					public void mouseOut() {
						super.mouseOut();
						ammoEnchantsMenu.disable();
					}
					@Override
					public AmmoBag objectToT(Object object) {
						if(object instanceof AmmoBag)
							return (AmmoBag)object;
						return null;
					}
					
				}).disable();
				//equipments
				//left 5 slot (main, sub, melee, shield, exoskeleton)
				addLast(new EquipmentSlot() {
					@Override
					public BodyParts targetBodyParts() {
						return NAGame.controllingUnit().body().mainEquipSlot();
					}
				}).setName("主武器").setBounds(660, 170, 80, 80);
				addLast(new EquipmentSlot() {
					@Override
					public BodyParts targetBodyParts() {
						return NAGame.controllingUnit().body().subEquipSlot();
					}
				}).setName("副武器").setBounds(660, 250, 80, 80);
				addLast(new EquipmentSlot() {
					@Override
					public BodyParts targetBodyParts() {
						return NAGame.controllingUnit().body().melleEquipSlot();
					}
				}).setName("近战武器").setBounds(660, 330, 80, 80);
				addLast(new EquipmentSlot() {
					@Override
					public BodyParts targetBodyParts() {
						return NAGame.controllingUnit().body().shieldSlot();
					}
				}).setName("盾牌").setBounds(660, 410, 80, 80);
				addLast(new EquipmentSlot() {
					@Override
					public BodyParts targetBodyParts() {
						return NAGame.controllingUnit().body().exoskeletonSlot();
					}
				}).setName("外骨骼").setBounds(660, 490, 80, 80);
				//right 5 slot (head, trunk, hands, legs, foots)
				addLast(new EquipmentSlot() {
					@Override
					public BodyParts targetBodyParts() {
						return ((HumanBody)NAGame.controllingUnit().body()).head();
					}
				}).setName("帽子").setBounds(955, 170, 80, 80);
				addLast(new EquipmentSlot() {
					@Override
					public BodyParts targetBodyParts() {
						return ((HumanBody)NAGame.controllingUnit().body()).trunk();
					}
				}).setName("衣服").setBounds(955, 250, 80, 80);
				addLast(new EquipmentSlot() {
					@Override
					public BodyParts targetBodyParts() {
						return ((HumanBody)NAGame.controllingUnit().body()).hands();
					}
				}).setName("手套").setBounds(955, 330, 80, 80);
				addLast(new EquipmentSlot() {
					@Override
					public BodyParts targetBodyParts() {
						return ((HumanBody)NAGame.controllingUnit().body()).legs();
					}
				}).setName("裤子").setBounds(955, 410, 80, 80);
				addLast(new EquipmentSlot() {
					@Override
					public BodyParts targetBodyParts() {
						return ((HumanBody)NAGame.controllingUnit().body()).foots();
					}
				}).setName("鞋").setBounds(955, 490, 80, 80);
			}
			@Override
			public void idle() {
				final Graphics2D G2 = GHQ.getG2D(Color.GRAY, GHQ.stroke3);
				//center (a human body)
				humanBodyIF.dotPaint_rate(845, 345, 1.2);
				//item description
				G2.setColor(Color.GRAY);
				G2.setStroke(GHQ.stroke3);
				G2.drawRect(145, 400, 350, 180);
				G2.setColor(Color.WHITE);
				G2.fillRect(145, 400, 350, 180);
				super.idle();
			}
		});
		//Talent
		set(TALENT, new GUIParts() {
			private TableStorageViewer<Talent> talentList;
			private final ImageFrame ICON_BG = defaultSlotPaint;
			private final GHQTextArea talentDescriptionTextArea = new GHQTextArea() {
				{
					setName("TalentDescription");
					setBounds(581, 90, 400, 375);
					this.textArea().setFont(GHQ.initialFont.deriveFont(20F));
				}
			};
			{
				setName("TALENT_SCREEN");
				addLast(new ScrollBar(talentDescriptionTextArea).setScrollSpd(10))
					.setBGColor(Color.WHITE);
				talentList = new TableStorageViewer<Talent>() {
					{
						point().setXY(40, 90);
						setCellSize(50);
						setTableStorage(new TableStorage<Talent>(8, 15, Talent.NULL_TALENT));
						setName("TalentCells");
					}
					@Override
					public Talent objectToT(Object object) {
						if(object instanceof Talent)
							return (Talent)object;
						return null;
					}
					@Override
					protected void paintOfCell(int id, HasDotPaint object, int x, int y) {
						ICON_BG.rectPaint(x, y, this.cellSize);
						super.paintOfCell(id, object, x, y);
						GHQ.getG2D(Color.RED, GHQ.stroke1).drawRect(x, y, cellSize, cellSize);
					}
					@Override
					public boolean clicked(MouseEvent e) {
						boolean b = super.clicked(e);
						talentDescriptionTextArea.textArea().setText(this.getMouseHoveredElement().description());
						return b;
					}
					@Override
					public boolean checkDragIn(GUIParts sourceUI, Object dropObject) {
						//reject all drag in
						//and, if the sourceUI is itself, it will guess it as a normal click event.
						if(sourceUI == this)
							talentDescriptionTextArea.textArea().setText(this.getMouseHoveredElement().description());
						return false;
					}
					@Override
					public boolean checkDragOut(GUIParts targetUI, Object dropObject) {
						//check nothing
						return true;
					}
				};
				talentList.setBGColor(Color.WHITE);
				addLast(new ScrollBar(talentList).setScrollSpd(10))
					.setBounds(40, 90, 500, 350)
					.setBGColor(Color.LIGHT_GRAY)
					.setName("TalentScrollBar");
			}
			@Override
			public GUIParts enable() {
				super.enable();
				talentList.storage.clear();
				for(Talent talent : NAGame.controllingUnit().talents()) {
					talentList.storage.add(talent);
				}
				talentDescriptionTextArea.textArea().setText("(点击一个图标查看说明...)");
				return this;
			}
		});
		//Parameter(controllingUnit information)
		set(STATUS, new GUIParts() {
			@Override
			public void idle() {
				super.idle();
				//BG
				GHQ.getG2D(Color.GRAY, GHQ.stroke3).drawRect(30, 80, 400, 450);
				GHQ.getG2D(Color.GRAY, GHQ.stroke3).drawRect(520, 80, 450, 450);
				//triangle
				final int xs = 200, ys = 400, d = 25;
				final double d1 = NAGame.controllingUnit().POW_FIXED.doubleValue(),
						d2 = NAGame.controllingUnit().AGI_FIXED.doubleValue(),
						d3 = NAGame.controllingUnit().INT_FIXED.doubleValue();
				int x1, y1, x2, y2, x3, y3;
				x1 = 0;
				y1 = (int)(-d1*d);
				x2 = (int)(-d2*0.86*d);
				y2 = (int)(+d2*0.5*d);
				x3 = (int)(+d3*0.86*d);
				y3 = (int)(+d3*0.5*d);
				final Graphics2D G2 = GHQ.getG2D(Color.BLACK, GHQ.stroke7);
				G2.drawPolygon(new int[] {xs + x1, xs + x2, xs + x3}, new int[] {ys + y1, ys + y2, ys + y3}, 3);
				G2.setColor(Color.WHITE);
				G2.fillPolygon(new int[] {xs + x1, xs + x2, xs + x3}, new int[] {ys + y1, ys + y2, ys + y3}, 3);
				//ruler
				G2.setColor(new Color(50, 50, 50, 100));
				G2.setStroke(GHQ.stroke5);
				for(int i = 1;i <= 5;++i) {
					x1 = 0;
					y1 = (int)(-i*d);
					x2 = (int)(-i*0.86*d);
					y2 = (int)(+i*0.5*d);
					x3 = (int)(+i*0.86*d);
					y3 = (int)(+i*0.5*d);
					G2.drawPolygon(new int[] {xs + x1, xs + x2, xs + x3}, new int[] {ys + y1, ys + y2, ys + y3}, 3);
				}
				//ruler2
				G2.setStroke(GHQ.stroke1);
				G2.setColor(Color.RED);
				G2.drawLine(xs, ys, xs, ys - 5*d);
				G2.setColor(Color.GREEN);
				G2.drawLine(xs, ys, xs - (int)(5*0.86*d), ys + (int)(5*0.5*d));
				G2.setColor(Color.BLUE);
				G2.drawLine(xs, ys, xs + (int)(5*0.86*d), ys + (int)(5*0.5*d));
				//outer ruler
				G2.setStroke(GHQ.stroke1);
				G2.setColor(Color.GRAY);
				G2.drawOval(xs - d*5, ys - d*5, d*10, d*10);
				//status text preview
				G2.setFont(GHQ.initialFont.deriveFont(22F));
				G2.setColor(Color.RED);
				G2.drawString("力量: " + NAGame.controllingUnit().POW_FLOAT.intValue() + "(" + NAGame.controllingUnit().POW_FIXED.intValue() + ")", 545, 115);
				G2.setColor(Color.BLUE);
				G2.drawString("智力: " + NAGame.controllingUnit().INT_FLOAT.intValue() + "(" + NAGame.controllingUnit().INT_FIXED.intValue() + ")", 545, 140);
				G2.setColor(Color.GREEN);
				G2.drawString("敏捷: " + NAGame.controllingUnit().AGI_FLOAT.intValue() + "(" + NAGame.controllingUnit().AGI_FIXED.intValue() + ")", 545, 165);
				
				G2.setColor(Color.RED);
				G2.drawString("HP: " + NAGame.controllingUnit().RED_BAR.intValue() + "/" + NAGame.controllingUnit().RED_BAR.max().intValue(), 545, 200);
				G2.setColor(Color.BLUE);
				G2.drawString("MP: " + NAGame.controllingUnit().BLUE_BAR.intValue() + "/" + NAGame.controllingUnit().BLUE_BAR.max().intValue(), 545, 225);
				G2.setColor(Color.GREEN);
				G2.drawString("STAMINA: " + NAGame.controllingUnit().GREEN_BAR.intValue() + "/" + NAGame.controllingUnit().GREEN_BAR.max().intValue(), 545, 250);
				G2.setColor(Color.WHITE);
				G2.drawString("FOOD: " + NAGame.controllingUnit().WHITE_BAR.intValue() + "/" + NAGame.controllingUnit().WHITE_BAR.max().intValue(), 545, 275);
			
				G2.setColor(Color.WHITE);
				G2.drawString("SPD: " + NAGame.controllingUnit().SPEED.intValue(), 545, 310);
				G2.drawString("SENSE: " + NAGame.controllingUnit().SENSE.intValue(), 545, 335);
				G2.drawString("TOUGHNESS: " + NAGame.controllingUnit().TOUGHNESS.intValue(), 545, 360);
				G2.drawString("TOUGHNESS_REG: " + NAGame.controllingUnit().TOUGHNESS_REG.intValue(), 545, 385);
				G2.drawString("CRI: " + NAGame.controllingUnit().CRI.doubleValue(), 545, 410);
				G2.drawString("AVD: " + NAGame.controllingUnit().AVD.doubleValue(), 545, 435);
				G2.drawString("REF: " + NAGame.controllingUnit().REF.doubleValue(), 545, 460);
				G2.drawString("SUCK: " + NAGame.controllingUnit().SUCK.doubleValue(), 545, 485);
			}
		});
		//Craft
		set(CRAFT, new GUIParts() {
			@Override
			public void paint() {
				super.paint();
				GHQ.getG2D(Color.GRAY).drawRect(190, 140, 200, 370);
				GHQ.getG2D(Color.GRAY).drawRect(440, 140, 200, 200);
				GHQ.getG2D(Color.GRAY).drawRect(730, 140, 200, 200);
			}
		});
		//System
		set(SYSTEM, new GUIParts() {
			{
				
			}
		});
		//Memo
		set(MEMO, new GUIParts() {
			{
				super.addLast(new AutoResizeMenu(30, 80, 400, 60, 10))
					.addNewLine(new TextButton("ENEMY CATALOG", Color.WHITE))
					.addNewLine(new TextButton("EQUIPMENT CATALOG", Color.WHITE))
					.addNewLine(new TextButton("MATERIAL CATALOG", Color.WHITE))
					.addNewLine(new TextButton("TALKING RECORD", Color.WHITE))
					.addNewLine(new TextButton("BLUEPRINT AND RECEIPE", Color.WHITE))
					;
			}
			@Override
			public void paint() {
				super.paint();
				GHQ.getG2D(Color.GRAY, GHQ.stroke3).drawRect(30, 80, 400, 450);
				GHQ.getG2D(Color.GRAY, GHQ.stroke3).drawRect(520, 80, 450, 450);
			}
		});
	}
	@Override
	public void idle() {
		super.idle();
		GHQ.getG2D(Color.GRAY);
		GHQ.drawStringGHQ("状态", (int)(GHQ.screenW()/6.0*0) + 15, 50, GHQ.initialFont.deriveFont(40F));
		GHQ.drawStringGHQ("天赋", (int)(GHQ.screenW()/6.0*1) + 15, 50, GHQ.initialFont.deriveFont(40F));
		GHQ.drawStringGHQ("背包", (int)(GHQ.screenW()/6.0*2) + 15, 50, GHQ.initialFont.deriveFont(40F));
		GHQ.drawStringGHQ("合成", (int)(GHQ.screenW()/6.0*3) + 15, 50, GHQ.initialFont.deriveFont(40F));
		GHQ.drawStringGHQ("设置", (int)(GHQ.screenW()/6.0*4) + 15, 50, GHQ.initialFont.deriveFont(40F));
		GHQ.drawStringGHQ("图鉴", (int)(GHQ.screenW()/6.0*5) + 15, 50, GHQ.initialFont.deriveFont(40F));
	}
}
