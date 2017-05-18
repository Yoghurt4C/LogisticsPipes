package logisticspipes.network.guis.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import logisticspipes.blocks.LogisticsDevelopmentStationTileEntity;
import logisticspipes.gui.GuiDevelopmentStation;
import logisticspipes.network.abstractguis.CoordinatesGuiProvider;
import logisticspipes.network.abstractguis.GuiProvider;
import logisticspipes.utils.gui.DummyContainer;

public class DevelopmentStationGUI extends CoordinatesGuiProvider {

	public DevelopmentStationGUI(int id) {
		super(id);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		LogisticsDevelopmentStationTileEntity station = this.getTile(player.getEntityWorld(), LogisticsDevelopmentStationTileEntity.class);
		if(station != null) {
			return new GuiDevelopmentStation(player, station);
		}
		return null;
	}

	@Override
	public Container getContainer(EntityPlayer player) {
		LogisticsDevelopmentStationTileEntity station = this.getTile(player.getEntityWorld(), LogisticsDevelopmentStationTileEntity.class);

		DummyContainer container = new DummyContainer(player.inventory, station.resultInv);
		container.addNormalSlotsForPlayerInventory(8, 50);
		container.addNormalSlot(0, station.resultInv, 146, 39);

		return container;
	}

	@Override
	public GuiProvider template() {
		return new DevelopmentStationGUI(getId());
	}
}
