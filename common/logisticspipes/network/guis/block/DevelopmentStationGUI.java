package logisticspipes.network.guis.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import logisticspipes.LogisticsPipes;
import logisticspipes.blocks.LogisticsReseachTableTileEntity;
import logisticspipes.gui.GuiDevelopmentStation;
import logisticspipes.items.ItemLogisticsChips;
import logisticspipes.network.abstractguis.CoordinatesGuiProvider;
import logisticspipes.network.abstractguis.GuiProvider;
import logisticspipes.utils.gui.DummyContainer;
import logisticspipes.utils.item.ItemIdentifier;

public class DevelopmentStationGUI extends CoordinatesGuiProvider {

	public DevelopmentStationGUI(int id) {
		super(id);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		LogisticsReseachTableTileEntity station = this.getTile(player.getEntityWorld(), LogisticsReseachTableTileEntity.class);
		if(station != null) {
			return new GuiDevelopmentStation(player, station);
		}
		return null;
	}

	@Override
	public Container getContainer(EntityPlayer player) {
		LogisticsReseachTableTileEntity station = this.getTile(player.getEntityWorld(), LogisticsReseachTableTileEntity.class);

		DummyContainer container = new DummyContainer(player, station.resultInv, station);
		container.addNormalSlotsForPlayerInventory(8, 50);
		container.addRestrictedSlot(0, station.resultInv, 146, 39, itemStack -> ItemIdentifier
				.get(new ItemStack(LogisticsPipes.LogisticsChips, 1, ItemLogisticsChips.ITEM_CHIP_PROGRAMMABLE))
				.equals(ItemIdentifier.get(itemStack)));

		return container;
	}

	@Override
	public GuiProvider template() {
		return new DevelopmentStationGUI(getId());
	}
}
