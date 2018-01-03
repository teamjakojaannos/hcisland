package jakojaannos.hcisland.client.event.handler;

import jakojaannos.hcisland.HardcoreIsland;
import jakojaannos.hcisland.config.HCIslandConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiCustomizeWorldScreen;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEventHandler {
    /**
     * Make "Hardcore Island" -worldtype default if config flag is set
     */
    @SubscribeEvent
    public static void onOpenGui(GuiOpenEvent event) {
        // If opening Create World gui and not recreating, set default worldtype
        // HACK: If opening via "Recreate World" -option, previous gui is GuiScreenWorking. Minecraft.currentScreen changes AFTER the event so we can use that to identify a re-create
        if (event.getGui() instanceof GuiCreateWorld && Minecraft.getMinecraft().currentScreen instanceof GuiWorldSelection) {
            if (HCIslandConfig.client.makeWorldTypeDefault) {
                int index = -1;
                for (int i = 0; i < WorldType.WORLD_TYPES.length; i++) {
                    if (WorldType.WORLD_TYPES[i] == HardcoreIsland.WORLD_TYPE) {
                        index = i;
                        break;
                    }
                }

                ((GuiCreateWorld)event.getGui()).selectedIndex = index != -1 ? index : 0;
            }
        }
    }
}
