package jakojaannos.hcisland.event.handler;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.world.biome.BiomeHCBase;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EntityEventHandler {
    @SubscribeEvent
    public static void onEntity(LivingSpawnEvent.CheckSpawn event) {
        if (event.getWorld().provider.getDimension() == 0 && HCIslandConfig.world.allowAoAMobsOnIsland) {
            return;
        }

        // FIXME: Add actual optional dependency on Aoa and do a cleaner check with types
        if (event.getEntity().getClass().getName().contains("net.tslat.aoa3")) {
            if (event.getWorld().getBiome(new BlockPos(event.getX(), event.getY(), event.getZ())) instanceof BiomeHCBase) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
