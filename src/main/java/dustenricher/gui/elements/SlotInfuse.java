package dustenricher.gui.elements;

import dustenricher.common.Recipes;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotInfuse extends Slot{
	
	public SlotInfuse(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}
	
	@Override
	public boolean isItemValid(ItemStack itemstack){
		return Recipes.isValidInfuse(itemstack);
	}
}
