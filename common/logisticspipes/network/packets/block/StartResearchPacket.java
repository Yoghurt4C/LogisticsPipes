package logisticspipes.network.packets.block;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.blocks.LogisticsReseachTableTileEntity;
import logisticspipes.network.abstractpackets.EnumCoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;

public class StartResearchPacket extends EnumCoordinatesPacket<LogisticsReseachTableTileEntity.ResearchTiers> {

	public StartResearchPacket(int id) {
		super(id);
	}

	@Override
	public Class<LogisticsReseachTableTileEntity.ResearchTiers> getEnumClass() {
		return LogisticsReseachTableTileEntity.ResearchTiers.class;
	}

	@Override
	public void processPacket(EntityPlayer player) {
		LogisticsReseachTableTileEntity table = this.getTile(player.worldObj, LogisticsReseachTableTileEntity.class);
		if(table == null) return;
		table.startUnlock(getEnumValue());
	}

	@Override
	public ModernPacket template() {
		return new StartResearchPacket(getId());
	}
}
