package logisticspipes.blocks;

import java.util.BitSet;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import lombok.Getter;

import logisticspipes.LogisticsPipes;
import logisticspipes.interfaces.IGuiOpenControler;
import logisticspipes.interfaces.IGuiTileEntity;
import logisticspipes.items.ItemLogisticsChips;
import logisticspipes.items.ItemModule;
import logisticspipes.network.NewGuiHandler;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.abstractguis.CoordinatesGuiProvider;
import logisticspipes.network.guis.block.DevelopmentStationGUI;
import logisticspipes.network.packets.block.CurrentResearchDevelopTaskPacket;
import logisticspipes.network.packets.block.CurrentResearchFlashTaskPacket;
import logisticspipes.network.packets.block.CurrentResearchNoTaskPacket;
import logisticspipes.network.packets.block.KnownResearchPacket;
import logisticspipes.proxy.MainProxy;
import logisticspipes.utils.PlayerCollectionList;
import logisticspipes.utils.item.ItemIdentifier;
import logisticspipes.utils.item.ItemIdentifierInventory;

public class LogisticsReseachTableTileEntity extends LogisticsSolidTileEntity implements IGuiTileEntity, IGuiOpenControler {

	@Getter
	private final EnumSet<ResearchTiers> knownResearch = EnumSet.of(ResearchTiers.Basic);
	private final PlayerCollectionList openGuiPlayers = new PlayerCollectionList();
	public ItemIdentifierInventory resultInv = new ItemIdentifierInventory(1, "Research Inventory", 1);
	public Task currentTask = null;
	private final ItemIdentifier programmChip = ItemIdentifier.get(new ItemStack(LogisticsPipes.LogisticsChips, 1, ItemLogisticsChips.ITEM_CHIP_PROGRAMMABLE));

	@Override
	public CoordinatesGuiProvider getGuiProvider() {
		return NewGuiHandler.getGui(DevelopmentStationGUI.class);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		final long[] toSave = { 0 };
		knownResearch.forEach(o -> toSave[0] |= 1L << o.ordinal());
		nbt.setLong("knownResearch", toSave[0]);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		long toRead = nbt.getLong("knownResearch");
		BitSet bs = BitSet.valueOf(new long[]{toRead});
		knownResearch.clear();
		for (int i = bs.nextSetBit(0); i >= 0 && i < ResearchTiers.values().length; i = bs.nextSetBit(i + 1)) {
			knownResearch.add(ResearchTiers.values()[i]);
		}
		knownResearch.add(ResearchTiers.Basic);
	}

	public boolean knows(ResearchTiers current) {
		return knownResearch.contains(current);
	}

	@Override
	public void guiOpenedByPlayer(EntityPlayer player) {
		MainProxy.sendPacketToPlayer(PacketHandler.getPacket(CurrentResearchNoTaskPacket.class).setTilePos(LogisticsReseachTableTileEntity.this), player);
		openGuiPlayers.add(player);
		MainProxy.sendPacketToPlayer(PacketHandler.getPacket(KnownResearchPacket.class).setKnownResearch(knownResearch).setTilePos(this), player);
	}

	@Override
	public void guiClosedByPlayer(EntityPlayer player) {
		openGuiPlayers.remove(player);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(currentTask != null) {
			boolean consumed = MainProxy.getGlobalTick() % 20 == 0;
			if(usePowerFromAdjacentLTGP(currentTask.powerConsumtion)) {
				currentTask.progress++;
				consumed = true;
			}
			if(consumed) {
				if(currentTask.progress >= currentTask.totalProgress) {
					currentTask.done();
					currentTask = null;
					MainProxy.sendToPlayerList(PacketHandler.getPacket(CurrentResearchNoTaskPacket.class).setTilePos(LogisticsReseachTableTileEntity.this), openGuiPlayers);
				} else {
					currentTask.sendUpdate();
				}
			}
		}
	}

	public void startUnlock(ResearchTiers enumValue) {
		if(currentTask == null) {
			currentTask = new DevelopTask(enumValue);
		}
	}

	public void startFlashing(ResearchTiers tier, int integer) {
		if(currentTask == null) {
			if(isProgrammableChip() && !isProgrammedChip()) {
				tier.getAllowedModules().stream().
						filter(m -> m.getId() == integer).
						findAny()
						.ifPresent(module1 -> currentTask = new FlashTask(tier, module1));
			}
		}
	}

	private boolean isProgrammableChip() {
		return resultInv.getIDStackInSlot(0) != null && resultInv.getIDStackInSlot(0).getItem().equals(programmChip);
	}

	private boolean isProgrammedChip() {
		return LogisticsPipes.LogisticsChips.hasModuleIdOnProgrammChip(resultInv.getStackInSlot(0));
	}

	public void handleCurrentResearchDevelopTask(int progress, int totalProgress, ResearchTiers target) {
		if(MainProxy.isClient(this.getWorldObj())) {
			currentTask = new DevelopTask(target, totalProgress, target.getPowerConsumtionToUnlock());
			currentTask.progress = progress;
		}
	}

	public void handleCurrentResearchFlashTask(int progress, int totalProgress, ResearchTiers target, int moduleId) {
		if(MainProxy.isClient(this.getWorldObj())) {
			Optional<ItemModule.Module> module = target.getAllowedModules().stream().
					filter(m -> m.getId() == moduleId).
					findAny();
			if(module.isPresent()) {
				currentTask = new FlashTask(target, module.get(), totalProgress, target.getPowerConsumtionToFlash());
				currentTask.progress = progress;
			}
		}
	}

	public void handleCurrentResearchNoTask() {
		if(MainProxy.isClient(this.getWorldObj())) {
			currentTask = null;
		}
	}

	/*
	 * Only add new values at the end, as othervise saving will be broken.
	 */
	public enum ResearchTiers {
		Basic(null, 0, 30, 0, 0),
		Advanced(Basic, 4 * 60, 2 * 60, 100, 50),
		HighSpeed(Advanced, 8 * 60, 4 * 60, 200, 50),
		Intelligent(Advanced, 15 * 60, 10 * 60, 200, 50),
		Compatibility(Basic, 4 * 60, 2 * 60, 100, 50);

		private final List<ResearchTiers> childs = new LinkedList<>();
		@Getter
		private final ResearchTiers parent;
		private final List<ItemModule.Module> allowedModules = new LinkedList<>();
		@Getter
		private final int timeToUnlockTier; // In Seconds
		@Getter
		private final int timeToFlashProgram; // In Seconds
		@Getter
		private final int powerConsumtionToUnlock;
		@Getter
		private final int powerConsumtionToFlash;


		private ResearchTiers(ResearchTiers parent, int timeToSelfDevelop, int timeToDevelopModule, int powerToUnlock, int powerToPrepare) {
			this.parent = parent;
			if(parent != null) {
				this.parent.childs.add(this);
			}
			this.timeToUnlockTier = timeToSelfDevelop;
			this.timeToFlashProgram = timeToDevelopModule;
			this.powerConsumtionToUnlock = powerToUnlock;
			this.powerConsumtionToFlash = timeToDevelopModule;
		}

		public void add(ItemModule.Module m) {
			allowedModules.add(m);
		}

		public List<ResearchTiers> getChilds() {
			return Collections.unmodifiableList(childs);
		}

		public List<ItemModule.Module> getAllowedModules() {
			return Collections.unmodifiableList(allowedModules);
		}
	}

	public abstract class Task {
		public int progress = 0;
		public final int totalProgress;
		public final int powerConsumtion;

		protected Task(int totalProgress, int powerConsumtion) {
			this.totalProgress = totalProgress;
			this.powerConsumtion = powerConsumtion;
		}

		public abstract void done();

		public abstract void sendUpdate();
	}

	public class DevelopTask extends Task {
		@Getter
		private final ResearchTiers target;

		public DevelopTask(ResearchTiers target) {
			super(target.getTimeToUnlockTier() * 20, target.getPowerConsumtionToUnlock());
			this.target = target;
		}

		public DevelopTask(ResearchTiers target, int totalProgress, int powerConsumtion) {
			super(totalProgress, powerConsumtion);
			this.target = target;
		}

		@Override
		public void done() {
			knownResearch.add(target);
			MainProxy.sendToPlayerList(PacketHandler.getPacket(KnownResearchPacket.class).setKnownResearch(knownResearch).setTilePos(LogisticsReseachTableTileEntity.this), openGuiPlayers);
		}

		@Override
		public void sendUpdate() {
			MainProxy.sendToPlayerList(PacketHandler.getPacket(CurrentResearchDevelopTaskPacket.class).setProgress(progress).setTotalProgress(totalProgress).setTarget(target).setTilePos(LogisticsReseachTableTileEntity.this), openGuiPlayers);
		}
	}

	public class FlashTask extends Task {
		public final ItemModule.Module target;
		@Getter
		public final ResearchTiers source;

		protected FlashTask(ResearchTiers source, ItemModule.Module target) {
			super(source.getTimeToFlashProgram() * 20, source.getPowerConsumtionToFlash());
			this.target = target;
			this.source = source;
		}

		protected FlashTask(ResearchTiers source, ItemModule.Module target, int totalProgress, int powerConsumtion) {
			super(totalProgress, powerConsumtion);
			this.target = target;
			this.source = source;
		}

		@Override
		public void done() {
			if(isProgrammableChip() && !isProgrammedChip()) {
				ItemStack stack = resultInv.getStackInSlotOnClosing(0);
				NBTTagCompound nbt = stack.getTagCompound();
				if(nbt == null) {
					nbt = new NBTTagCompound();
				}
				LogisticsPipes.LogisticsChips.addModuleIdToProgrammChip(nbt, target.getId());
				stack.setTagCompound(nbt);
				resultInv.setInventorySlotContents(0, stack);
			}
		}

		@Override
		public void sendUpdate() {
			MainProxy.sendToPlayerList(PacketHandler.getPacket(CurrentResearchFlashTaskPacket.class).setProgress(progress).setTotalProgress(totalProgress).setTarget(source).setModuleId(target.getId()).setTilePos(LogisticsReseachTableTileEntity.this), openGuiPlayers);
		}
	}
}
