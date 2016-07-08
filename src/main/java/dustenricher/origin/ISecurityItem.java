package dustenricher.origin;

import dustenricher.origin.ISecurityTile.SecurityMode;
import mekanism.common.security.IOwnerItem;
import net.minecraft.item.ItemStack;

public interface ISecurityItem extends IOwnerItem
{	
	public SecurityMode getSecurity(ItemStack stack);
	
	public void setSecurity(ItemStack stack, SecurityMode mode);
	
	public boolean hasSecurity(ItemStack stack);
}
