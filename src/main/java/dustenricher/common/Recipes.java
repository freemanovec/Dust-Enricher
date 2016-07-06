package dustenricher.common;

import java.util.ArrayList;

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
}
