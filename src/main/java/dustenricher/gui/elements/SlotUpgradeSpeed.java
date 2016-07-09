package dustenricher.gui.elements;

import dustenricher.common.Recipes;
import mekanism.common.MekanismItems;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotUpgradeSpeed extends Slot{
	public SlotUpgradeSpeed(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}
	
	@Override
	public boolean isItemValid(ItemStack itemstack){
		System.out.println("Validating");
		//return (itemstack.getItem()==MekanismItems.SpeedUpgrade);
		return true;
	}
}
