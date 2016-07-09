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
import dustenricher.blocks.DustInjectionChamberBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

@Mod(modid = ResourcesDNM.modid, version = ResourcesDNM.version, canBeDeactivated = false, dependencies = "required-after:Mekanism")
public class Main {
	public static final String MODID = "dustenricher";
	
	public static DustEnricherCreativeTab tabDustEnricher = new DustEnricherCreativeTab();
	public static SimpleNetworkWrapper testChannel;
	
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
		
	}
	void initRecipes(){
		Recipes.AddRecipe(new Recipe(Items.apple,Items.gold_ingot,Items.golden_apple));
		Recipes.AddRecipe(new Recipe(Items.coal,Items.redstone,Items.diamond));
	}
}
