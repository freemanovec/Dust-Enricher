package dustenricher.gui;

import org.lwjgl.opengl.GL11;

import dustenricher.tileentities.DustInjectionChamberTE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class DustInjectionChamberGUI extends GuiContainer{
	public DustInjectionChamberGUI(InventoryPlayer inventoryPlayer, DustInjectionChamberTE tileEntity){
		//the container is instanciated and passed to the superclass for handling
		super(new DustInjectionChamberContainer(inventoryPlayer, tileEntity));
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1,int par2){
		//draw text and stuff here
		//the parameters for drawString are: string, x, y, color
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		fontRenderer.drawString("Dust Injection Chamber",8,6,4210752);
		fontRenderer.drawString("Test GUI", 8, 17, 4210752);
		fontRenderer.drawString("You have to finish this!", 28, 60, 045000255);
		//draws "Inventory" or your regional equivalent
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize-96 + 2, 4210752);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3){
		//draw your Gui here, only thing you need to change is the path
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.renderEngine.bindTexture(new ResourceLocation("dustenricher:textures/gui/GuiBlank.png"));
		int x = (width - xSize)/2;
		int y = (height - ySize)/2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
