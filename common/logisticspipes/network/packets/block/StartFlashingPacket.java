package logisticspipes.network.packets.block;

import net.minecraft.entity.player.EntityPlayer;

import lombok.Getter;
import lombok.Setter;

import logisticspipes.blocks.LogisticsReseachTableTileEntity;
import logisticspipes.network.abstractpackets.IntegerCoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;
import network.rs485.logisticspipes.util.LPDataInput;
import network.rs485.logisticspipes.util.LPDataOutput;

public class StartFlashingPacket extends IntegerCoordinatesPacket {

	@Getter
	@Setter
	private LogisticsReseachTableTileEntity.ResearchTiers tier;

	public StartFlashingPacket(int id) {
		super(id);
	}

	@Override
	public void readData(LPDataInput input) {
		super.readData(input);
		tier = input.readEnum(LogisticsReseachTableTileEntity.ResearchTiers.class);
	}

	@Override
	public void writeData(LPDataOutput output) {
		super.writeData(output);
		output.writeEnum(tier);
	}

	@Override
	public void processPacket(EntityPlayer player) {
		LogisticsReseachTableTileEntity table = this.getTile(player.worldObj, LogisticsReseachTableTileEntity.class);
		if(table == null) return;
		table.startFlashing(tier, getInteger());
	}

	@Override
	public ModernPacket template() {
		return new StartFlashingPacket(getId());
	}
}
