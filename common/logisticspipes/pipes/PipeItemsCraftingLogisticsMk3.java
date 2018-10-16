package logisticspipes.pipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;

import logisticspipes.gui.hud.HUDCraftingMK3;
import logisticspipes.interfaces.IBufferItems;
import logisticspipes.interfaces.IHeadUpDisplayRenderer;
import logisticspipes.interfaces.routing.IAdditionalTargetInformation;
import logisticspipes.modules.ModuleCrafter;
import logisticspipes.modules.ModuleCrafterMK3;
import logisticspipes.modules.abstractmodules.LogisticsModule.ModulePositionType;
import logisticspipes.textures.Textures;
import logisticspipes.textures.Textures.TextureType;
import logisticspipes.utils.item.ItemIdentifierStack;

public class PipeItemsCraftingLogisticsMk3 extends PipeItemsCraftingLogisticsMk2 implements IBufferItems {

	private HUDCraftingMK3 HUD = new HUDCraftingMK3(this);

	public PipeItemsCraftingLogisticsMk3(Item item) {
		super(item);
		craftingModule = new ModuleCrafterMK3(this);
		craftingModule.registerPosition(ModulePositionType.IN_PIPE, 0);
	}

	@Override
	public void enabledUpdateEntity() {
		super.enabledUpdateEntity();
	}

	@Override
	public TextureType getCenterTexture() {
		return Textures.LOGISTICSPIPE_CRAFTERMK3_TEXTURE;
	}

	@Override
	public IHeadUpDisplayRenderer getRenderer() {
		return HUD;
	}

	@Override
	public ModuleCrafter getLogisticsModule() {
		return craftingModule;
	}

	public ModuleCrafterMK3 getMk3Module() {
		return (ModuleCrafterMK3) craftingModule;
	}

	/**
	 * does not claim ownership of stack.
	 */
	@Override
	public int addToBuffer(ItemIdentifierStack stack, IAdditionalTargetInformation info) {
		return getMk3Module().addToBuffer(stack, info);
	}

	public List<ItemIdentifierStack> getCraftedItems() {
		// TODO PROVIDE REFACTOR
		return new ArrayList<>(0);
	}
}
