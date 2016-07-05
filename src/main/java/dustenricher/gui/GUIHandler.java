package dustenricher.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import dustenricher.tileentities.DustInjectionChamberTE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GUIHandler implements IGuiHandler{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z); 
		if(tileEntity instanceof DustInjectionChamberTE)
		{ 
			//System.out.println("Opening Container");
			return new DustInjectionChamberContainer(player.inventory, (DustInjectionChamberTE) tileEntity);
		} 
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if(tileEntity instanceof DustInjectionChamberTE){
			//System.out.println("Opening GUI");
			return new DustInjectionChamberGUI(player.inventory,(DustInjectionChamberTE)tileEntity);
		}
		return null;
	}

}
