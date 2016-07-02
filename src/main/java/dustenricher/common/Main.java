package dustenricher.common;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = ResourcesDNM.modid, version = ResourcesDNM.version, canBeDeactivated = false, dependencies = "required-after:Mekanism")
public class Main {
	public static final String MODID = "dustenricher";
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		
	}
}
