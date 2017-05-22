package logisticspipes.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import lombok.Getter;

import logisticspipes.LogisticsPipes;
import logisticspipes.blocks.LogisticsSolidBlock;
import logisticspipes.items.ItemLogisticsChips;
import logisticspipes.items.ItemModule;

public class ChipCraftingRecipes extends CraftingPartRecipes {

	private enum ModuleRecipeTemplate {
		BASIC(" c ", "rfr", "imi"),
		BASIC2(" c ", "rfr", "gmg"),
		VERSION2("fcf", "rbr", "imi"),
		VERSION3("fcf", "rar", "gmg"),
		VERSION4(" c ", "rbr", "gmg"),
		VERSION5("bcb", "rar", "gmg"),
		;
		@Getter
		private String line1;
		@Getter
		private String line2;
		@Getter
		private String line3;
		private ModuleRecipeTemplate(String line1, String line2, String line3) {
			this.line1 = line1;
			this.line2 = line2;
			this.line3 = line3;
		}
	}

	public void createModuleRecipe(ModuleRecipeTemplate template, int moduleID, CraftingParts parts, RecipeManager.RecipeIndex... addition) {
		createModuleRecipe(template.getLine1(), template.getLine2(), template.getLine3(), moduleID, parts, addition);
	}

	public void createModuleRecipe(String line1, String line2, String line3, int moduleID, CraftingParts parts, RecipeManager.RecipeIndex... addition) {
		List<RecipeManager.RecipeIndex> list = new ArrayList<>();
		list.add(new RecipeManager.RecipeIndex('r', "dustRedstone"));
		list.add(new RecipeManager.RecipeIndex('l', "gemLapis"));
		list.add(new RecipeManager.RecipeIndex('i', "ingotIron"));
		list.add(new RecipeManager.RecipeIndex('g', "ingotGold"));
		if(!Arrays.stream(addition).anyMatch(out -> out.getIndex() == 'm')) {
			list.add(new RecipeManager.RecipeIndex('m', new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK)));
		}
		ItemStack stack = new ItemStack(LogisticsPipes.LogisticsChips, 1, ItemLogisticsChips.ITEM_CHIP_PROGRAMMABLE);
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null) {
			nbt = new NBTTagCompound();
		}
		LogisticsPipes.LogisticsChips.addModuleIdToProgrammChip(nbt, moduleID);
		stack.setTagCompound(nbt);
		list.add(new RecipeManager.RecipeIndex('c', stack));
		list.add(new RecipeManager.RecipeIndex('f', parts.getChipFpga()));
		list.add(new RecipeManager.RecipeIndex('b', parts.getChipBasic()));
		list.add(new RecipeManager.RecipeIndex('a', parts.getChipAdvanced()));
		list.addAll(Arrays.asList(addition));

		List<Object> finalList = new ArrayList<>();
		finalList.add(new RecipeManager.RecipeLayout(
				line1,
				line2,
				line3
		));
		finalList.addAll(list.stream().filter(value -> (line1 + line2 + line3).contains(String.valueOf(value.getIndex()))).collect(Collectors.toList()));

		RecipeManager.craftingManager.addModuleRecipe(new ItemStack(LogisticsPipes.ModuleItem, 1, moduleID), finalList.toArray());
	}

	@Override
	protected void loadRecipes(CraftingParts parts) {
		ItemStack logisticsBlockFrame = new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_BLOCK_FRAME);

		RecipeManager.craftingManager.addRecipe(new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_POWER_JUNCTION),
				new RecipeManager.RecipeLayout(
						" c ",
						"rfr",
						"ibi"
				),
				new RecipeManager.RecipeIndex('c', parts.getChipBasic()),
				new RecipeManager.RecipeIndex('i', "ingotIron"),
				new RecipeManager.RecipeIndex('r', "dustRedstone"),
				new RecipeManager.RecipeIndex('f', logisticsBlockFrame),
				new RecipeManager.RecipeIndex('b', Blocks.redstone_block));

		RecipeManager.craftingManager.addRecipe(new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_SECURITY_STATION),
				new RecipeManager.RecipeLayout(
						" g ",
						"rfr",
						"iri"
				),
				new RecipeManager.RecipeIndex('g', parts.getChipAdvanced()),
				new RecipeManager.RecipeIndex('r', "dustRedstone"),
				new RecipeManager.RecipeIndex('f', logisticsBlockFrame),
				new RecipeManager.RecipeIndex('i', "ingotIron"));

		RecipeManager.craftingManager.addRecipe(new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_STATISTICS_TABLE),
				new RecipeManager.RecipeLayout(
						" g ",
						"rfr",
						" i "
				),
				new RecipeManager.RecipeIndex('g', parts.getChipAdvanced()),
				new RecipeManager.RecipeIndex('r', "dustRedstone"),
				new RecipeManager.RecipeIndex('f', logisticsBlockFrame),
				new RecipeManager.RecipeIndex('i', "ingotIron"));

		RecipeManager.craftingManager.addRecipe(new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_FUZZYCRAFTING_TABLE),
				new RecipeManager.RecipeLayoutSmall(
						"c",
						"t"
				),
				new RecipeManager.RecipeIndex('c', parts.getChipBasic()),
				new RecipeManager.RecipeIndex('t', new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_AUTOCRAFTING_TABLE)));

		RecipeManager.craftingManager.addRecipe(new ItemStack(LogisticsPipes.logisticsRequestTable),
				new RecipeManager.RecipeLayout(
						" c ",
						"rfp",
						" k "
				),
				new RecipeManager.RecipeIndex('c', parts.getChipAdvanced()),
				new RecipeManager.RecipeIndex('r', LogisticsPipes.LogisticsRequestPipeMk2),
				new RecipeManager.RecipeIndex('f', new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_BLOCK_FRAME)),
				new RecipeManager.RecipeIndex('k', Blocks.chest),
				new RecipeManager.RecipeIndex('p', LogisticsPipes.LogisticsCraftingPipeMk1));

		RecipeManager.craftingManager.addRecipe(new ItemStack(LogisticsPipes.LogisticsBasicPipe),
				new RecipeManager.RecipeLayoutSmall(
						"f",
						"p"
				),
				new RecipeManager.RecipeIndex('f', parts.getChipFpga()),
				new RecipeManager.RecipeIndex('p', new ItemStack(LogisticsPipes.BasicTransportPipe)));


		createModuleRecipe(ModuleRecipeTemplate.BASIC, ItemModule.ITEMSINK, parts);
		createModuleRecipe(ModuleRecipeTemplate.BASIC, ItemModule.PASSIVE_SUPPLIER, parts);
		createModuleRecipe(ModuleRecipeTemplate.VERSION2, ItemModule.EXTRACTOR, parts);
		createModuleRecipe(ModuleRecipeTemplate.BASIC, ItemModule.POLYMORPHIC_ITEMSINK, parts);
		createModuleRecipe(ModuleRecipeTemplate.VERSION3, ItemModule.QUICKSORT, parts);
		createModuleRecipe(ModuleRecipeTemplate.BASIC, ItemModule.TERMINUS, parts);
		createModuleRecipe("c", "f", "m", ItemModule.ADVANCED_EXTRACTOR, parts,
				new RecipeManager.RecipeIndex('m', new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR)));
		createModuleRecipe(ModuleRecipeTemplate.VERSION4, ItemModule.EXTRACTOR_MK2, parts,
				new RecipeManager.RecipeIndex('m', new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR)));
		createModuleRecipe(ModuleRecipeTemplate.VERSION4, ItemModule.ADVANCED_EXTRACTOR_MK2, parts,
				new RecipeManager.RecipeIndex('m', new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR)));
		createModuleRecipe(ModuleRecipeTemplate.VERSION5, ItemModule.EXTRACTOR_MK3, parts,
				new RecipeManager.RecipeIndex('m', new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK2)));
		createModuleRecipe(ModuleRecipeTemplate.VERSION5, ItemModule.ADVANCED_EXTRACTOR_MK3, parts,
				new RecipeManager.RecipeIndex('m', new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR_MK2)));
		createModuleRecipe("fcf", "lbl", "imi", ItemModule.PROVIDER, parts);
		createModuleRecipe("fcf", "lal", "gmg", ItemModule.PROVIDER_MK2, parts,
				new RecipeManager.RecipeIndex('m', new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PROVIDER)));
		createModuleRecipe(ModuleRecipeTemplate.BASIC2, ItemModule.MODBASEDITEMSINK, parts);
		createModuleRecipe(ModuleRecipeTemplate.BASIC2, ItemModule.OREDICTITEMSINK, parts);
		createModuleRecipe(ModuleRecipeTemplate.BASIC2, ItemModule.CREATIVETABBASEDITEMSINK, parts);
		createModuleRecipe(ModuleRecipeTemplate.BASIC, ItemModule.ENCHANTMENTSINK, parts);
		createModuleRecipe(ModuleRecipeTemplate.BASIC2, ItemModule.ENCHANTMENTSINK_MK2, parts,
				new RecipeManager.RecipeIndex('m', new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ENCHANTMENTSINK)));
		createModuleRecipe("fcf", "lbl", "gmg", ItemModule.CRAFTER, parts);
		createModuleRecipe("bcb", "lal", "gmg", ItemModule.CRAFTER_MK2, parts,
				new RecipeManager.RecipeIndex('m', new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CRAFTER)));
		createModuleRecipe("aca", "pbp", "gmg", ItemModule.CRAFTER_MK3, parts,
				new RecipeManager.RecipeIndex('m', new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CRAFTER_MK2)),
				new RecipeManager.RecipeIndex('p', Items.blaze_powder));
		createModuleRecipe("bcb", "pap", "gmg", ItemModule.ACTIVE_SUPPLIER, parts,
				new RecipeManager.RecipeIndex('p', Items.blaze_powder));
	}

	@Override
	protected void loadPlainRecipes() {
	}
}
