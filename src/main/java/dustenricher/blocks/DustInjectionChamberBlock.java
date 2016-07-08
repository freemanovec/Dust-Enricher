package dustenricher.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustenricher.common.Main;
import dustenricher.tileentities.DustInjectionChamberTE;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class DustInjectionChamberBlock extends BlockContainer{
	
	IIcon iconCasing;
	IIcon iconFrontOff;
	IIcon iconFrontOn;
	
	public DustInjectionChamberBlock() {
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
	public void registerBlockIcons(IIconRegister reg){
		iconCasing = reg.registerIcon("dustenricher:SteelCasing");
		iconFrontOff = reg.registerIcon("dustenricher:DustInjectionChamberFrontOff");
		iconFrontOn = reg.registerIcon("dustenricher:DustInjectionChamberFrontOn");
	}
	@Override
	public IIcon getIcon(int side, int meta){
		if(meta==0&&side==3)
			return iconFrontOff;
		if(meta==1&&side==4)
			return iconFrontOff;
		if(meta==2&&side==2)
			return iconFrontOff;
		if(meta==3&&side==5)
			return iconFrontOff;
		if(meta==5&&side==2)
			return iconFrontOn;
		if(meta==6&&side==5)
			return iconFrontOn;
		if(meta==7&&side==3)
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
	//@SideOnly(Side.SERVER)
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemstack){
		int rotation = MathHelper.floor_double((double)((entityLiving.rotationYaw * 4F) / 360F) + 2.5D) & 3;
		System.out.println("Rotation: " + rotation);
		world.setBlockMetadataWithNotify(x, y, z, rotation, 1);
	}
}
