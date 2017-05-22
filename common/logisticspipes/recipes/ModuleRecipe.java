package logisticspipes.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.oredict.ShapedOreRecipe;

import logisticspipes.utils.item.ItemIdentifier;

public class ModuleRecipe extends ShapedOreRecipe {

	private final int xPos;
	private final int yPos;

	public ModuleRecipe(ItemStack result, Object... recipe) {
		super(result, recipe);
		if(((String)recipe[0]).contains("c")) {
			xPos = ((String)recipe[0]).indexOf('c');
			yPos = 0;
		} else if(((String)recipe[1]).contains("c")) {
			xPos = ((String)recipe[1]).indexOf('c');
			yPos = 1;
		} else if(((String)recipe[2]).contains("c")) {
			xPos = ((String)recipe[2]).indexOf('c');
			yPos = 2;
		} else {
			xPos = 0;
			yPos = 0;
		}
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		boolean result = super.matches(inv, world);
		if(result) {
			ItemStack stack = inv.getStackInSlot(xPos + yPos * 3);
			Object input = this.getInput()[xPos + yPos * 3];
			if (input instanceof ItemStack) {
				return ItemIdentifier.get(stack).equals(ItemIdentifier.get((ItemStack) input));
			}
		}
		return result;
	}
}
