package logisticspipes.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import com.google.common.collect.Lists;

import logisticspipes.blocks.LogisticsDevelopmentStationTileEntity;
import logisticspipes.items.ItemModule;
import logisticspipes.utils.Color;
import logisticspipes.utils.gui.DummyContainer;
import logisticspipes.utils.gui.GuiGraphics;
import logisticspipes.utils.gui.ItemDisplay;
import logisticspipes.utils.gui.LogisticsBaseGuiScreen;
import logisticspipes.utils.gui.SmallGuiButton;

public class GuiDevelopmentStation extends LogisticsBaseGuiScreen {

	private int pageNextDev = 0; // currently not needed to no buttons
	private int pageFlash = 0;
	private int nextDevSel = -1;
	private int flashSel = -1;
	private LogisticsDevelopmentStationTileEntity.DevelopmentModules current = LogisticsDevelopmentStationTileEntity.DevelopmentModules.Basic;
	private final LogisticsDevelopmentStationTileEntity station;

	public GuiDevelopmentStation(EntityPlayer player, LogisticsDevelopmentStationTileEntity station) {
		super(180, 180, 0, 0);

		DummyContainer container = new DummyContainer(player.inventory, station.resultInv);

		container.addNormalSlotsForPlayerInventory(10, 95);

		container.addNormalSlot(0, station.resultInv, 146, 39);

		this.inventorySlots = container;
		this.station = station;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		addButton(new SmallGuiButton(0, guiLeft + 6, guiTop + 12, 10, 10, "<", () -> current.getParent() != null));
		addButton(new SmallGuiButton(1, guiLeft + 6, guiTop + 60, 45, 10, () -> nextDevSel >= 0 ? station.knows(current.getChilds().get(nextDevSel)) ? "Open" : "Develop" : "", () -> nextDevSel >= 0));
		addButton(new SmallGuiButton(2, guiLeft + 56, guiTop + 12, 10, 10, "<", () -> current.getChilds().size() > 6));
		addButton(new SmallGuiButton(3, guiLeft + 156, guiTop + 12, 10, 10, ">", () -> current.getChilds().size() > 6));
		addButton(new SmallGuiButton(4, guiLeft + 136, guiTop + 60, 35, 10, () -> flashSel >= 0 ? "Flash" : "", () -> flashSel >= 0));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GuiGraphics.drawGuiBackGround(mc, guiLeft, guiTop, right, bottom, zLevel, true);
		GuiGraphics.drawPlayerInventoryBackground(mc, guiLeft + 10, guiTop + 95);
		GuiGraphics.drawSlotBackground(mc, guiLeft + 18, guiTop + 8);
		GuiGraphics.drawSlotBackground(mc, guiLeft + 145, guiTop + 38);
		this.drawRect(guiLeft + 10, guiTop + 38, guiLeft + 10 + 2 * 18 , guiTop + 38 + 18, Color.GREY);
		if(nextDevSel >= 0) {
			this.drawRect(guiLeft + 10 + nextDevSel * 18, guiTop + 38, guiLeft + 10 + (1 + nextDevSel) * 18 , guiTop + 38 + 18, Color.WHITE);
			this.drawRect(guiLeft + 11 + nextDevSel * 18, guiTop + 39, guiLeft + 9 + (1 + nextDevSel) * 18 , guiTop + 39 + 16, Color.DARKER_GREY);
		}
		this.drawRect(guiLeft + 62, guiTop + 8, guiLeft + 62 + 6 * 18 , guiTop + 8 + 18, Color.GREY);
		if(flashSel >= 0) {
			this.drawRect(guiLeft + 62 + flashSel * 18, guiTop + 8, guiLeft + 62 + (1 + flashSel) * 18 , guiTop + 8 + 18, Color.WHITE);
			this.drawRect(guiLeft + 63 + flashSel * 18, guiTop + 9, guiLeft + 61 + (1 + flashSel) * 18 , guiTop + 9 + 16, Color.DARKER_GREY);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		GuiGraphics.drawFromPath(mc, 19, 9, 16, 16, "textures/items/development/" + current.name() + ".png");
		for(int i = pageNextDev;i < pageNextDev + 2 && i + pageNextDev < current.getChilds().size(); i++) {
			GuiGraphics.drawFromPath(mc, 11 + (i-pageNextDev) * 18, 39, 16, 16, "textures/items/development/" + current.getChilds().get(i).name() + ".png");
		}
		for(int i = pageFlash;i < pageFlash + 6 && i + pageFlash < current.getAllowedModules().size(); i++) {
			ItemModule.Module module = current.getAllowedModules().get(i);
			GuiGraphics.renderIconAt(mc, 63 + (i-pageFlash) * 18, 9, zLevel, module.getModuleIcon());
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);
		if(b == 0) {
			if(x > guiLeft + 10 && x < guiLeft + 10 + 2 * 18 && y > guiTop + 38 && y < guiTop + 38 + 18) {
				if(x >= guiLeft + 28) {
					if(pageNextDev + 1 < current.getChilds().size()) {
						nextDevSel = 1;
					} else {
						nextDevSel = -1;
					}
				} else {
					nextDevSel = 0;
				}
			}
			if(x > guiLeft + 62 && x < guiLeft + 62 + 6 * 18 && y > guiTop + 8 && y < guiTop + 8 + 18) {
				int lX = (x - guiLeft - 62) / 18;
				if(lX < 0 || lX >= 6 || (lX + pageFlash) > current.getAllowedModules().size()) {
					flashSel = -1;
				} else {
					flashSel = lX;
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch(button.id) {
			case 0: // Back

				break;
			case 1: // Develop/Open

				break;
			case 2: // Prev

				break;
			case 3: // Next

				break;
			case 4: // Flash

				break;
		}
	}
}
