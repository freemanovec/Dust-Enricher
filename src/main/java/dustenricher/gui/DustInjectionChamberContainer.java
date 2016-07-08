package dustenricher.gui;

import dustenricher.tileentities.DustInjectionChamberTE;
import mekanism.common.inventory.slot.SlotEnergy;
import mekanism.common.inventory.slot.SlotOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class DustInjectionChamberContainer extends Container{

	protected DustInjectionChamberTE tileEntity;
	
	public DustInjectionChamberContainer(InventoryPlayer inventoryPlayer, DustInjectionChamberTE te){
		tileEntity = te;
		bindMachineInventory(te);
		bindPlayerInventory(inventoryPlayer);
	}
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}
	protected void bindMachineInventory(DustInjectionChamberTE te){
		addSlotToContainer(new Slot(te,2,22,50));
		addSlotToContainer(new Slot(te,1,22,23));
		addSlotToContainer(new SlotOutput(te,3,92,36));
		addSlotToContainer(new SlotEnergy.SlotDischarge(te, 4, 144, 14));
	}
	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer){
		for(int i=0;i<3;i++){
			for(int j=0;j<9;j++){
				addSlotToContainer(new Slot(inventoryPlayer,j+i*9+9,8+j*18,84+i*18));
			}
		}
		for(int i=0;i<9;i++){
			addSlotToContainer(new Slot(inventoryPlayer,i,8+i*18,142));
		}
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot){
		ItemStack stack = null;
		Slot slotObject = (Slot)inventorySlots.get(slot);
		System.out.println("InventorySize = " + tileEntity.getSizeInventory());
		
		//null checks and checks if the item can be stacked (maxStackSize > 1)
		if(slotObject!=null&&slotObject.getHasStack()){  //start
			//System.out.println(0);
			ItemStack stackInSlot = slotObject.getStack();
			//System.out.println(1);
			stack = stackInSlot.copy();
			//System.out.println(2);
			
			//merges the item into player inventory since its in the tileEntity
			if(slot<tileEntity.getSizeInventory()){
				//System.out.println(3);
				//if(!this.mergeItemStack(stackInSlot, tileEntity.getSizeInventory(), 36+tileEntity.getSizeInventory(), true)){
				if(!this.mergeItemStack(stackInSlot, tileEntity.getSizeInventory(), 28+tileEntity.getSizeInventory(), true)){
					//System.out.println(4);
					return null;
				}
				//System.out.println(5);
				//places it into the tileEntity is possible since its in the player inventory
			}else if(!this.mergeItemStack(stackInSlot, 0, tileEntity.getSizeInventory(), false)){
				//System.out.println(6);
				return null;
			}//else{
				//System.out.println(7);
			//}
			//System.out.println(8);
			if(stackInSlot.stackSize==0){
				//System.out.println(9);
				slotObject.putStack(null);
				//System.out.println(10);
			}else{
				//System.out.println(11);
				slotObject.onSlotChanged();
				//System.out.println(12);
			}
			//System.out.println(13);
			if(stackInSlot.stackSize==stack.stackSize){
				//System.out.println(14);
				return null;
			}
			//System.out.println(15);
			slotObject.onPickupFromSlot(player, stackInSlot);
			//System.out.println(16);
		} //end
		//System.out.println(17);
		return stack;
	}

}
