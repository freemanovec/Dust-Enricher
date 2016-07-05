package dustenricher.tileentities;

import mekanism.api.IConfigurable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class DustInjectionChamberTE extends TileEntity implements IInventory, IConfigurable{

	public void setFacing(int val){
		facing = val;
	}
	int facing = 1;
	
	private ItemStack[] inv;
	
	boolean metastateActive = false;
	public void setMetastate(boolean active){
		if(active){
			if(facing<5){
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, facing+4, 1);
				System.out.println("Set active!");
				metastateActive = true;
			}
		}else{
			System.out.println(facing);
			if(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)>4){
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord)-4, 1);
				System.out.println("Set inactive!");
				metastateActive = false;
			}
		}
	}
	
	public DustInjectionChamberTE(){
		inv = new ItemStack[9];
	}
	boolean setTo = false;
	@Override
	public boolean onSneakRightClick(EntityPlayer player, int side) {
		setTo = !setTo;
		setMetastate(setTo);
		System.out.println("Setting active to " + setTo);
		return true;
	}

	@Override
	public boolean onRightClick(EntityPlayer player, int side) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void onItemsChanged(){
		System.out.println("Facing: " + facing);
		System.out.println("Items changed");
	}
	
	@Override
	public int getSizeInventory() {
		return inv.length;
	}
	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv[slot];
	}
	@Override
	public ItemStack decrStackSize(int slot, int ammount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null)
		{ 
			if (stack.stackSize <= ammount) 
			{ 
				setInventorySlotContents(slot, null);
				} 
			else { 
				stack = stack.splitStack(ammount); 
				if (stack.stackSize == 0) 
				{ 
					setInventorySlotContents(slot, null);
					}
				}
			}
		return stack;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if(stack!=null){
			setInventorySlotContents(slot,null);
		}
		return stack;
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		inv[slot] = itemStack;
		if(itemStack!=null&&itemStack.stackSize > getInventoryStackLimit()){
			itemStack.stackSize = getInventoryStackLimit();
		}
		System.out.println("Slot on index " + slot + " has changed to " + itemStack);
	}
	@Override
	public String getInventoryName() {
		return "DustInjectionChamberGUI";
	}
	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		boolean sameEntity =worldObj.getTileEntity(xCoord, yCoord, zCoord)==this;
		boolean inRange = player.getDistanceSq(xCoord+0.5f, yCoord+0.5f, zCoord+0.5f)<64;
		return (sameEntity&&inRange);
	}
	@Override
	public void openInventory() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return true;
	}

}
