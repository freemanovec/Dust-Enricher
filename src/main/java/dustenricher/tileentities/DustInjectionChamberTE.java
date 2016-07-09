package dustenricher.tileentities;

import dustenricher.common.Recipes;
import mekanism.api.IConfigurable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class DustInjectionChamberTE extends TileEntity implements IInventory, IConfigurable{
	
	private ItemStack[] inventory = new ItemStack[4];
	
	public DustInjectionChamberTE(){
	}
	
	public double getScaledProgress(){
		double progress = (double)ticks_running/(double)ticks_required;
		System.out.println("Progress: " + progress);
		if(progress>1)
			return 1;
		else
			return progress;
	}
	public double getEnergy(){
		return 5000;
	}
	public double getMaxEnergy(){
		return 10000;
	}
	///--PROCESSING--\\\
	
	public int ticks_running = 0;
	public int ticks_required = 40;
	public Slot slot_input, slot_infuse, slot_output, slot_energy;
	
	@Override
	public void updateEntity(){
		if(slot_input==null||slot_infuse==null||slot_output==null||slot_energy==null)
			return;
		if(slot_input.getStack()==null||slot_infuse.getStack()==null){
			ticks_running=0;
			return;
		}
		//start of processing
		Item output = Recipes.getOutputFrom(slot_input.getStack().getItem(), slot_infuse.getStack().getItem());
		if(output==null)
			return;
		//we can now start
		if(ticks_running>=ticks_required){
			processingFinished(output);
		}else{
			//processing not finished, increment our progress meter
			ticks_running++;
		}
	}
	public void processingFinished(Item output){
		if(!slot_output.getHasStack()){
			//nothing in output slot, we can proceed
			processingFinalize(output);
		}else{
			//something in output slot
			if(slot_output.getStack().getItem()==output){
				//item in output slot is our desired item
				if(slot_output.getStack().stackSize<64){
					//we can still add one
					processingFinalize(output);
				}else{
					//output slot is full
					return;
				}
			}else{
				//item in output slot is not our desired item
				return;
			}
		}
	}
	public void processingFinalize(Item output){
		slot_input.decrStackSize(1);
		slot_infuse.decrStackSize(1);
		if(slot_output.getHasStack()){
			slot_output.putStack(new ItemStack(output,slot_output.getStack().stackSize+1));
		}else{
			slot_output.putStack(new ItemStack(output,1));
		}
		//reset running meter
		ticks_running=0;
	}
	
	///--END OF PROCESSING--\\\
	///--TEXTURE CHANGE--\\\
	public void setActiveTexture(boolean active){
		int facing = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		System.out.println("Actual metadata is " + worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
  		if(active){
 			System.out.println("Setting to true");
  			if(facing<5){
  				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, facing+4, 1);
  				System.out.println("New metadata is " + worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
 				System.out.println("Set active!");
  			}
  		}else{
 			System.out.println("Setting to false");
  			if(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)>3){
  				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord)-4, 1);
 				System.out.println("Set inactive!");
  			}
  		}
	}
	///--END OF TEXTURE CHANGE--\\\
	
	
	///--INVENTORY STUFF, DO NOT CHANGE--\\\

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if (index < 0 || index >= this.getSizeInventory())
	        return null;
	    return this.inventory[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (this.getStackInSlot(index) != null) {
	        ItemStack itemstack;

	        if (this.getStackInSlot(index).stackSize <= count) {
	            itemstack = this.getStackInSlot(index);
	            this.setInventorySlotContents(index, null);
	            this.markDirty();
	            return itemstack;
	        } else {
	            itemstack = this.getStackInSlot(index).splitStack(count);

	            if (this.getStackInSlot(index).stackSize <= 0) {
	                this.setInventorySlotContents(index, null);
	            } else {
	                //Just to show that changes happened
	                this.setInventorySlotContents(index, this.getStackInSlot(index));
	            }

	            this.markDirty();
	            return itemstack;
	        }
	    } else {
	        return null;
	    }
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		ItemStack stack = this.getStackInSlot(index);
	    this.setInventorySlotContents(index, null);
	    return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (index < 0 || index >= this.getSizeInventory())
	        return;

	    if (stack != null && stack.stackSize > this.getInventoryStackLimit())
	        stack.stackSize = this.getInventoryStackLimit();
	        
	    if (stack != null && stack.stackSize == 0)
	        stack = null;

	    this.inventory[index] = stack;
	    this.markDirty();
	}

	@Override
	public String getInventoryName() {
		return "container.dic_tile_entity";
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
		return this.worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + .5f, yCoord + .5f, zCoord + .5f) <= 64;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		System.out.println("Slot: " + slot);
		return true;
		//TODO filter items based on recipes and slot
	}
	
	///--END OF INVENTORY STUFF--\\\
	///--START OF DEBUG STUFF--\\\

	@Override
	public boolean onSneakRightClick(EntityPlayer player, int side) {
		setActiveTexture(false);
		return true;
	}

	@Override
	public boolean onRightClick(EntityPlayer player, int side) {
		setActiveTexture(true);
		return true;
	}
	///--END OF DEBUG STUFF--\\\

}
