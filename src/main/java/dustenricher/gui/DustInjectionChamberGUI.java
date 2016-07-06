package dustenricher.gui;

import org.lwjgl.opengl.GL11;

import dustenricher.tileentities.DustInjectionChamberTE;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiPowerBar;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class DustInjectionChamberGUI extends GuiMekanism{
	
	public DustInjectionChamberTE tileEntity;
	
	public DustInjectionChamberGUI(InventoryPlayer inventoryPlayer, DustInjectionChamberTE te){
		//the container is instanciated and passed to the superclass for handling		
		super(new DustInjectionChamberContainer(inventoryPlayer, te));
		tileEntity = te;
		//guiElements.add(new GuiPowerBar(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GuiMetallurgicInfuser.png"),164,15));
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1,int par2){
		//draw text and stuff here
		//the parameters for drawString are: string, x, y, color
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		fontRenderer.drawString("Dust Injection Chamber",30,6,4210752);
		fontRenderer.drawString("Test GUI", 16, 17, 4210752);
		//fontRenderer.drawString("You have to finish this!", 28, 60, 045000255);
		//draws "Inventory" or your regional equivalent
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize-96 + 2, 4210752);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3){
		//draw your Gui here, only thing you need to change is the path
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.renderEngine.bindTexture(new ResourceLocation("dustenricher:textures/gui/GuiAttempt.png"));
		int x = (width - xSize)/2;
		int y = (height - ySize)/2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
