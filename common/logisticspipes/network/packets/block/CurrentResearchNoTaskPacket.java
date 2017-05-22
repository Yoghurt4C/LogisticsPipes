package logisticspipes.network.packets.block;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.blocks.LogisticsReseachTableTileEntity;
import logisticspipes.network.abstractpackets.CoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;

public class CurrentResearchNoTaskPacket extends CoordinatesPacket {

	public CurrentResearchNoTaskPacket(int id) {
		super(id);
	}

	@Override
	public void processPacket(EntityPlayer player) {
		LogisticsReseachTableTileEntity table = this.getTile(player.worldObj, LogisticsReseachTableTileEntity.class);
		if(table == null) return;
		table.handleCurrentResearchNoTask();
	}

	@Override
	public ModernPacket template() {
		return new CurrentResearchNoTaskPacket(getId());
	}
}
