package roito.afterthedrizzle.client.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import roito.afterthedrizzle.AfterTheDrizzle;
import roito.afterthedrizzle.common.capability.CapabilityPlayerTemperature;
import roito.afterthedrizzle.common.config.NormalConfig;
import roito.afterthedrizzle.common.environment.ApparentTemperature;

import java.util.Timer;
import java.util.TimerTask;

import static roito.afterthedrizzle.AfterTheDrizzle.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class PlayerTemperatureRenderer extends AbstractGui
{

    private final static ResourceLocation OVERLAY_BAR = new ResourceLocation(MODID, "textures/gui/hud/temperature.png");

    private static final Timer timer = new Timer();
    private static int index = -1;
    private static int up = 0;

    private Minecraft mc;

    public PlayerTemperatureRenderer(Minecraft mc)
    {
        this.mc = mc;
    }

    public void renderStatusBar(int screenWidth, int screenHeight, CapabilityPlayerTemperature.Data t, double env, int cold, int heat)
    {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlphaTest();

//        this.drawString(mc.fontRenderer, String.valueOf(temp), screenWidth / 2 - mc.fontRenderer.getStringWidth(String.valueOf(temp)) / 2, 6, 4210752);
//        this.drawString(mc.fontRenderer, String.valueOf(env), screenWidth / 2 - mc.fontRenderer.getStringWidth(String.valueOf(env)) / 2, 12, 4210752);

        mc.getTextureManager().bindTexture(OVERLAY_BAR);
        if (up == 0)
        {
            up = t.getHotterOrColder();
            if (up != 0)
            {
                index = 0;
            }
            else
            {
                index = -1;
            }
        }
        if (up != 0 && index >= 0)
        {
            blit((NormalConfig.playerTemperatureX.get()) + 26, screenHeight - NormalConfig.playerTemperatureY.get() + 7, index / 8  * 7, 48 + (up - 1) * 16, 7, 16);
            index %= 120;
            if (index == 0)
            {
                up = t.getHotterOrColder();
            }
        }

        ApparentTemperature temperature = ApparentTemperature.getTemperature(t.getTemperature());
        blit((NormalConfig.playerTemperatureX.get()), screenHeight - NormalConfig.playerTemperatureY.get(), (temperature.getIndex() - 1) * 30, 0, 30, 30);
        blit((NormalConfig.playerTemperatureX.get() + 32), screenHeight - NormalConfig.playerTemperatureY.get(), cold * 9, 30, 9, 9);
        blit((NormalConfig.playerTemperatureX.get() + 42), screenHeight - NormalConfig.playerTemperatureY.get(), heat * 9, 39, 9, 9);
        this.drawString(mc.fontRenderer, temperature.getTranslation().getFormattedText(), NormalConfig.playerTemperatureX.get() + 34, screenHeight - NormalConfig.playerTemperatureY.get() + 22, temperature.getColor().getColor());

        GlStateManager.disableAlphaTest();
        mc.getTextureManager().bindTexture(OverlayEventHandler.DEFAULT);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (index >=0)
        {
            index++;
        }
    }
}
