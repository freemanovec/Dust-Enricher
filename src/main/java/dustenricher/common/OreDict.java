package dustenricher.common;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class OreDict {
	public static ItemStack getItemStack(String key){
		if(OreDictionary.doesOreNameExist(key)){
			ArrayList<ItemStack> toReturn = OreDictionary.getOres(key);
			return toReturn.get(0);
		}else
			return null;
	}
	public static Item getItem(String key){
		if(OreDictionary.doesOreNameExist(key)){
			ArrayList<ItemStack> toReturn = OreDictionary.getOres(key);
			return toReturn.get(0).getItem();
		}else
			return null;
	}
	public static boolean hasKey(String key){
		return OreDictionary.doesOreNameExist(key);
	}
	public static String getKey(ItemStack itemstack){
		if(itemstack==null)
			return "";
		ItemStack newItemstack = itemstack.copy();
		newItemstack.stackSize=1;
		int[] ids = OreDictionary.getOreIDs(newItemstack);
		if(ids.length<1)
			return "";
		int id = ids[0];
		String key = OreDictionary.getOreName(id);
		return key;
	}
}
