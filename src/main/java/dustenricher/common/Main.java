package dustenricher.common;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import dustenricher.gui.GUIHandler;
import dustenricher.tileentities.DustInjectionChamberTE;
import ic2.core.Ic2Items;
import mekanism.common.MekanismItems;
import mekanism.common.Resource;
import mekanism.common.recipe.RecipeHandler;
import dustenricher.blocks.DustInjectionChamberBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mod(modid = ResourcesDNM.modid, version = ResourcesDNM.version, canBeDeactivated = false, dependencies = "required-after:Mekanism")
public class Main {
	public static final String MODID = "dustenricher";
	
	public static DustEnricherCreativeTab tabDustEnricher = new DustEnricherCreativeTab();
	
	@SidedProxy(clientSide="dustenricher.common.ClientProxy",serverSide="dustenricher.common.ServerProxy")
	public static CommonProxy proxy;
	
	@Instance
	public static Main instance = new Main();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		proxy.preInit(event);
	}
	@EventHandler
	public void init(FMLInitializationEvent event){
		proxy.init(event);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GUIHandler());
		initItems();
		initBlocks();
	}
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		proxy.postInit(event);
		initRecipes();
	}

	void initBlocks(){
		GameRegistry.registerBlock(new DustInjectionChamberBlock(), "dustInjectionChamber").setCreativeTab(tabDustEnricher);
		GameRegistry.registerTileEntity(DustInjectionChamberTE.class, "dustInjectionChamberTE");
	}
	void initItems(){
		Item dustDirtToRegister = new Item().setUnlocalizedName("dustDirt").setCreativeTab(tabDustEnricher).setTextureName("dustenricher:dustDirt");
		GameRegistry.registerItem(dustDirtToRegister, "dustDirt");
		dustDirtItem = dustDirtToRegister;		
	}
	Item dustDirtItem;
	void initRecipes(){
		ItemStack dustIron = new ItemStack(MekanismItems.Dust, 1, Resource.IRON.ordinal());
		ItemStack dustGold = new ItemStack(MekanismItems.Dust, 1, Resource.GOLD.ordinal());
		ItemStack dustOsmium = new ItemStack(MekanismItems.Dust, 1, Resource.OSMIUM.ordinal());
		ItemStack dustObsidian = new ItemStack(MekanismItems.OtherDust, 2, 6);
		ItemStack dustDiamond = new ItemStack(MekanismItems.OtherDust, 1, 0);
		ItemStack dustSteel = new ItemStack(MekanismItems.OtherDust, 1, 1);
		ItemStack dustCopper = new ItemStack(MekanismItems.Dust, 1, Resource.COPPER.ordinal());
		ItemStack dustTin = new ItemStack(MekanismItems.Dust, 1, Resource.TIN.ordinal());
		ItemStack dustSilver = new ItemStack(MekanismItems.Dust, 1, Resource.SILVER.ordinal());
		ItemStack dustLead = new ItemStack(MekanismItems.Dust, 1, Resource.LEAD.ordinal());
		ItemStack dustSulfur = new ItemStack(MekanismItems.OtherDust, 1, 3);
		ItemStack dustLithium = new ItemStack(MekanismItems.OtherDust, 1, 4);
		
		System.out.println("------------------------------------------------------------------");
		System.out.println("Dust tin: " + dustTin.getDisplayName() + " , " + dustTin.getItemDamage());
		
		ItemStack dustCoal = Ic2Items.coalDust;
		ItemStack dustBronze = Ic2Items.bronzeDust;
		ItemStack dustClay = Ic2Items.clayDust;
		ItemStack dustStone = Ic2Items.stoneDust;
		ItemStack dustLapisLazuli = Ic2Items.lapiDust;
		
		ItemStack dustGlowstone = new ItemStack(Items.glowstone_dust,1);
		ItemStack redstone = new ItemStack(Items.redstone,1);
		
		ItemStack dustDirt = new ItemStack(dustDirtItem,1);

		Recipes.AddRecipe(new Recipe(new ItemStack(Items.iron_ingot),new ItemStack(Items.coal),dustGlowstone));
		Recipes.AddRecipe(new Recipe(dustTin,dustCoal,dustGlowstone));
		/*Recipes.AddRecipe(new Recipe(dustTin,dustCopper,dustBronze));
		Recipes.AddRecipe(new Recipe(dustCoal,dustDirt,dustClay));
		RecipeHandler.addCrusherRecipe(new ItemStack(Blocks.dirt), new ItemStack(dustDirt.getItem(),8,dustDirt.getItemDamage()));
		Recipes.AddRecipe(new Recipe(dustDirt,dustStone,dustCoal));
		Recipes.AddRecipe(new Recipe(dustTin,dustDirt,dustCopper));
		Recipes.AddRecipe(new Recipe(dustCopper,redstone,dustGold));
		Recipes.AddRecipe(new Recipe(dustTin,dustStone,dustIron));
		Recipes.AddRecipe(new Recipe(dustTin,dustGlowstone,dustSilver));
		Recipes.AddRecipe(new Recipe(dustCoal,dustStone,dustTin));
		Recipes.AddRecipe(new Recipe(dustSulfur,dustCoal,dustLead));
		Recipes.AddRecipe(new Recipe(dustDiamond,dustCoal,dustObsidian));
		Recipes.AddRecipe(new Recipe(dustIron,dustGlowstone,dustLapisLazuli));
		Recipes.AddRecipe(new Recipe(dustGold,dustGlowstone,dustSulfur));
		Recipes.AddRecipe(new Recipe(dustSilver,dustStone,dustLithium));
		Recipes.AddRecipe(new Recipe(dustSulfur,dustLithium,dustDiamond));
		Recipes.AddRecipe(new Recipe(dustTin,dustIron,dustOsmium));
		Recipes.AddRecipe(new Recipe(dustIron,dustCoal,dustSteel));*/
		
		for(Recipe recipe : Recipes.recipes_DustInjectionChamber){
			System.out.println("Recipe for " + recipe.getOutput().getDisplayName() + "(" + recipe.getOutput().getItemDamage() + ")" + " from " + recipe.getInput().getDisplayName() + "(" + recipe.getOutput().getItemDamage() + ")" + " with infusion of " + recipe.getInfuse().getDisplayName() + "(" + recipe.getOutput().getItemDamage() + ")" + " loaded.");
		}
	}
}
