package dustenricher.common;

import mekanism.common.MekanismItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DustEnricherCreativeTab extends CreativeTabs{

	public DustEnricherCreativeTab() {
		super("tabDustEnricher");
	}
	
	@Override
	public ItemStack getIconItemStack()
	{
		return new ItemStack(MekanismItems.CompressedCarbon);
	}

	@Override
	public Item getTabIconItem() 
	{
		return MekanismItems.CompressedCarbon;
	}

}
