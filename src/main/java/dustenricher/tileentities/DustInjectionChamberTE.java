package dustenricher.tileentities;

import dustenricher.common.Recipe;
import dustenricher.common.Recipes;
import mekanism.api.IConfigurable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class DustInjectionChamberTE extends TileEntity implements IInventory, IConfigurable{

	public void setFacing(int val){
		facing = val;
	}
	int facing = 1;
	
	private ItemStack[] inv;
	
	boolean metastateActive = false;
	
	private double energy_max = 1500000d;
	public double energyPerTick = 2500;
	private double operatingTicks = 0;
	private double ticksRequired = 10;
	
	public Slot slot_infuse;
	public Slot slot_input;
	public Slot slot_output;
	public Slot slot_energy;
	public Slot slot_upgrades;
	
	
	public double getScaledProgress()
	{
		return (operatingTicks/ticksRequired);
	}
	public double getMaxEnergy(){
		return energy_max;
	}
	public double getEnergy(){
		return energy_internal;
	}
	public void setEnergy(double val){
		if(val>getMaxEnergy())
			val = getMaxEnergy();
		energy_internal = val;
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
	private double energy_internal = 0;
	
	@Override
	public void updateEntity(){
		if(!removeEnergy(10000))
			setEnergy(getMaxEnergy());
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
								System.out.println("-1");
								setMetastate(false);
							}else{
								//output item is already in output slot
								//we can increase the itemstack size
								processTick(foundRecipe.getOutput());
							}
						}else{
							//item in output slot is not the same as our desired output item
							//DONT do anything
							System.out.println("0");
							setMetastate(false);
						}
					}else{
						//output slot is empty
						processTick(foundRecipe.getOutput());
					}
				}else{
					System.out.println("1");
					setMetastate(false);
				}
			}else{
				System.out.println("2");
				setMetastate(false);
			}
		}else{
			System.out.println("3");
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
		if(active){
			System.out.println("Setting to true");
			if(facing<5){
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, facing+4, 2);
				metastateActive = true;
			}
		}else{
			System.out.println("Setting to false");
			//System.out.println(facing);
			if(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)>4){
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord)-4, 2);
				metastateActive = false;
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		metastateActive = nbt.getBoolean("metastateActive");
		setTo = nbt.getBoolean("setTo");
		energy_internal = nbt.getDouble("energy_internal");
	}
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("metastateActive", metastateActive);
		nbt.setBoolean("setTo", setTo);
		nbt.setDouble("energy_internal", energy_internal);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub
		
	}


}
