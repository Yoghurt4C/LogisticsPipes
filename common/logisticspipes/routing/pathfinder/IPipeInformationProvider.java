package logisticspipes.routing.pathfinder;

import java.util.List;
import java.util.stream.Stream;

import logisticspipes.interfaces.routing.IFilter;
import logisticspipes.pipes.basic.CoreRoutedPipe;
import logisticspipes.transport.LPTravelingItem;
import logisticspipes.utils.item.ItemIdentifier;

import network.rs485.logisticspipes.world.DoubleCoordinates;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import net.minecraft.util.EnumFacing;

public interface IPipeInformationProvider {

	enum ConnectionPipeType {
		ITEM,
		FLUID,
		MULTI,
		UNDEFINED
	}

	boolean isCorrect(ConnectionPipeType type);

	int getX();

	int getY();

	int getZ();

	World getWorld();

	boolean isRouterInitialized();

	boolean isRoutingPipe();

	CoreRoutedPipe getRoutingPipe();

	TileEntity getNextConnectedTile(EnumFacing direction);

	boolean isFirewallPipe();

	IFilter getFirewallFilter();

	TileEntity getTile();

	boolean divideNetwork();

	boolean powerOnly();

	boolean isOnewayPipe();

	boolean isOutputOpen(EnumFacing direction);

	boolean canConnect(TileEntity to, EnumFacing direction, boolean flag);

	double getDistance();

	boolean isItemPipe();

	boolean isFluidPipe();

	boolean isPowerPipe();

	double getDistanceTo(int destinationint, EnumFacing ignore, ItemIdentifier ident, boolean isActive, double travled, double max, List<DoubleCoordinates> visited);

	boolean acceptItem(LPTravelingItem item, TileEntity from);

	void refreshTileCacheOnSide(EnumFacing side);

	boolean isMultiBlock();

	Stream<TileEntity> getPartsOfPipe();
}
