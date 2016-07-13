package dustenricher.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Recipe {
	private ItemStack input;
	private ItemStack infuse;
	private ItemStack output;
	public ItemStack getInput(){
		return input;
	}
	public ItemStack getInfuse(){
		return infuse;
	}
	public ItemStack getOutput(){
		return output;
	}
	public Recipe(ItemStack _input, ItemStack _infuse, ItemStack _output){
		this.input = new ItemStack(_input.getItem(),1,_input.getItemDamage());
		this.infuse = new ItemStack(_infuse.getItem(),1,_infuse.getItemDamage());
		this.output = new ItemStack(_output.getItem(),1,_output.getItemDamage());
	}
}
