package roito.afterthedrizzle.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import roito.afterthedrizzle.AfterTheDrizzle;
import roito.afterthedrizzle.common.inventory.ContainerStoneMill;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiContainerStoneMill extends GuiContainer
{
    private static final String TEXTURE_PATH = "textures/gui/container/gui_stone_mill.png";
    private static final ResourceLocation TEXTURE = new ResourceLocation(AfterTheDrizzle.MODID, TEXTURE_PATH);
    private ContainerStoneMill inventory;

    public GuiContainerStoneMill(ContainerStoneMill inventorySlotsIn)
    {
        super(inventorySlotsIn);
        xSize = 176;
        ySize = 166;
        inventory = inventorySlotsIn;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick)
    {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTick);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        int offsetX = (width - xSize) / 2, offsetY = (height - ySize) / 2;

        GlStateManager.color(1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(offsetX, offsetY, 0, 0, xSize, ySize);

        int totalTicks = this.inventory.getTileEntity().getTotalTicks();
        int processTicks = this.inventory.getTileEntity().getProcessTicks();
        int textureWidth = 0;
        if (totalTicks != 0)
        {
            textureWidth = (int) Math.ceil(22 * processTicks / totalTicks);
        }
        this.drawTexturedModalRect(offsetX + 95, offsetY + 37, 176, 48, textureWidth, 16);

        int height = 48 - (int) Math.ceil(48 * this.inventory.getTileEntity().getFluidAmount() / 2000);
        if (height != 48)
        {
            this.drawTank(offsetX + 37, offsetY + 22, this.inventory.getTileEntity().getFluidName());
        }
        mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(offsetX + 37, offsetY + 22, 185, 0, 16, height);
        this.drawTexturedModalRect(offsetX + 37, offsetY + 28, 176, 0, 9, 37);
        this.inventory.detectAndSendChanges();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String title = I18n.format("tile.afterthedrizzle.stone_mill.name");
        fontRenderer.drawString(title, (xSize - fontRenderer.getStringWidth(title)) / 2, 6, 0x404040);
    }

    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        super.renderHoveredToolTip(mouseX, mouseY);
        int offsetX = (width - xSize) / 2, offsetY = (height - ySize) / 2;
        if (offsetX + 37 < mouseX && mouseX < offsetX + 53 && offsetY + 22 < mouseY && mouseY < offsetY + 70)
        {
            if (this.inventory.getTileEntity().getFluidAmount() != 0)
            {
                List<String> list = new ArrayList();
                list.add(this.inventory.getTileEntity().getFluidTranslation());
                list.add(this.inventory.getTileEntity().getFluidAmount() + "mB");
                this.drawHoveringText(list, mouseX, mouseY);
            }
        }
    }

    protected void drawTank(int x, int y, String fluidID)
    {
        if (fluidID == null)
        {
            return;
        }

        Fluid fluid = FluidRegistry.getFluid(fluidID);
        int color = fluid.getColor();
        ResourceLocation fluidTexture = fluid.getStill();
        TextureAtlasSprite sprite = this.mc.getTextureMapBlocks().getAtlasSprite(fluidTexture.toString());
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.color((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, 1.0f);
        this.drawTexturedModalRect(x, y, sprite, 16, 16);
        this.drawTexturedModalRect(x, y + 16, sprite, 16, 16);
        this.drawTexturedModalRect(x, y + 32, sprite, 16, 16);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
