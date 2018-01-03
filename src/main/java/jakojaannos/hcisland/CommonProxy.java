package jakojaannos.hcisland;

import jakojaannos.hcisland.event.handler.WorldGenEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class CommonProxy {
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.TERRAIN_GEN_BUS.register(WorldGenEventHandler.class);
    }
}
