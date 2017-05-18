package logisticspipes.blocks;

import java.util.BitSet;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import lombok.Getter;

import logisticspipes.interfaces.IGuiOpenControler;
import logisticspipes.interfaces.IGuiTileEntity;
import logisticspipes.items.ItemModule;
import logisticspipes.network.NewGuiHandler;
import logisticspipes.network.abstractguis.CoordinatesGuiProvider;
import logisticspipes.network.guis.block.DevelopmentStationGUI;
import logisticspipes.utils.PlayerCollectionList;
import logisticspipes.utils.item.ItemIdentifierInventory;

public class LogisticsDevelopmentStationTileEntity extends LogisticsSolidTileEntity implements IGuiTileEntity, IGuiOpenControler {

	private final EnumSet<DevelopmentModules> knownDevelopemnts = EnumSet.of(DevelopmentModules.Basic);
	private final PlayerCollectionList openGuiPlayers = new PlayerCollectionList();
	public ItemIdentifierInventory resultInv = new ItemIdentifierInventory(1, "Development Inventory", 64);
	public Task currentTask = null;

	@Override
	public CoordinatesGuiProvider getGuiProvider() {
		return NewGuiHandler.getGui(DevelopmentStationGUI.class);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		final long[] toSave = { 0 };
		knownDevelopemnts.forEach(o -> toSave[0] |= 1L << o.ordinal());
		nbt.setLong("knownDevelopemnts", toSave[0]);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		long toRead = nbt.getLong("knownDevelopemnts");
		BitSet bs = BitSet.valueOf(new long[]{toRead});
		knownDevelopemnts.clear();
		for (int i = bs.nextSetBit(0); i >= 0 && i < DevelopmentModules.values().length; i = bs.nextSetBit(i + 1)) {
			knownDevelopemnts.add(DevelopmentModules.values()[i]);
		}
	}

	public boolean knows(DevelopmentModules current) {
		return knownDevelopemnts.contains(current);
	}

	@Override
	public void guiOpenedByPlayer(EntityPlayer player) {
		openGuiPlayers.add(player);
	}

	@Override
	public void guiClosedByPlayer(EntityPlayer player) {
		openGuiPlayers.remove(player);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(currentTask != null) {
			currentTask.progress++;
			if(currentTask.progress >= currentTask.totalProgress) {
				currentTask.done();
				currentTask = null;
			} else {
				currentTask.sendUpdate();
			}
		}


	}

	/*
	 * Only add new values at the end, as othervise saving will be broken.
	 */
	public enum DevelopmentModules {
		Basic(null, 0, 8),
		Advanced(Basic, 4 * 60, 2 * 60),
		HighSpeed(Advanced, 8 * 60, 4 * 60),
		Intelligent(Advanced, 15 * 60, 10 * 60),
		Compatibility(Basic, 4 * 60, 2 * 60);

		private final List<DevelopmentModules> childs = new LinkedList<>();
		@Getter
		private final DevelopmentModules parent;
		private final List<ItemModule.Module> allowedModules = new LinkedList<>();
		@Getter
		private final int timeToSelfDevelop; // In Seconds
		@Getter
		private final int timeToDevelopModule; // In Seconds


		private DevelopmentModules(DevelopmentModules parent, int timeToSelfDevelop, int timeToDevelopModule) {
			this.parent = parent;
			if(parent != null) {
				this.parent.childs.add(this);
			}
			this.timeToSelfDevelop = timeToSelfDevelop;
			this.timeToDevelopModule = timeToDevelopModule;
		}

		public void add(ItemModule.Module m) {
			allowedModules.add(m);
		}

		public List<DevelopmentModules> getChilds() {
			return Collections.unmodifiableList(childs);
		}

		public List<ItemModule.Module> getAllowedModules() {
			return Collections.unmodifiableList(allowedModules);
		}
	}

	public abstract class Task {
		public int progress = 0;
		public int totalProgress = 0;

		public abstract void done();

		public abstract void sendUpdate();
	}

	public class DevelopTask extends Task {
		public DevelopmentModules target;

		@Override
		public void done() {
			knownDevelopemnts.add(target);
		}

		@Override
		public void sendUpdate() {

		}
	}

	public class FlashTask extends Task {
		public ItemModule.Module target;

		@Override
		public void done() {

		}

		@Override
		public void sendUpdate() {

		}
	}
}
