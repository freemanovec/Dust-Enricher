package dustenricher.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustenricher.common.ResourcesDNM;
import dustenricher.tileentities.DustInjectionChamberTE;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.DefIcon;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
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
}
