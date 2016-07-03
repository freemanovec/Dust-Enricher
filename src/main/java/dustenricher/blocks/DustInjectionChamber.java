package dustenricher.blocks;

import dustenricher.common.ResourcesDNM;
import dustenricher.tileentities.DustInjectionChamberTE;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class DustInjectionChamber extends BlockContainer{

	IIcon[] icons = new IIcon[6];
	
	@Override
	public void registerBlockIcons(IIconRegister reg){
		icons[0] = reg.registerIcon("dustenricher:SteelCasing");
		icons[1] = reg.registerIcon("dustenricher:SteelCasing");
		icons[3] = reg.registerIcon("dustenricher:DustInjectionChamberFront");
		icons[2] = reg.registerIcon("dustenricher:SteelCasing");
		icons[4] = reg.registerIcon("dustenricher:SteelCasing");
		icons[5] = reg.registerIcon("dustenricher:SteelCasing");
	}
	@Override
	public IIcon getIcon(int side, int meta){
		return icons[side];
	}
	
	public DustInjectionChamber() {
		super(Material.iron);
		this.setHardness(3.5f);
		this.setResistance(8f);
	}
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new DustInjectionChamberTE();
	}

}
