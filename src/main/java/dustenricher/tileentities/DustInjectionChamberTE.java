package dustenricher.tileentities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustenricher.common.Recipes;
import mekanism.api.IConfigurable;
import mekanism.api.energy.IStrictEnergyAcceptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class DustInjectionChamberTE extends TileEntity implements IInventory, IConfigurable, IStrictEnergyAcceptor{
	
	private ItemStack[] inventory = new ItemStack[4];
	
	public DustInjectionChamberTE(){
	}
	
	public double getScaledProgress(){
		double progress = (double)ticks_running/(double)ticks_required;
		if(progress>1)
			return 1;
		else
			return progress;
	}
	
	///--NETWORKING STUFF--\\\
	
	@Override
	public Packet getDescriptionPacket(){ //sending
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}
	@Override
	public void onDataPacket(NetworkManager networkManager, S35PacketUpdateTileEntity packet){ //receiving
		readFromNBT(packet.func_148857_g());
	}
	public void markForUpdate(){
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	///--END OF NETWORKING STUFF--\\\
	
	///--SAVING STUFF--\\\
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("energy", getEnergy());
		nbt.setInteger("ticks_running", ticks_running);

		NBTTagList list = new NBTTagList();
	    for (int i = 0; i < this.getSizeInventory(); ++i) {
	        if (this.getStackInSlot(i) != null) {
	            NBTTagCompound stackTag = new NBTTagCompound();
	            stackTag.setByte("Slot", (byte) i);
	            this.getStackInSlot(i).writeToNBT(stackTag);
	            list.appendTag(stackTag);
	        }
	    }
	    nbt.setTag("Items", list);
	}
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		setEnergy(nbt.getDouble("energy"));
		ticks_running = nbt.getInteger("ticks_running");
		if(!worldObj.isRemote){
			NBTTagList list = nbt.getTagList("Items", 10);
	    	for (int i = 0; i < list.tagCount(); ++i) {
	    	NBTTagCompound stackTag = list.getCompoundTagAt(i);
	        int slot = stackTag.getByte("Slot") & 255;
	        this.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(stackTag));
	    	}
		}
	}
	
	///--END OF SAVING STUFF--\\\
	///--ENERGY STUFF--\\\
	private double energy_internal = 0;
	private double energy_perItem = 2500;
	private double energy_perTickMaxInput = 1000;
	private double energy_max = 20000;
	@SideOnly(Side.CLIENT)
	public double client_getEnergy(){
		return energy_internal;
		//TODO handle packet server ---> client
	}
	public double getEnergy(){
		return energy_internal;
	}
	public double getMaxEnergy(){
		return energy_max;
	}
	public boolean addEnergy(double energy){
		energy_internal += energy;
		if(energy_internal>getMaxEnergy()){
			energy_internal = getMaxEnergy();
			return false;
		}
		return true;
	}
	public boolean removeEnergy(double energy){
		if(energy_internal-energy<0)
			return false;
		energy_internal -= energy;
		return true;
	}
	@Override
	public void setEnergy(double energy) {
		energy_internal = energy;
	}

	@Override
	public double transferEnergyToAcceptor(ForgeDirection side, double amount) {
		if(getEnergy()==getMaxEnergy())
			return 0;
		double possible = (getMaxEnergy()-getEnergy());
		if(possible>energy_perTickMaxInput)
			possible = energy_perTickMaxInput;
		if(possible-amount>=0){
			addEnergy(amount);
			return amount;
		}
		addEnergy(possible);
		return possible;
	}

	@Override
	public boolean canReceiveEnergy(ForgeDirection side) {
		return true;
	}	
	///--END OF ENERGY STUFF--\\\
	///--PROCESSING--\\\
	
	public int ticks_running = 0;
	public int ticks_required = 200;
	public Slot slot_input, slot_infuse, slot_output, slot_energy;
	
	@Override
	public void updateEntity(){
		markForUpdate();
		if(!worldObj.isRemote){
			if(slot_input==null||slot_infuse==null||slot_output==null||slot_energy==null)
				return;
			if(slot_input.getStack()==null||slot_infuse.getStack()==null){
				ticks_running=0;
				return;
			}
			//start of processing
			System.out.println("Input:  " + slot_input.getStack());
			System.out.println("Infuse: " + slot_infuse.getStack());
			ItemStack output = Recipes.getOutputFrom(slot_input.getStack(), slot_infuse.getStack());
			if(output==null){
				//System.out.println("Output is null");
				return;
			}
			//we can now start
			if(ticks_running>=ticks_required){
				processingFinished(output);
			}else{
				//processing not finished, increment our progress meter
				if(removeEnergy(energy_perItem/ticks_required))
					ticks_running++;
			}
		}
	}
	public void processingFinished(ItemStack output){
		if(!slot_output.getHasStack()){
			//nothing in output slot, we can proceed
			processingFinalize(output);
		}else{
			//something in output slot
			//get one from output slot
			ItemStack oneOfThem = new ItemStack(slot_output.getStack().getItem(),1,slot_output.getStack().getItemDamage());
			if(oneOfThem==output){
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
	public void processingFinalize(ItemStack output){
		slot_input.decrStackSize(1);
		slot_infuse.decrStackSize(1);
		if(slot_output.getHasStack()){
			slot_output.putStack(new ItemStack(output.getItem(),slot_output.getStack().stackSize+1,slot_output.getStack().getItemDamage()));
		}else{
			slot_output.putStack(new ItemStack(output.getItem(),1,slot_output.getStack().getItemDamage()));
		}
		//reset running meter
		ticks_running=0;
	}
	
	///--END OF PROCESSING--\\\
	///--TEXTURE CHANGE--\\\
	public void setActiveTexture(boolean active){
		int facing = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		//System.out.println("Actual metadata is " + worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
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
		return true;
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
