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
import net.minecraft.util.MathHelper;

public class DustInjectionChamber extends BlockContainer{
	
	public DustInjectionChamber() {
		super(Material.iron);
		this.setHardness(3.5f);
		this.setResistance(8f);
		this.textureName = "dustenricher:SteelCasing";
	}
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new DustInjectionChamberTE();
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
	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6)
    {
		System.out.println("breakBlock called, parsing arguments to dropItems");
        dropItems(world,x,y,z, par5);
        super.breakBlock(world, x, y, z, par5, par6);
    }
	//@SideOnly(Side.SERVER)
	private void dropItems(World world, int x, int y, int z, Block block){
		DustInjectionChamberTE tileEntity = (DustInjectionChamberTE)world.getTileEntity(x, y, z);
		if(tileEntity!=null){
			Random rand = new Random();
			for(int i=0;i<tileEntity.getSizeInventory();i++){
				ItemStack itemStack = tileEntity.getStackInSlot(i);
				//DEBUG CHANGE
				//ItemStack itemStack = new ItemStack(Items.gold_ingot,50);
				//END DEBUG CHANGE
				if(itemStack!=null){
					float f = rand.nextFloat() * 0.8F + 0.1F;
                    float f1 = rand.nextFloat() * 0.8F + 0.1F;
                    float f2 = rand.nextFloat() * 0.8F + 0.1F;
                    while(itemStack.stackSize>0){
                    	int j = rand.nextInt(21)+10;
                    	if(j>itemStack.stackSize)
                    		j=itemStack.stackSize;
                    	
                    	itemStack.stackSize -= j;
                    	EntityItem entityItem = new EntityItem(world,x+f,y+f1,z+f2,new ItemStack(itemStack.getItem(),j,itemStack.getItemDamage()));
                    	
                    	if(itemStack.hasTagCompound())
                    		entityItem.getEntityItem().setTagCompound((NBTTagCompound)itemStack.getTagCompound().copy());
                    	float f3 = 0.05F;
                        entityItem.motionX = (double)((float)rand.nextGaussian() * f3);
                        entityItem.motionY = (double)((float)rand.nextGaussian() * f3 + 0.2F);
                        entityItem.motionZ = (double)((float)rand.nextGaussian() * f3);
                        world.spawnEntityInWorld(entityItem);
                    }
				}
			}
			
			System.out.println("Item dropping done");
		}
		world.func_147453_f(x, y, z, block);
	}
}
