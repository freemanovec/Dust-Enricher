package dustenricher.tileentities;

import java.util.ArrayList;
import java.util.EnumSet;

import dustenricher.origin.ISecurityTile;
import dustenricher.origin.ISideConfiguration;
import dustenricher.origin.IUpgradeTile;
import dustenricher.origin.MekanismUtils;
import dustenricher.origin.TileComponentConfig;
import dustenricher.origin.TileComponentEjector;
import dustenricher.origin.TileComponentSecurity;
import dustenricher.origin.TileComponentUpgrade;
import dustenricher.origin.TileEntityElectricBlock;
import io.netty.buffer.ByteBuf;
import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.IConfigCardAccess;
import mekanism.api.MekanismConfig.general;
import mekanism.api.Range4D;
import mekanism.api.infuse.InfuseObject;
import mekanism.api.infuse.InfuseRegistry;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.InfuseStorage;
import mekanism.common.Mekanism;
import mekanism.common.MekanismBlocks;
import mekanism.common.MekanismItems;
import mekanism.common.PacketHandler;
import mekanism.common.SideData;
import mekanism.common.Upgrade;
import mekanism.common.base.IActiveState;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.base.IRedstoneControl.RedstoneControl;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.integration.IComputerIntegration;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.inputs.InfusionInput;
import mekanism.common.recipe.machines.MetallurgicInfuserRecipe;
import mekanism.common.tile.TileEntityFactory;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.InventoryUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.ForgeDirection;

public class DustInjectionChamberTE extends TileEntityElectricBlock implements IComputerIntegration, ISideConfiguration, IUpgradeTile, IRedstoneControl, IConfigCardAccess, ISecurityTile, IActiveState{

	public int MAX_INFUSE = 1000;
	public double BASE_ENERGY_PER_TICK = 2500;
	public double energyPerTick = BASE_ENERGY_PER_TICK;
	public int BASE_TICKS_REQUIRED = 200;
	public int ticksRequired = BASE_TICKS_REQUIRED;
	public InfuseStorage infuseStored = new InfuseStorage();
	public int operatingTicks;
	public boolean isActive;
	public boolean clientActive;
	public int updateDelay;
	public double prevEnergy;
	public RedstoneControl controlType = RedstoneControl.DISABLED;
	
	public TileComponentUpgrade upgradeComponent;
	public TileComponentEjector ejectorComponent;
	public TileComponentConfig configComponent;
	public TileComponentSecurity securityComponent;
	
	//TODO add those variables to config
	private static double maxEnergy = 1500000;
	//TODO add config
	
	public DustInjectionChamberTE(){
		super("DustInjectionChamber", maxEnergy);
		
		configComponent = new TileComponentConfig(this, TransmissionType.ITEM);
		
		configComponent.addOutput(TransmissionType.ITEM, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
		configComponent.addOutput(TransmissionType.ITEM, new SideData("Input", EnumColor.DARK_RED, new int[] {2}));
		configComponent.addOutput(TransmissionType.ITEM, new SideData("Output", EnumColor.DARK_BLUE, new int[] {3}));
		configComponent.addOutput(TransmissionType.ITEM, new SideData("Energy", EnumColor.DARK_GREEN, new int[] {4}));
		configComponent.addOutput(TransmissionType.ITEM, new SideData("Infuse", EnumColor.PURPLE, new int[] {1}));
		
		configComponent.setConfig(TransmissionType.ITEM, new byte[] {4, 0, 0, 3, 1, 2});
		
		inventory = new ItemStack[5];
		
		upgradeComponent = new TileComponentUpgrade(this, 0);
		upgradeComponent.setSupported(Upgrade.MUFFLING);
		
		ejectorComponent = new TileComponentEjector(this);
		ejectorComponent.setOutputData(TransmissionType.ITEM, configComponent.getOutputs(TransmissionType.ITEM).get(2));
		
		securityComponent = new TileComponentSecurity(this);
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		
		if(worldObj!=null)
		if(worldObj.isRemote && updateDelay > 0){
			updateDelay--;
			
			if(updateDelay == 0 && clientActive != isActive){
				isActive = clientActive;
				MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
			}
		}
		
		if(worldObj!=null)
		if(!worldObj.isRemote){
			if(updateDelay > 0){
				updateDelay--;
				
				if(updateDelay == 0 && clientActive != isActive){
					Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
				}
			}
			
			ChargeUtils.discharge(4, this);
			
			if(inventory[1] != null){
				if(InfuseRegistry.getObject(inventory[1]) != null){
					InfuseObject infuse = InfuseRegistry.getObject(inventory[1]);
					
					if(infuseStored.type == null || infuseStored.type == infuse.type){
						if(infuseStored.amount + infuse.stored <= MAX_INFUSE){
							infuseStored.amount += infuse.stored;
							infuseStored.type = infuse.type;
							inventory[1].stackSize--;
							
							if(inventory[1].stackSize <= 0){
								inventory[1] = null;
							}
						}
					}
				}
			}
			
			MetallurgicInfuserRecipe recipe = RecipeHandler.getMetallurgicInfuserRecipe(getInput());
			
			if(canOperate(recipe) && MekanismUtils.canFunction(this) && getEnergy() >= energyPerTick){
				setActive(true);
				setEnergy(getEnergy() - energyPerTick);
				
				if((operatingTicks + 1) < ticksRequired){
					operatingTicks++;
				}else{
					operate(recipe);
					operatingTicks = 0;
				}
			}else{
				if(prevEnergy >= getEnergy()){
					setActive(false);
				}
			}
			
			if(!canOperate(recipe)){
				operatingTicks = 0;
			}
			
			if(infuseStored.amount <= 0){
				infuseStored.amount = 0;
				infuseStored.type = null;
			}
			
			prevEnergy = getEnergy();
		}
	}
	
	public void upgrade(RecipeType type){
		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		worldObj.setBlock(xCoord, yCoord, zCoord, MekanismBlocks.MachineBlock, 5, 3);
		
		TileEntityFactory factory = (TileEntityFactory)worldObj.getTileEntity(xCoord, yCoord, zCoord);
		
		//Basic
				factory.facing = facing;
				factory.clientFacing = clientFacing;
				factory.ticker = ticker;
				factory.redstone = redstone;
				factory.redstoneLastTick = redstoneLastTick;
				factory.doAutoSync = doAutoSync;
				
				//Electric
				factory.electricityStored = electricityStored;
				
				//Noisy
				//factory.soundURL = soundURL;
				
				//Machine
				factory.progress[0] = operatingTicks;
				factory.clientActive = clientActive;
				factory.isActive = isActive;
				factory.updateDelay = updateDelay;
				factory.controlType = controlType;
				factory.prevEnergy = prevEnergy;
				//factory.upgradeComponent.readFrom(upgradeComponent);
				factory.upgradeComponent.setUpgradeSlot(0);
				//factory.ejectorComponent.readFrom(ejectorComponent);
				factory.ejectorComponent.setOutputData(TransmissionType.ITEM, factory.configComponent.getOutputs(TransmissionType.ITEM).get(2));
				factory.recipeType = type;
				factory.upgradeComponent.setSupported(Upgrade.GAS, type.fuelEnergyUpgrades());
				//factory.securityComponent.readFrom(securityComponent);
				
				for(TransmissionType transmission : configComponent.transmissions)
				{
					factory.configComponent.setConfig(transmission, configComponent.getConfig(transmission));
					factory.configComponent.setEjecting(transmission, configComponent.isEjecting(transmission));
				}
				
				//Infuser
				factory.infuseStored.amount = infuseStored.amount;
				factory.infuseStored.type = infuseStored.type;

				factory.inventory[5] = inventory[2];
				factory.inventory[1] = inventory[4];
				factory.inventory[5+3] = inventory[3];
				factory.inventory[0] = inventory[0];
				factory.inventory[4] = inventory[1];
				
				for(Upgrade upgrade : factory.upgradeComponent.getSupportedTypes())
				{
					factory.recalculateUpgradables(upgrade);
				}
				
				factory.upgraded = true;
				
				factory.markDirty();
	}
	
	@Override
	public boolean canExtractItem(int slotID, ItemStack itemstack, int side){
		if(slotID == 4)
		{
			return ChargeUtils.canBeOutputted(itemstack, false);
		}
		else if(slotID == 3)
		{
			return true;
		}

		return false;
	}
	
	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemstack){
		switch(slotID){
		case 3:
			return false;
		case 1:
			return InfuseRegistry.getObject(itemstack) != null && (infuseStored.type == null || infuseStored.type == InfuseRegistry.getObject(itemstack).type);
		case 0:
			return itemstack.getItem() == MekanismItems.SpeedUpgrade || itemstack.getItem() == MekanismItems.EnergyUpgrade;
		case 2:
			if(infuseStored.type != null){
				if(RecipeHandler.getMetallurgicInfuserRecipe(new InfusionInput(infuseStored, itemstack)) != null){
					return true;
				}
			}else{
				for(Object obj : Recipe.METALLURGIC_INFUSER.get().keySet()){
					InfusionInput input = (InfusionInput)obj;
					
					if(input.inputStack.isItemEqual(itemstack)){
						return true;
					}
				}
			}
		case 4:
			return ChargeUtils.canBeDischarged(itemstack);
		}
		return false;
	}
	public InfusionInput getInput(){
		return new InfusionInput(infuseStored,inventory[2]);
	}
	public void operate(MetallurgicInfuserRecipe recipe){
		recipe.output(inventory, 2, 3, infuseStored);
		
		markDirty();
		ejectorComponent.outputItems();
	}
	public boolean canOperate(MetallurgicInfuserRecipe recipe){
		return recipe != null && recipe.canOperate(inventory, 2, 3, infuseStored);
	}
	public int getScaledInfuseLevel(int i){
		return infuseStored.amount * i / MAX_INFUSE;
	}
	public double getScaledProgress(){
		return ((double)operatingTicks) / ((double)ticksRequired);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTags)
	{
		super.readFromNBT(nbtTags);

		clientActive = isActive = nbtTags.getBoolean("isActive");
		operatingTicks = nbtTags.getInteger("operatingTicks");
		infuseStored.amount = nbtTags.getInteger("infuseStored");
		controlType = RedstoneControl.values()[nbtTags.getInteger("controlType")];
		infuseStored.type = InfuseRegistry.get(nbtTags.getString("type"));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbtTags)
	{
		super.writeToNBT(nbtTags);

		nbtTags.setBoolean("isActive", isActive);
		nbtTags.setInteger("operatingTicks", operatingTicks);
		nbtTags.setInteger("infuseStored", infuseStored.amount);
		nbtTags.setInteger("controlType", controlType.ordinal());

		if(infuseStored.type != null)
		{
			nbtTags.setString("type", infuseStored.type.name);
		}
		else {
			nbtTags.setString("type", "null");
		}

		nbtTags.setBoolean("sideDataStored", true);
	}
	
	@Override
	public void handlePacketData(ByteBuf dataStream)
	{
		if(!worldObj.isRemote)
		{
			infuseStored.amount = dataStream.readInt();
			return;
		}

		super.handlePacketData(dataStream);

		if(worldObj.isRemote)
		{
			clientActive = dataStream.readBoolean();
			operatingTicks = dataStream.readInt();
			infuseStored.amount = dataStream.readInt();
			controlType = RedstoneControl.values()[dataStream.readInt()];
			infuseStored.type = InfuseRegistry.get(PacketHandler.readString(dataStream));
	
			if(updateDelay == 0 && clientActive != isActive)
			{
				updateDelay = general.UPDATE_DELAY;
				isActive = clientActive;
				MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
			}
		}
	}
	
	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);

		data.add(isActive);
		data.add(operatingTicks);
		data.add(infuseStored.amount);
		data.add(controlType.ordinal());

		if(infuseStored.type != null)
		{
			data.add(infuseStored.type.name);
		}
		else {
			data.add("null");
		}

		return data;
	}

    private static final String[] methods = new String[] {"getEnergy", "getProgress", "facing", "canOperate", "getMaxEnergy", "getEnergyNeeded", "getInfuse", "getInfuseNeeded"};

	@Override
	public String[] getMethods()
	{
		return methods;
	}

	@Override
	public Object[] invoke(int method, Object[] arguments) throws Exception
	{
		switch(method)
		{
			case 0:
				return new Object[] {getEnergy()};
			case 1:
				return new Object[] {operatingTicks};
			case 2:
				return new Object[] {facing};
			case 3:
				return new Object[] {canOperate(RecipeHandler.getMetallurgicInfuserRecipe(getInput()))};
			case 4:
				return new Object[] {getMaxEnergy()};
			case 5:
				return new Object[] {getMaxEnergy()-getEnergy()};
			case 6:
				return new Object[] {infuseStored};
			case 7:
				return new Object[] {MAX_INFUSE-infuseStored.amount};
			default:
				throw new NoSuchMethodException();
		}
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side){
		return configComponent.getOutput(TransmissionType.ITEM, side, facing).availableSlots;
	}
	
	@Override
	public boolean canSetFacing(int side){
		return side != 0 && side != 1;
	}
	
	@Override
	public void setActive(boolean active)
	{
		isActive = active;

		if(clientActive != active && updateDelay == 0)
		{
			Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));

			updateDelay = 10;
			clientActive = active;
		}
	}
	
	@Override
	public boolean getActive()
	{
		return isActive;
	}

	@Override
	public TileComponentConfig getConfig()
	{
		return configComponent;
	}

	@Override
	public int getOrientation()
	{
		return facing;
	}

	@Override
	public boolean renderUpdate()
	{
		return false;
	}

	@Override
	public boolean lightUpdate()
	{
		return true;
	}

	@Override
	public RedstoneControl getControlType()
	{
		return controlType;
	}

	@Override
	public void setControlType(RedstoneControl type)
	{
		controlType = type;
		MekanismUtils.saveChunk(this);
	}

	@Override
	public boolean canPulse()
	{
		return false;
	}
	
	@Override
	public TileComponentUpgrade getComponent()
	{
		return upgradeComponent;
	}

	@Override
	public TileComponentEjector getEjector()
	{
		return ejectorComponent;
	}
	
	@Override
	public TileComponentSecurity getSecurity()
	{
		return securityComponent;
	}

	@Override
	public void recalculateUpgradables(Upgrade upgrade)
	{
		super.recalculateUpgradables(upgrade);

		switch(upgrade)
		{
			case SPEED:
				ticksRequired = MekanismUtils.getTicks(this, BASE_TICKS_REQUIRED);
			case ENERGY:
				energyPerTick = MekanismUtils.getEnergyPerTick(this, BASE_ENERGY_PER_TICK);
				maxEnergy = MekanismUtils.getMaxEnergy(this, BASE_MAX_ENERGY);
			default:
				break;
		}
	}
	
	
	
	/*
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

*/
}
