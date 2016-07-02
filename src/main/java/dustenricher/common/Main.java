package dustenricher.common;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import dustenricher.blocks.Blank;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

@Mod(modid = ResourcesDNM.modid, version = ResourcesDNM.version, canBeDeactivated = false, dependencies = "required-after:Mekanism")
public class Main {
	public static final String MODID = "dustenricher";
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		initItems();
		initBlocks();
	}
	void initBlocks(){
		Block dustInjectionChamber = new Blank(Material.iron).setBlockName("dustInjectionChamber");
		
		GameRegistry.registerBlock(dustInjectionChamber, "dustInjectionChamber");
	}
	void initItems(){
		
	}
}
