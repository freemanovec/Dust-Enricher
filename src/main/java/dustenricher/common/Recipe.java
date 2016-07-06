package dustenricher.common;

import net.minecraft.item.Item;

public class Recipe {
	private Item input;
	private Item infuse;
	private Item output;
	public Item getInput(){
		return input;
	}
	public Item getInfuse(){
		return infuse;
	}
	public Item getOutput(){
		return output;
	}
	public Recipe(Item _input, Item _infuse, Item _output){
		this.input = _input;
		this.infuse = _infuse;
		this.output = _output;
	}
}
