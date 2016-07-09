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
		initRecipes();
	}
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		proxy.postInit(event);
	}

	void initBlocks(){
		GameRegistry.registerBlock(new DustInjectionChamberBlock(), "dustInjectionChamber").setCreativeTab(tabDustEnricher);
		GameRegistry.registerTileEntity(DustInjectionChamberTE.class, "dustInjectionChamberTE");
	}
	void initItems(){
		Item dustDirtToRegister = new Item().setUnlocalizedName("dustDirt").setCreativeTab(tabDustEnricher).setTextureName("dustenricher:dustDirt");
		GameRegistry.registerItem(dustDirtToRegister, "dustDirt");
		dustDirt = dustDirtToRegister;		
	}
	Item dustDirt;
	void initRecipes(){
		Item dustIron = GameRegistry.findItem("Mekanism", "ironDust");
		Item dustGold = GameRegistry.findItem("Mekanism", "goldDust");
		Item dustOsmium = GameRegistry.findItem("Mekanism", "osmiumDust");
		Item dustObsidian = GameRegistry.findItem("Mekanism", "obsidianDust");
		Item dustDiamond = GameRegistry.findItem("Mekanism", "diamondDust");
		Item dustSteel = GameRegistry.findItem("Mekanism", "steelDust");
		Item dustCopper = GameRegistry.findItem("Mekanism", "copperDust");
		Item dustTin = GameRegistry.findItem("Mekanism", "tinDust");
		Item dustSilver = GameRegistry.findItem("Mekanism", "silverDust");
		Item dustLead = GameRegistry.findItem("Mekanism", "leadDust");
		Item dustSulfur = GameRegistry.findItem("Mekanism", "sulfurDust");
		Item dustLithium = GameRegistry.findItem("Mekanism", "lithiumDust");
		Item dustCoal = Ic2Items.coalDust.getItem();
		Item dustBronze = Ic2Items.bronzeDust.getItem();
		Item dustClay = Ic2Items.clayDust.getItem();
		Item dustStone = Ic2Items.stoneDust.getItem();
		Item dustLapisLazuli = Ic2Items.lapiDust.getItem();
		Recipes.AddRecipe(new Recipe(dustTin,dustCoal,Items.glowstone_dust));
		Recipes.AddRecipe(new Recipe(dustTin,dustCopper,dustBronze));
		Recipes.AddRecipe(new Recipe(dustCoal,dustDirt,dustClay));
		RecipeHandler.addCrusherRecipe(new ItemStack(Blocks.dirt), new ItemStack(dustDirt));
		Recipes.AddRecipe(new Recipe(dustDirt,dustStone,dustCoal));
		Recipes.AddRecipe(new Recipe(dustTin,dustCoal,dustCopper));
		Recipes.AddRecipe(new Recipe(dustCopper,Items.redstone,dustGold));
		Recipes.AddRecipe(new Recipe(dustTin,dustStone,dustIron));
		Recipes.AddRecipe(new Recipe(dustTin,Items.glowstone_dust,dustSilver));
		Recipes.AddRecipe(new Recipe(dustCoal,dustStone,dustTin));
		Recipes.AddRecipe(new Recipe(dustSulfur,dustCoal,dustLead));
		Recipes.AddRecipe(new Recipe(dustDiamond,dustCoal,dustObsidian));
		Recipes.AddRecipe(new Recipe(dustIron,Items.glowstone_dust,dustLapisLazuli));
		Recipes.AddRecipe(new Recipe(dustGold,Items.glowstone_dust,dustSulfur));
		Recipes.AddRecipe(new Recipe(dustSilver,dustStone,dustLithium));
		Recipes.AddRecipe(new Recipe(dustSulfur,dustLithium,dustDiamond));
		Recipes.AddRecipe(new Recipe(dustTin,dustIron,dustOsmium));
		Recipes.AddRecipe(new Recipe(dustIron,dustCoal,dustSteel));
	}
}
