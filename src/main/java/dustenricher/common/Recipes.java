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
		ItemStack oneOfThem = new ItemStack(itemstack.getItem(),1,itemstack.getItemDamage());
		boolean inArray = false;
		for(Recipe recipe : recipes_DustInjectionChamber){
			//if(recipe.getInput().getItem()==oneOfThem.getItem()&&recipe.getInput().getItemDamage()==oneOfThem.getItemDamage())
			if(recipe.getInput().getItem()==itemstack.getItem()&&recipe.getInput().getItemDamage()==itemstack.getItemDamage()){
				//System.out.println("Input is valid for " + itemstack + " in recipe " + recipe.toString());
				return true;
			}
		}
		System.out.println("Input not valid for " + itemstack);
		return false;
	}
	public static boolean isValidInfuse(ItemStack itemstack){
		ItemStack oneOfThem = new ItemStack(itemstack.getItem(),1,itemstack.getItemDamage());
		boolean inArray = false;
		for(Recipe recipe : recipes_DustInjectionChamber){
			if(recipe.getInfuse().getItem()==oneOfThem.getItem()&&recipe.getInfuse().getItemDamage()==oneOfThem.getItemDamage())
				return true;
			/*if(recipe.getInfuse()==oneOfThem){
				return true;
			}*/
		}
		return false;
	}
	public static boolean hasOutputFrom(ItemStack input, ItemStack infuse){
		if(getOutputFrom(input,infuse)==null)
			return false;
		else
			return true;
	}
	public static ItemStack getOutputFrom(ItemStack input, ItemStack infuse){
		if(!isValidInput(input) || !isValidInfuse(infuse)){
			System.out.println("Input/Infuse not valid");
			return null;
		}else{
			ArrayList<Recipe> foundRecipes = new ArrayList<Recipe>();
			//ItemStack oneOfThem = new ItemStack(input.getItem(),1,input.getItemDamage());
			for(Recipe recipe : recipes_DustInjectionChamber){
				if(recipe.getInput().getItem()==input.getItem()&&recipe.getInput().getItemDamage()==input.getItemDamage()){
					//System.out.println("Input valid");
					foundRecipes.add(recipe);
				}
			}
			for(Recipe recipe : foundRecipes){
				if(recipe.getInfuse().getItem()==infuse.getItem()&&recipe.getInfuse().getItemDamage()==infuse.getItemDamage()){
					return recipe.getOutput();
				}
			}
			System.out.println("Input/Infuse not found valid in recipes");
			return null;
		}
	}
}