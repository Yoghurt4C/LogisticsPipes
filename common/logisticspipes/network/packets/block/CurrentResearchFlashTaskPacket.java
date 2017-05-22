package logisticspipes.network.packets.block;

import net.minecraft.entity.player.EntityPlayer;

import lombok.Getter;
import lombok.Setter;

import logisticspipes.blocks.LogisticsReseachTableTileEntity;
import logisticspipes.network.abstractpackets.CoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;
import network.rs485.logisticspipes.util.LPDataInput;
import network.rs485.logisticspipes.util.LPDataOutput;

public class CurrentResearchFlashTaskPacket extends CoordinatesPacket {

	@Getter
	@Setter
	public int progress = 0;

	@Getter
	@Setter
	public int totalProgress = 0;

	@Getter
	@Setter
	private LogisticsReseachTableTileEntity.ResearchTiers target;

	@Getter
	@Setter
	private int moduleId = 0;


	public CurrentResearchFlashTaskPacket(int id) {
		super(id);
	}

	@Override
	public void processPacket(EntityPlayer player) {
		LogisticsReseachTableTileEntity table = this.getTile(player.worldObj, LogisticsReseachTableTileEntity.class);
		if(table == null) return;
		table.handleCurrentResearchFlashTask(getProgress(), getTotalProgress(), getTarget(), getModuleId());
	}

	@Override
	public void writeData(LPDataOutput output) {
		super.writeData(output);
		output.writeInt(progress);
		output.writeInt(totalProgress);
		output.writeEnum(target);
		output.writeInt(moduleId);
	}

	@Override
	public void readData(LPDataInput input) {
		super.readData(input);
		progress = input.readInt();
		totalProgress = input.readInt();
		target = input.readEnum(LogisticsReseachTableTileEntity.ResearchTiers.class);
		moduleId = input.readInt();
	}

	@Override
	public ModernPacket template() {
		return new CurrentResearchFlashTaskPacket(getId());
	}
}
