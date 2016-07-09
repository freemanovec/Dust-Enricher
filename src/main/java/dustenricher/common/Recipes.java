package dustenricher.common;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class Recipes {
	public static ArrayList<Recipe> recipes_DustInjectionChamber = new ArrayList<Recipe>();
	
	public static boolean AddRecipe(Recipe recipe){
		if(recipes_DustInjectionChamber.contains(recipe))
			return false;
		else{
			recipes_DustInjectionChamber.add(recipe);
			return true;
		}
	}
	
	public static boolean isValidInput(ItemStack itemstack){
		Item item = itemstack.getItem();
		boolean inArray = false;
		for(Recipe recipe : recipes_DustInjectionChamber){
			if(recipe.getInput()==item){
				return true;
			}
		}
		return false;
	}
	public static boolean isValidInfuse(ItemStack itemstack){
		Item item = itemstack.getItem();
		boolean inArray = false;
		for(Recipe recipe : recipes_DustInjectionChamber){
			if(recipe.getInfuse()==item){
				return true;
			}
		}
		return false;
	}
	public static boolean hasOutputFrom(Item input, Item infuse){
		if(getOutputFrom(input,infuse)==null)
			return false;
		else
			return true;
	}
	public static Item getOutputFrom(Item input, Item infuse){
		if(!isValidInput(new ItemStack(input)) || !isValidInfuse(new ItemStack(infuse)))
			return null;
		else{
			ArrayList<Recipe> foundRecipes = new ArrayList<Recipe>();
			for(Recipe recipe : recipes_DustInjectionChamber){
				if(recipe.getInput()==input){
					foundRecipes.add(recipe);
				}
			}
			for(Recipe recipe : foundRecipes){
				if(recipe.getInfuse()==infuse){
					return recipe.getOutput();
				}
			}
			return null;
		}
	}
}