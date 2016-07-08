package dustenricher.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustenricher.common.Main;
import dustenricher.common.ResourcesDNM;
import dustenricher.tileentities.DustInjectionChamberTE;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.DefIcon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.util.MathHelper;

public class DustInjectionChamber extends BlockContainer{
	
	IIcon iconCasing;
	IIcon iconFrontOff;
	IIcon iconFrontOn;
	
	public DustInjectionChamber() {
		super(Material.iron);
		this.setHardness(3.5f);
		this.setResistance(8f);
		this.textureName = "dustenricher:SteelCasing";
	}
	
	int id = 0;
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new DustInjectionChamberTE();
	}
	@Override
	public void registerBlockIcons(IIconRegister reg){
		iconCasing = reg.registerIcon("dustenricher:SteelCasing");
		iconFrontOff = reg.registerIcon("dustenricher:DustInjectionChamberFrontOff");
		iconFrontOn = reg.registerIcon("dustenricher:DustInjectionChamberFrontOn");
	}
	@Override
	public IIcon getIcon(int side, int meta){
		if(meta==0&&side==3)
			return iconFrontOff;
		if(meta==1&&side==2)
			return iconFrontOff;
		if(meta==2&&side==5)
			return iconFrontOff;
		if(meta==3&&side==3)
			return iconFrontOff;
		if(meta==4&&side==4)
			return iconFrontOff;
		if(meta==5&&side==2)
			return iconFrontOn;
		if(meta==6&&side==5)
			return iconFrontOn;
		if(meta==7&&side==3)
			return iconFrontOn;
		if(meta==8&&side==4)
			return iconFrontOn;
		return iconCasing;
	}
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float sideX, float sideY, float sideZ){
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if(tileEntity==null||player.isSneaking()){
			System.out.println("tileEntity is null");
			return false;
		}
		player.openGui(Main.instance, 0, world, x, y, z);
		return true;
	}	
}
