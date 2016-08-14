package dustenricher.tileentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustenricher.common.OreDict;
import dustenricher.common.Recipes;
import mekanism.api.IConfigurable;
import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.SideData;
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
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.util.ForgeDirection;

public class DustInjectionChamberTE extends TileEntity implements IInventory, IStrictEnergyAcceptor, IConfigurable{
	
	private ItemStack[] inventory = new ItemStack[4];
	public int facing(){
		int process = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		if(process>4)
			process-=4;
		return process;
	}
	
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
		//nbt.setInteger("output_side_iteration", outputSideIteration);
		//nbt.setString("output_side", outputSide);
		NBTTagList list = new NBTTagList();
	    for (int i = 0; i < this.getSizeInventory(); ++i) {
	        if (this.getStackInSlot(i) != null) {
	            NBTTagCompound stackTag = new NBTTagCompound();
	            stackTag.setByte("Slot", (byte) i);
	            ItemStack inSlot = this.getStackInSlot(i);
	            inSlot.writeToNBT(stackTag);
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
		//outputSideIteration = nbt.getInteger("output_side_iteration");
		//outputSide = nbt.getString("output_side");
		if(worldObj==null||!worldObj.isRemote){
			NBTTagList list = nbt.getTagList("Items", 10);
			System.out.println("Got NBTTagList " + list);
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
	
	///--START OF OUTPUT STUFF--\\\

	public void debugPosition(){
		System.out.println("TE Rotation -> " + facing());
		System.out.println("TE Position -> " + getNiceCoords(xCoord, yCoord, zCoord));
		String posLeft = "NONE BITCH";
		String posRight = "NONE BITCH";
		int[] input = {0,0,0};
		int[] output = {0,0,0};
		switch(facing()){
		case 0:
			//BAD OFFSET
			//INVERT OP
			posLeft = getNiceCoords(xCoord+1,yCoord,zCoord);
			posRight = getNiceCoords(xCoord-1,yCoord,zCoord);
			input = new int[]{xCoord+1,yCoord,zCoord};
			output = new int[]{xCoord-1,yCoord,zCoord};
			break;
		case 1:
			//FINE
			posLeft = getNiceCoords(xCoord,yCoord,zCoord-1);
			posRight = getNiceCoords(xCoord,yCoord,zCoord+1);
			input = new int[]{xCoord,yCoord,zCoord-1};
			output = new int[]{xCoord,yCoord,zCoord+1};
			break;
		case 2:
			//BAD OFFSET
			//INVERT OP
			posLeft = getNiceCoords(xCoord-1,yCoord,zCoord);
			posRight = getNiceCoords(xCoord+1,yCoord,zCoord);
			input = new int[]{xCoord-1,yCoord,zCoord};
			output = new int[]{xCoord+1,yCoord,zCoord};
			break;
		case 3:
			//FINE
			posLeft = getNiceCoords(xCoord,yCoord,zCoord+1);
			posRight = getNiceCoords(xCoord,yCoord,zCoord-1);
			input = new int[]{xCoord,yCoord,zCoord+1};
			output = new int[]{xCoord,yCoord,zCoord-1};
			break;
		}
		
		System.out.println("LC Position -> " + posLeft);
		System.out.println("RC Position -> " + posRight);
		
		TileEntity te_input = worldObj.getTileEntity(input[0],input[1],input[2]);
		TileEntity te_output = worldObj.getTileEntity(output[0], output[1], output[2]);
		System.out.println("Left  TE -> " + te_input);
		System.out.println("Right TE -> " + te_output);
		
	}
	public String getNiceCoords(int x, int y, int z){
		return "(" + x + ":" + y + ":" + z + ")";
	}
	public void listContentOfChest(TileEntityChest chest){
		if(chest==null){
			System.out.println("No chest to list!");
			return;
		}
		for(int i = 0; i <chest.getSizeInventory(); i++){
			System.out.println("#" + i + ": " + chest.getStackInSlot(i));
		}
	}
	public boolean tryInputFromChest(TileEntityChest chest, ItemStack in_slot){
		//System.out.println("Hook #-1");
		if(chest==null)
			return false;
		if(in_slot != null && in_slot.stackSize >= 64)
			return false;
		boolean keep = true;
		/*for(int iteration = 0; iteration < chest.getSizeInventory(); iteration++){
			if(keep){
				if(in_slot != null || compareItemStacks(in_slot, chest.getStackInSlot(iteration))){
					System.out.println("Prev - " + chest.getStackInSlot(iteration));
					chest.decrStackSize(iteration, 1);
					System.out.println("Now  - " + chest.getStackInSlot(iteration));
					
					ItemStack toPut = slot_input.getStack();
					if(toPut==null)
						System.out.println("in_slot is null AND slot_input.getStack() is null? WTF?!");
					toPut.stackSize += 1;
					slot_input.putStack(toPut);
					keep=false;
				}else if(Recipes.isValidInput(chest.getStackInSlot(iteration))){
					ItemStack toPut = chest.getStackInSlot(iteration);
					toPut.stackSize = 1;
					System.out.println("Prev - " + chest.getStackInSlot(iteration));
					chest.decrStackSize(iteration, 1);
					System.out.println("Now  - " + chest.getStackInSlot(iteration));
					slot_input.putStack(toPut);
					keep=false;
				}
			}
		}*/
		return true;
		/*int we_can_get_at_max;
		if(in_slot==null){
			we_can_get_at_max = 64;
		}else{
			we_can_get_at_max = 64 - in_slot.stackSize;
		}
		if(in_slot != null && in_slot.stackSize>=64)
			return null;

		int we_gathered_so_far = 0; //this is how much we know well get from that chest
									//we'll increment our input slot with this value
		ItemStack content_of_input_slot = in_slot; //this is the ItemStack we have in out input slot in our DIC
		ItemStack sample = null;
		
		System.out.println("Hook #0");
	
		for(int index = 0; index < chest.getSizeInventory(); index ++){ //here we iterate through every slot of the chest
			System.out.println("Hook #1");
			ItemStack content_of_iterated_slot_in_chest = chest.getStackInSlot(index); //this is the ItemStack we have on index in our chest
			if(compareItemStacks(content_of_input_slot, content_of_iterated_slot_in_chest)||(content_of_input_slot==null&&Recipes.isValidInput(content_of_iterated_slot_in_chest))){ //we will proceed only if the item in the chest's slot is the same as in our DIC's input
				System.out.println("Hook #2");
				sample = content_of_iterated_slot_in_chest;
				int how_much_we_will_take_from_this_slot = 0; //that is zero so far
				int how_much_we_can_take_from_this_slot = we_can_get_at_max - we_gathered_so_far; //how much we can take without overflowing our input slot
				int size_of_iterated_itemstack = content_of_iterated_slot_in_chest.stackSize; //how much this slot can give us
				if(size_of_iterated_itemstack < how_much_we_can_take_from_this_slot){ //e.g. in chest will be 32 iron, but we can get 64 of it
					System.out.println("Hook #3");
					how_much_we_will_take_from_this_slot = size_of_iterated_itemstack; //we can take the whole slot
				}else{
					System.out.println("Hook #4");
					how_much_we_will_take_from_this_slot = how_much_we_can_take_from_this_slot; //we can take just a part of this itemstack
				}
				System.out.println("Hook #5");
				we_gathered_so_far += how_much_we_will_take_from_this_slot; //we increment it so we know how much to add to our DIC's input
				chest.decrStackSize(index, how_much_we_will_take_from_this_slot); //we decrease the slot in chest
			}
		}
		System.out.println("Hook #6");
		//here we will return the ItemStack
		if(in_slot==null && sample != null){
			ItemStack toReturn = new ItemStack(sample.getItem(),we_gathered_so_far,sample.getItemDamage());
			System.out.println("Hook #7");
			return toReturn;
		}else{
			System.out.println("Hook #8");
			if(in_slot==null){
				System.out.println("Hook #9");
				return null;
			}
			if(in_slot.getItem()==null)
				System.out.println("Hook #10");
			ItemStack toReturn = new ItemStack(in_slot.getItem(),in_slot.stackSize+we_gathered_so_far,in_slot.getItemDamage());
			return toReturn;
		}*/
	}
	public boolean tryOutputToChest(TileEntityChest chest, ItemStack in_slot){
		if(chest==null)
			return false;
		return false;
	}
	public boolean compareItemStacks(ItemStack itemstackOne, ItemStack itemstackTwo){
		if(itemstackOne==null||itemstackTwo==null)
			return false;
		if(itemstackOne.getItem()==itemstackTwo.getItem()&&itemstackOne.getItemDamage()==itemstackTwo.getItemDamage())
			return true;
		return false;
	}

	///-END OF OUTPUT STUFF--\\\
	///--PROCESSING--\\\
	
	public int ticks_running = 0;
	public int ticks_required = 200;
	public Slot slot_input, slot_infuse, slot_output, slot_energy;
	
	@Override
	public void updateEntity(){
		markForUpdate();
		if(!worldObj.isRemote){
			//debugPosition();
			//System.out.println(this.outputSide);
			/*if(outputSide!="None"){
				//System.out.println("Facing integer: " + facing());
			}*/
			
			int[] input_chest_pos = {0,0,0};
			int[] output_chest_pos = {0,0,0};
			switch(facing()){
			case 0:
				System.out.println("Case 0");
				input_chest_pos = new int[]{xCoord+1,yCoord,zCoord};
				output_chest_pos = new int[]{xCoord-1,yCoord,zCoord};
				break;
			case 1:
				System.out.println("Case 1");
				input_chest_pos = new int[]{xCoord,yCoord,zCoord-1};
				output_chest_pos = new int[]{xCoord,yCoord,zCoord+1};
				break;
			case 2:
				System.out.println("Case 2");
				input_chest_pos = new int[]{xCoord-1,yCoord,zCoord};
				output_chest_pos = new int[]{xCoord+1,yCoord,zCoord};
				break;
			case 3:
				System.out.println("Case 3");
				input_chest_pos = new int[]{xCoord,yCoord,zCoord+1};
				output_chest_pos = new int[]{xCoord,yCoord,zCoord-1};
				break;
			}
			TileEntityChest input_chest_te = (TileEntityChest) worldObj.getTileEntity(input_chest_pos[0],input_chest_pos[1],input_chest_pos[2]);
			TileEntityChest output_chest_te = (TileEntityChest) worldObj.getTileEntity(output_chest_pos[0], output_chest_pos[1], output_chest_pos[2]);
			if(slot_input != null){
				//tryInputFromChest(input_chest_te, slot_input.getStack());
				listContentOfChest(input_chest_te);
				//listContentOfChest(output_chest_te);
				/*if(slot_input.getHasStack())
					tryInputFromChest(input_chest_te, slot_input.getStack());
				else
					tryInputFromChest(input_chest_te, null);*/
				/*if(slot_input.getHasStack())
					slot_input.putStack(tryInputFromChest(input_chest_te, slot_input.getStack()));
				else
					slot_input.putStack(tryInputFromChest(input_chest_te, null));
				System.exit(1);*/
			}
			
			
			if(slot_input==null||slot_infuse==null||slot_output==null||slot_energy==null){
				setActiveTexture(false);
				return;
			}
			if(slot_input.getStack()==null||slot_infuse.getStack()==null){
				setActiveTexture(false);
				ticks_running=0;
				return;
			}
			//start of processing
			/*System.out.println("Input:  " + slot_input.getStack());
			System.out.println("Infuse: " + slot_infuse.getStack());*/
			ItemStack output = Recipes.getOutputFrom(slot_input.getStack(), slot_infuse.getStack());
			if(output==null){
				//System.out.println("Output is null");
				setActiveTexture(false);
				return;
			}
			//we can now start
			if(ticks_running>=ticks_required){
				processingFinished(output);
			}else{
				//processing not finished, increment our progress meter
				if(removeEnergy(energy_perItem/ticks_required)){
					setActiveTexture(true);
					ticks_running++;
				}
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
			if(slot_output.getStack().getItem()==output.getItem()&&slot_output.getStack().getItemDamage()==output.getItemDamage()){
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
			System.out.println("Output slot contains desired item, increasing");
			output.stackSize=slot_output.getStack().stackSize+1;
			//slot_output.putStack(new ItemStack(output.getItem(),slot_output.getStack().stackSize+1,slot_output.getStack().getItemDamage()));
			slot_output.putStack(output);
		}else{
			System.out.println("Output slot does not contain anything, creating");
			output.stackSize=1;
			//slot_output.putStack(new ItemStack(output.getItem(),1,slot_output.getStack().getItemDamage()));
			slot_output.putStack(output);
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
 			//System.out.println("Setting to true");
  			if(facing<5){
  				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, facing+4, 1);
  				//System.out.println("New metadata is " + worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
 				//System.out.println("Set active!");
  			}
  		}else{
 			//System.out.println("Setting to false");
  			if(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)>3){
  				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord)-4, 1);
 				//System.out.println("Set inactive!");
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
		//setActiveTexture(false);
		debugPosition();
		return true;
	}

	@Override
	public boolean onRightClick(EntityPlayer player, int side) {
		//setActiveTexture(true);
		//debugPosition();
		return true;
	}
	///--END OF DEBUG STUFF--\\\



}
