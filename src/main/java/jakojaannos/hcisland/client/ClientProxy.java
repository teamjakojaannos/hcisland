package jakojaannos.hcisland.client;

import jakojaannos.hcisland.CommonProxy;
import jakojaannos.hcisland.client.event.handler.GuiEventHandler;
import jakojaannos.hcisland.client.event.handler.RenderEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    public void onInit(FMLInitializationEvent event) {
        super.onInit(event);

        MinecraftForge.EVENT_BUS.register(GuiEventHandler.class);
        MinecraftForge.EVENT_BUS.register(RenderEventHandler.class);
    }
}
