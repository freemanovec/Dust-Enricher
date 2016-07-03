package dustenricher.common;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import dustenricher.blocks.*;
import mekanism.common.Mekanism;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

@Mod(modid = ResourcesDNM.modid, version = ResourcesDNM.version, canBeDeactivated = false, dependencies = "required-after:Mekanism")
public class Main {
	public static final String MODID = "dustenricher";
	
	@SidedProxy(clientSide="dustenricher.common.ClientProxy",serverSide="dustenricher.common.ServerProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		proxy.preInit(event);
	}
	@EventHandler
	public void init(FMLInitializationEvent event){
		proxy.init(event);
		
		initItems();
		initBlocks();
	}
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		proxy.postInit(event);
	}
	void initBlocks(){
		Block dustInjectionChamber = new DustInjectionChamber().setBlockName("dustInjectionChamber").setCreativeTab(Mekanism.tabMekanism);
		
		GameRegistry.registerBlock(dustInjectionChamber, "dustInjectionChamber");
	}
	void initItems(){
		
	}
}
