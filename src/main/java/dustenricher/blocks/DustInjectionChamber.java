package dustenricher.blocks;

import dustenricher.common.ResourcesDNM;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class DustInjectionChamber extends Block{

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

}
