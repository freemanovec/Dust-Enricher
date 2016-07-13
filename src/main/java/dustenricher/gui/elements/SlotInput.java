package dustenricher.gui.elements;

import dustenricher.common.Recipes;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotInput extends Slot{

	public SlotInput(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}
	
	@Override
	public boolean isItemValid(ItemStack itemstack){
		return Recipes.isValidInput(itemstack);
	}
}
