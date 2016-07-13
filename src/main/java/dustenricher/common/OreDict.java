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
		ItemStack newItemstack = itemstack.copy();
		newItemstack.stackSize=1;
		int id = OreDictionary.getOreIDs(newItemstack)[0];
		if(id==-1)
			return "";
		String key = OreDictionary.getOreName(id);
		//System.out.println("Returning key: " + key);
		return key;
	}
}
