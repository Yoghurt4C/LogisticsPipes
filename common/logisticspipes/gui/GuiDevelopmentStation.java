package logisticspipes.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import logisticspipes.LogisticsPipes;
import logisticspipes.blocks.LogisticsReseachTableTileEntity;
import logisticspipes.items.ItemLogisticsChips;
import logisticspipes.items.ItemModule;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.packets.block.StartFlashingPacket;
import logisticspipes.network.packets.block.StartResearchPacket;
import logisticspipes.proxy.MainProxy;
import logisticspipes.utils.Color;
import logisticspipes.utils.gui.DummyContainer;
import logisticspipes.utils.gui.GuiGraphics;
import logisticspipes.utils.gui.LogisticsBaseTabGuiScreen;
import logisticspipes.utils.gui.ProgressBar;
import logisticspipes.utils.gui.SimpleGraphics;
import logisticspipes.utils.gui.SmallGuiButton;
import logisticspipes.utils.item.ItemIdentifier;
import logisticspipes.utils.item.ItemStackRenderer;
import logisticspipes.utils.string.StringUtils;

public class GuiDevelopmentStation extends LogisticsBaseTabGuiScreen {

	private static final String GUI_PREFIX = "gui.researchTable.";

	//private LogisticsReseachTableTileEntity.ResearchTiers current = LogisticsReseachTableTileEntity.ResearchTiers.Basic;
	private final LogisticsReseachTableTileEntity table;
	private final Slot toFlash;
	private final ItemIdentifier programmChip = ItemIdentifier.get(new ItemStack(LogisticsPipes.LogisticsChips, 1, ItemLogisticsChips.ITEM_CHIP_PROGRAMMABLE));

	public GuiDevelopmentStation(EntityPlayer player, LogisticsReseachTableTileEntity station) {
		super(180, 200);

		DummyContainer container = new DummyContainer(player.inventory, station.resultInv);

		container.addNormalSlotsForPlayerInventory(10, 115);

		toFlash = container.addRestrictedSlot(0, station.resultInv, 10, 82, itemStack -> programmChip.equals(ItemIdentifier.get(itemStack)));

		addTab(new ResearchTier(container, LogisticsReseachTableTileEntity.ResearchTiers.Basic));
		addTab(new ResearchTier(container, LogisticsReseachTableTileEntity.ResearchTiers.Advanced));
		addTab(new ResearchTier(container, LogisticsReseachTableTileEntity.ResearchTiers.HighSpeed));
		addTab(new ResearchTier(container, LogisticsReseachTableTileEntity.ResearchTiers.Intelligent));
		addTab(new ResearchTier(container, LogisticsReseachTableTileEntity.ResearchTiers.Compatibility));

		this.inventorySlots = container;
		this.table = station;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouse_x, int mouse_y) {
		super.drawGuiContainerBackgroundLayer(f, mouse_x, mouse_y);
		GuiGraphics.drawPlayerInventoryBackground(mc, guiLeft + 10, guiTop + 115);
	}


	private class ResearchTier extends TabSubGui {

		private final LogisticsReseachTableTileEntity.ResearchTiers thisTier;
		private GuiButton unlock;
		private GuiButton flash;
		private GuiButton pageback;
		private GuiButton pagenext;
		private ProgressBar unlockProgress;
		private ProgressBar flashProgress;
		private int pageFlash;
		private int flashSel;

		private boolean isTabActive;

		public ResearchTier(DummyContainer container, LogisticsReseachTableTileEntity.ResearchTiers thisTier) {
			this.thisTier = thisTier;
		}

		@Override
		public void initTab() {
			super.initTab();
			this.unlockProgress = new ProgressBar(8, 42, 164, 14);
			this.flashProgress = new ProgressBar(8, 80, 164, 14);
			this.unlock = addButton(new GuiButton(0, guiLeft + 8, guiTop + 41, 162, 20, StringUtils.translate(GUI_PREFIX + "unlock")));
			this.flash = addButton(new GuiButton(1, guiLeft + 34, guiTop + 80, 136, 20, StringUtils.translate(GUI_PREFIX + "flash")));
			this.pageback = addButton(new SmallGuiButton(2, guiLeft + 145, guiTop + 30, 10, 10, "<", () -> thisTier.getAllowedModules().size() > 9));
			this.pagenext = addButton(new SmallGuiButton(3, guiLeft + 160, guiTop + 30, 10, 10, ">", () -> thisTier.getAllowedModules().size() > 9));
			this.unlock.visible = !GuiDevelopmentStation.this.table.knows(thisTier);
			this.flash.visible = GuiDevelopmentStation.this.table.knows(thisTier);
		}

		@Override
		public void checkButton(GuiButton button, boolean isTabActive) {
			super.checkButton(button, isTabActive);
			this.isTabActive = isTabActive;
			if(isTabActive) {
				this.unlock.visible = !GuiDevelopmentStation.this.table.knows(thisTier);
				this.unlock.enabled = table.currentTask == null;
				if(table.currentTask instanceof LogisticsReseachTableTileEntity.DevelopTask && ((LogisticsReseachTableTileEntity.DevelopTask) table.currentTask).getTarget() == thisTier) {
					unlock.visible = false;
				}
				this.flash.visible = GuiDevelopmentStation.this.table.knows(thisTier);
				this.flash.enabled = table.currentTask == null;
				if(table.currentTask instanceof LogisticsReseachTableTileEntity.FlashTask && ((LogisticsReseachTableTileEntity.FlashTask) table.currentTask).getSource() == thisTier) {
					this.flash.visible = false;
				}
			}
		}

		@Override
		public void renderIcon(int x, int y) {
			GuiGraphics.drawFromPath(mc, x, y, 16, 16, "textures/items/development/" + thisTier.name() + ".png");
		}

		@Override
		public void renderBackgroundContent() {
			mc.fontRenderer.drawString(StringUtils.translate("gui.researchTier." + thisTier.name()), guiLeft + 9, guiTop + 29, 0x404040);
			if(unlock.visible) {
				mc.fontRenderer.drawString(StringUtils.getCuttedString(
						StringUtils.translate(GUI_PREFIX + "takestime") + " " + StringUtils.ticksToReadableTime(thisTier.getTimeToUnlockTier() * 20), 165, mc.fontRenderer),
						guiLeft + 8, guiTop + 66, 0x404040);
			}
			if(table.currentTask instanceof LogisticsReseachTableTileEntity.DevelopTask && ((LogisticsReseachTableTileEntity.DevelopTask) table.currentTask).getTarget() == thisTier) {
				this.unlockProgress.setProgress(((float)table.currentTask.progress) / table.currentTask.totalProgress);
				mc.fontRenderer.drawString(StringUtils.getCuttedString(
						StringUtils.translate(GUI_PREFIX + "timetogo") + " " + StringUtils.ticksToReadableTime((table.currentTask.totalProgress - table.currentTask.progress), false, true), 165, mc.fontRenderer),
						guiLeft + 8, guiTop + 62, 0x404040);

			}
			if(table.currentTask instanceof LogisticsReseachTableTileEntity.FlashTask && ((LogisticsReseachTableTileEntity.FlashTask) table.currentTask).getSource() == thisTier) {
				this.flashProgress.setProgress(((float)table.currentTask.progress) / table.currentTask.totalProgress);
				mc.fontRenderer.drawString(StringUtils.getCuttedString(
						StringUtils.translate(GUI_PREFIX + "timetogo") + " " + StringUtils.ticksToReadableTime((table.currentTask.totalProgress - table.currentTask.progress), false, true), 165, mc.fontRenderer),
						guiLeft + 8, guiTop + 100, 0x404040);

			}
			if(GuiDevelopmentStation.this.table.knows(thisTier)) {
				if (this.flash.enabled && this.flash.visible) {
					GuiGraphics.drawSlotBackground(mc, guiLeft + 9, guiTop + 81);
					ItemStackRenderer itemstackRenderer = new ItemStackRenderer(guiLeft + 10, guiTop + 82, 1.0F, true, false, true);
					itemstackRenderer.setItemstack(programmChip.makeNormalStack(1)).setDisplayAmount(ItemStackRenderer.DisplayAmount.NEVER);
					itemstackRenderer.renderInGui();


					int x = guiLeft + 9;
					int y = guiTop + 81;
					GuiGraphics.zLevel = 2;
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
					mc.renderEngine.bindTexture(GuiGraphics.SLOT_TEXTURE);

					Tessellator var9 = Tessellator.instance;
					var9.startDrawingQuads();
					var9.addVertexWithUV(x, y + 18, GuiGraphics.zLevel, 0, 1);
					var9.addVertexWithUV(x + 18, y + 18, GuiGraphics.zLevel, 1, 1);
					var9.addVertexWithUV(x + 18, y, GuiGraphics.zLevel, 1, 0);
					var9.addVertexWithUV(x, y, GuiGraphics.zLevel, 0, 0);
					var9.draw();
					GL11.glDisable(GL11.GL_BLEND);
				}
				GuiDevelopmentStation.this.drawRect(guiLeft + 9, guiTop + 43, guiLeft + 9 + 9 * 18 , guiTop + 43 + 18, Color.GREY);
				if(flashSel >= 0) {
					GuiDevelopmentStation.this.drawRect(guiLeft + 9 + flashSel * 18, guiTop + 43, guiLeft + 9 + (1 + flashSel) * 18 , guiTop + 43 + 18, Color.WHITE);
					GuiDevelopmentStation.this.drawRect(guiLeft + 10 + flashSel * 18, guiTop + 44, guiLeft + 8 + (1 + flashSel) * 18 , guiTop + 44 + 16, Color.DARKER_GREY);
					mc.fontRenderer.drawString(StringUtils.getCuttedString(StringUtils.translate("item." + thisTier.getAllowedModules().get(flashSel + pageFlash).getILogisticsModuleClassSimpleName()), 165, mc.fontRenderer), guiLeft + 9, guiTop + 67, 0x404040);
				}
			}
		}

		@Override
		public void renderForgroundContent() {
			if(table.currentTask instanceof LogisticsReseachTableTileEntity.DevelopTask && ((LogisticsReseachTableTileEntity.DevelopTask) table.currentTask).getTarget() == thisTier) {
				this.unlockProgress.render();
			}
			if(table.currentTask instanceof LogisticsReseachTableTileEntity.FlashTask && ((LogisticsReseachTableTileEntity.FlashTask) table.currentTask).getSource() == thisTier) {
				this.flashProgress.render();
			}
			if(GuiDevelopmentStation.this.table.knows(thisTier)) {
				for (int i = pageFlash; i < pageFlash + 9 && i < thisTier.getAllowedModules().size(); i++) {
					ItemModule.Module module = thisTier.getAllowedModules().get(i);
					GuiGraphics.renderIconAt(mc, 10 + (i - pageFlash) * 18, 44, zLevel, module.getModuleIcon());
				}
			}
		}

		@Override
		public boolean showSlot(Slot slot) {
			if(isTabActive && slot == toFlash) {
				return GuiDevelopmentStation.this.table.knows(thisTier) && this.flash.enabled && this.flash.visible;
			}
			return super.showSlot(slot);
		}

		@Override
		public boolean isSlotForTab(Slot slot) {
			if(isTabActive && slot == toFlash) {
				return true;
			}
			return super.isSlotForTab(slot);
		}

		@Override
		public void buttonClicked(GuiButton button) {
			if(button == unlock) {
				if(table.currentTask == null) {
					if(!GuiDevelopmentStation.this.table.knows(thisTier)) {
						MainProxy.sendPacketToServer(PacketHandler.getPacket(StartResearchPacket.class).setEnumValue(thisTier).setTilePos(table));
					}
				}
			}
			if(button == pageback) {
				pageFlash--;
				if(pageFlash < 0) pageFlash = 0;
			}
			if(button == pagenext) {
				pageFlash++;
				if(pageFlash > thisTier.getAllowedModules().size() - 9) pageFlash = thisTier.getAllowedModules().size() - 9;
			}
			if(button == flash) {
				if(table.currentTask == null && GuiDevelopmentStation.this.table.knows(thisTier) && flashSel >= 0) {
					ItemModule.Module module = thisTier.getAllowedModules().get(flashSel + pageFlash);
					MainProxy.sendPacketToServer(PacketHandler.getPacket(StartFlashingPacket.class).setTier(thisTier).setInteger(module.getId()).setTilePos(table));
				}
			}
		}

		@Override
		public boolean handleClick(int x, int y, int type) {
			if(GuiDevelopmentStation.this.table.knows(thisTier) && !(table.currentTask instanceof LogisticsReseachTableTileEntity.FlashTask && ((LogisticsReseachTableTileEntity.FlashTask) table.currentTask).getSource() == thisTier)) {
				if (x > guiLeft + 9 && x < guiLeft + 9 + 9 * 18 && y > guiTop + 43 && y < guiTop + 43 + 18) {
					int lX = (x - guiLeft - 10) / 18;
					if (lX < 0 || lX >= 9 || (lX + pageFlash) >= thisTier.getAllowedModules().size()) {
						flashSel = -1;
					} else {
						flashSel = lX;
					}
				}
			}
			return super.handleClick(x, y, type);
		}
	}
}
