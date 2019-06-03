package jakojaannos.hcisland.event.handler;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.world.WorldTypeHCIsland;
import lombok.val;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EntityEventHandler {
    private static float radiusInBlocksSq = 0f;

    @SubscribeEvent
    public static void onLoad(WorldEvent.Load event) {
        if (event.getWorld().provider.getDimension() == 0 && event.getWorld().getWorldType() instanceof WorldTypeHCIsland) {
            val worldType = (WorldTypeHCIsland) event.getWorld().getWorldType();
            val radiusInChunks = worldType.getSettings().getTotalRadialZoneRadius();
            val radiusInBlocks = radiusInChunks * 16;
            radiusInBlocksSq = radiusInBlocks * radiusInBlocks;
        }
    }

    @SubscribeEvent
    public static void onEntity(LivingSpawnEvent.CheckSpawn event) {
        if (event.getWorld().provider.getDimension() != 0 || HCIslandConfig.world.allowAoAMobsOnIsland) {
            return;
        }

        // FIXME: Add actual optional dependency on Aoa and do a cleaner check with types
        if (event.getEntity().getClass().getName().contains("net.tslat.aoa3")) {
            val x = event.getX();
            val z = event.getZ();
            double distSq = x * x + z * z;
            if (distSq < radiusInBlocksSq) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
