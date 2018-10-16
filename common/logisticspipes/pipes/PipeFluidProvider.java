package logisticspipes.pipes;

import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import logisticspipes.pipes.basic.fluid.FluidRoutedPipe;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.textures.Textures;
import logisticspipes.textures.Textures.TextureType;

public class PipeFluidProvider extends FluidRoutedPipe {

	public PipeFluidProvider(Item item) {
		super(item);
	}

	@Override
	public boolean disconnectPipe(TileEntity tile, EnumFacing dir) {
		return SimpleServiceLocator.pipeInformationManager.isFluidPipe(tile);
	}

	@Override
	public TextureType getCenterTexture() {
		return Textures.LOGISTICSPIPE_LIQUID_PROVIDER;
	}

	@Override
	public boolean canInsertToTanks() {
		return true;
	}

	@Override
	public boolean canInsertFromSideToTanks() {
		return true;
	}

	@Override
	public boolean canReceiveFluid() {
		return false;
	}
}
