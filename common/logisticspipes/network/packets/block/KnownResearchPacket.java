package logisticspipes.network.packets.block;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;

import lombok.Getter;
import lombok.Setter;

import logisticspipes.blocks.LogisticsReseachTableTileEntity;
import logisticspipes.network.abstractpackets.CoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;
import network.rs485.logisticspipes.util.LPDataInput;
import network.rs485.logisticspipes.util.LPDataOutput;

public class KnownResearchPacket extends CoordinatesPacket {

	@Getter
	@Setter
	private EnumSet<LogisticsReseachTableTileEntity.ResearchTiers> knownResearch;

	public KnownResearchPacket(int id) {
		super(id);
	}

	@Override
	public void processPacket(EntityPlayer player) {
		LogisticsReseachTableTileEntity tile = this.getTile(player.worldObj, LogisticsReseachTableTileEntity.class);
		if(tile == null) return;
		tile.getKnownResearch().clear();
		tile.getKnownResearch().addAll(knownResearch);
	}

	@Override
	public void writeData(LPDataOutput output) {
		super.writeData(output);
		output.writeEnumSet(knownResearch, LogisticsReseachTableTileEntity.ResearchTiers.class);
	}

	@Override
	public void readData(LPDataInput input) {
		super.readData(input);
		knownResearch = input.readEnumSet(LogisticsReseachTableTileEntity.ResearchTiers.class);
	}

	@Override
	public ModernPacket template() {
		return new KnownResearchPacket(getId());
	}
}
