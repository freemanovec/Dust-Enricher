package dustenricher.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import dustenricher.gui.elements.PowerBar;
import dustenricher.tileentities.DustInjectionChamberTE;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiPowerBar;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.GuiUpgradeTab;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class DustInjectionChamberGUI extends GuiMekanism{
	
	public DustInjectionChamberTE tileEntity;
	
	public DustInjectionChamberGUI(InventoryPlayer inventoryPlayer, DustInjectionChamberTE te){
		//the container is instanciated and passed to the superclass for handling		
		super(new DustInjectionChamberContainer(inventoryPlayer, te));
		tileEntity = te;
		guiElements.add(new GuiProgress(new IProgressInfoHandler()
		{
			@Override
			public double getProgress()
			{
				return tileEntity.getScaledProgress();
			}
		}, ProgressBar.MEDIUM, this, MekanismUtils.getResource(ResourceType.GUI, "GuiMetallurgicInfuser.png"), 50, 35));
		guiElements.add(new GuiProgress(new IProgressInfoHandler()
		{
			@Override
			public double getProgress()
			{
				return tileEntity.getScaledProgress();
			}
		}, ProgressBar.MEDIUM, this, MekanismUtils.getResource(ResourceType.GUI, "GuiMetallurgicInfuser.png"), 50, 43));
		guiElements.add(new PowerBar(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GuiMetallurgicInfuser.png"),164,15));
		/*guiElements.add(new GuiPowerBar(this,tileEntity,MekanismUtils.getResource(ResourceType.GUI, "GuiMetallurgicInfuser.png"),164,15));
		guiElements.add(new GuiSlot(SlotType.INPUT,this,MekanismUtils.getResource(ResourceType.GUI, "GuiMetallurgicInfuser.png"), 50, 42));
		guiElements.add(new GuiSlot(SlotType.OUTPUT, this, MekanismUtils.getResource(ResourceType.GUI, "GuiMetallurgicInfuser.png"), 108, 42));*/
		guiElements.add(new GuiSlot(SlotType.POWER, this, MekanismUtils.getResource(ResourceType.GUI, "GuiMetallurgicInfuser.png"), 143, 13).with(SlotOverlay.POWER));
		/*guiElements.add(new GuiSlot(SlotType.EXTRA, this, MekanismUtils.getResource(ResourceType.GUI, "GuiMetallurgicInfuser.png"), 16, 34));
		guiElements.add(new GuiUpgradeTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GuiMetallurgicInfuser.png")));
		guiElements.add(new GuiProgress(new IProgressInfoHandler()
		{
			@Override
			public double getProgress()
			{
				return tileEntity.getScaledProgress();
			}
		}, ProgressBar.MEDIUM, this, MekanismUtils.getResource(ResourceType.GUI, "GuiMetallurgicInfuser.png"), 70, 46));
		guiElements.add(new GuiEnergyInfo(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				String multiplier = MekanismUtils.getEnergyDisplay(tileEntity.energyPerTick);
				return ListUtils.asList(LangUtils.localize("gui.using") + ": " + multiplier + "/t", LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy()-tileEntity.getEnergy()));
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "GuiMetallurgicInfuser.png")));*/
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX,int mouseY){
		//the parameters for drawString are: string, x, y, color
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		fontRenderer.drawString("Dust Injection Chamber",30,6,4210752);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize-96 + 2, 4210752);
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY){
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.renderEngine.bindTexture(new ResourceLocation("dustenricher:textures/gui/GuiBlank.png"));
		int x = (width - xSize)/2;
		int y = (height - ySize)/2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		//super.drawGuiContainerBackgroundLayer(partialTick,mouseX,mouseY);
		
		int guiWidth = (super.width - super.xSize) / 2;
		int guiHeight = (super.height - super.ySize) / 2;
		
		int xAxis = mouseX - guiWidth;
		int yAxis = mouseY - guiHeight;
		
		for(GuiElement element : super.guiElements){
			element.renderBackground(xAxis, yAxis, guiWidth, guiHeight);
		}
	}
}
