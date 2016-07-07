package dustenricher.tileentities;

import java.util.EnumSet;

import dustenricher.common.IEnergyWrapper;
import dustenricher.common.Recipe;
import dustenricher.common.Recipes;
import dustenricher.origin.TileEntityElectricBlock;
import dustenricher.origin.TileEntityNoisyElectricBlock;
import mekanism.api.IConfigurable;
import mekanism.api.MekanismConfig.general;
import mekanism.common.base.IActiveState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.ForgeDirection;

public class DustInjectionChamberTE extends TileEntityElectricBlock implements IInventory, IConfigurable, IActiveState, IEnergyWrapper{

	
	
	
	public void setFacing(int val){
		facing = val;
	}
	int facing = 1;
	
	private ItemStack[] inv;
	
	boolean metastateActive = false;
	
	private double energy_internal = 0;
	private static double energy_max = 1500000d;
	public double energyPerTick = 2500;
	private double operatingTicks = 0;
	private double ticksRequired = 200;
	
	public Slot slot_infuse;
	public Slot slot_input;
	public Slot slot_output;
	public Slot slot_energy;
	public Slot slot_upgrades;
	
	public NBTTagCompound nbtTagCompound;
	
	
	public double getScaledProgress()
	{
		return (operatingTicks/ticksRequired);
	}
	public double getMaxEnergy(){
		return energy_max;
	}
	public final double getEnergy(){
		//System.out.println("Returning energy: " + energy_internal);
		return energy_internal;
	}
	public void setEnergy(double val){
		if(val>getMaxEnergy())
			val = getMaxEnergy();
		energy_internal = val;
		System.out.println("Energy level is " + val);
		System.out.println("Internal reported " + energy_internal);
		System.out.println("GetEnergy got " + getEnergy());
	}
	public void addEnergy(double val){
		if((val+getEnergy())>getMaxEnergy()){
			setEnergy(getMaxEnergy());
		}else{
			setEnergy(getEnergy()+val);
		}
	}
	public boolean removeEnergy(double val){
		if(getEnergy()-val<0){
			return false;
		}else{
			setEnergy(getEnergy()-val);
			return true;
		}
	}
	
	
	@Override
	public void updateEntity(){
		/*if(!removeEnergy(10000))
			setEnergy(getMaxEnergy());*/
		/*operatingTicks++;
		if(operatingTicks>ticksRequired)
			operatingTicks = 0;*/
		if(slot_input==null){
			return;
		}
		if(slot_input.getHasStack()){
			Item inSlot = slot_input.getStack().getItem();
			Recipe foundRecipe = null;
			for(Recipe recipe : Recipes.recipes_DustInjectionChamber){
				if(recipe.getInput()==inSlot){
					foundRecipe = recipe;
				}
			}
			if(foundRecipe!=null){
				if(slot_input.getStack().getItem()==foundRecipe.getInput()){
					//process
					if(slot_output.getHasStack()){
						if(slot_output.getStack().getItem() == foundRecipe.getOutput()){
							//output item is already in output slot
							if(slot_output.getStack().stackSize>=64){
								//output slot is full
								//DONT do anything
								setMetastate(false);
							}else{
								//output item is already in output slot
								//we can increase the itemstack size
								processTick(foundRecipe.getOutput());
							}
						}else{
							//item in output slot is not the same as our desired output item
							//DONT do anything
							setMetastate(false);
						}
					}else{
						//output slot is empty
						processTick(foundRecipe.getOutput());
					}
				}else{
					setMetastate(false);
				}
			}else{
				setMetastate(false);
			}
		}else{
			setMetastate(false);
		}
	}
	private void processTick(Item outputItem){
		//System.out.println("Processing tick!");
		setMetastate(true);
		if(operatingTicks==ticksRequired){
			//we're done
			if(slot_output.getHasStack()){
				//output slot has something in them
				ItemStack inSlot = slot_output.getStack();
				ItemStack toSlot = new ItemStack(inSlot.getItem(),inSlot.stackSize+1);
				slot_output.putStack(toSlot);
				slot_input.decrStackSize(1);
			}else{
				ItemStack toSlot = new ItemStack(outputItem,1);
				slot_output.putStack(toSlot);
				slot_input.decrStackSize(1);
			}
			
			operatingTicks=0;
		}else{
			operatingTicks++;
		}
	}
	public void setMetastate(boolean active){
		if(active!=metastateActive){
			//state changed
			if(active){
				worldObj.setLightValue(EnumSkyBlock.Block, xCoord, yCoord, zCoord, 14);
				worldObj.notifyBlockChange(xCoord, yCoord, zCoord, blockType);
				worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
				this.lightUpdate();
			}else{
				worldObj.setLightValue(EnumSkyBlock.Block, xCoord, yCoord, zCoord, 0);
				worldObj.notifyBlockChange(xCoord, yCoord, zCoord, blockType);
				worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
				this.lightUpdate();
			}
		}
		if(active){
			if(facing<5){
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, facing+4, 2);
				metastateActive = true;
			}
		}else{
			if(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)>4){
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord)-4, 2);
				metastateActive = false;
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		nbtTagCompound = nbt;
		metastateActive = nbt.getBoolean("metastateActive");
		energy_internal = nbt.getDouble("energy_internal");
		operatingTicks = nbt.getDouble("operatingTicks");
	}
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbtTagCompound = nbt;
		nbt.setBoolean("metastateActive", metastateActive);
		nbt.setDouble("energy_internal", energy_internal);
		nbt.setDouble("operatingTicks", operatingTicks);
	}
	
	public DustInjectionChamberTE(){
		super("DustInjectionChamber", energy_max);
		//TODO Fix everything
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
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return true;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}
	@Override
	public boolean getActive() {
		return metastateActive;
	}
	@Override
	public void setActive(boolean active) {
		setMetastate(active);
	}
	@Override
	public boolean renderUpdate() {
		return false;
	}
	@Override
	public boolean lightUpdate() {
		return true;
	}
	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if(getOutputtingSides().contains(from)){
			double toRemove = Math.min(getEnergy(), Math.min(getMaxOutput(), maxExtract*general.FROM_TE));
			if(!simulate){
				setEnergy(getEnergy()-toRemove);
			}
			return (int)Math.round(toRemove*general.TO_TE);
		}
		return 0;
	}
	@Override
	public int getEnergyStored(ForgeDirection arg0) {
		return (int)Math.round(getEnergy()*general.TO_TE);
	}
	@Override
	public int getMaxEnergyStored(ForgeDirection arg0) {
		return (int)Math.round(getMaxEnergy()*general.TO_TE);
	}
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if(getConsumingSides().contains(from))
		{
			double toAdd = (int)Math.min(getMaxEnergy()-getEnergy(), maxReceive*general.FROM_TE);

			if(!simulate)
			{
				setEnergy(getEnergy() + toAdd);
			}

			return (int)Math.round(toAdd*general.TO_TE);
		}

		return 0;
	}
	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return getConsumingSides().contains(from) || getOutputtingSides().contains(from);
	}
	@Override
	public double transferEnergyToAcceptor(ForgeDirection side, double amount)
	{
		//System.out.println("Getting energy");
		if(!(getConsumingSides().contains(side) || side == ForgeDirection.UNKNOWN))
		{
			System.out.println("Returning 0");
			return 0;
		}
		double toUse = Math.min(getMaxEnergy()-getEnergy(), amount);
		//setEnergy(getEnergy() + toUse);
		addEnergy(toUse);
		return toUse;
	}
	@Override
	public boolean canReceiveEnergy(ForgeDirection side)
	{
		return getConsumingSides().contains(side);
		//return false;
	}
	@Override
	public boolean canOutputTo(ForgeDirection side)
	{
		return getOutputtingSides().contains(side);
	}
	@Override
	public EnumSet<ForgeDirection> getOutputtingSides() {
		return EnumSet.noneOf(ForgeDirection.class);
	}
	@Override
	public EnumSet<ForgeDirection> getConsumingSides() {
		return EnumSet.allOf(ForgeDirection.class);
		//return EnumSet.noneOf(ForgeDirection.class);
	}
	@Override
	public double getMaxOutput() {
		return 0;
	}


}
