package dustenricher.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Recipe {
	private String input;
	private String infuse;
	private String output;
	public String getInput(){
		return input;
	}
	public String getInfuse(){
		return infuse;
	}
	public String getOutput(){
		return output;
	}
	public Recipe(String _input, String _infuse, String _output){
		boolean hasInput = OreDict.hasKey(_input);
		boolean hasInfuse = OreDict.hasKey(_infuse);
		boolean hasOutput = OreDict.hasKey(_output);
		boolean success = hasInput&&hasInfuse&&hasOutput;
		if(success)
			System.out.println("Recipe initialization successful - " + "Recipe(INPUT:"+_input+"+INFUSE:"+_infuse+"=OUTPUT:"+_output+")");
		else
			System.out.println("Recipe initialization failed - " + "Recipe(INPUT:"+_input+"+INFUSE:"+_infuse+"=OUTPUT:"+_output+")");
		this.input = _input;
		this.infuse = _infuse;
		this.output = _output;
	}
	@Override
	public String toString(){
		return "Recipe(INPUT:"+this.input+"+INFUSE:"+this.infuse+"=OUTPUT:"+this.output+")";
	}
}
