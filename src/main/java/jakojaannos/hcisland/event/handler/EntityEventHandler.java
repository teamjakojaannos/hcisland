package jakojaannos.hcisland.event.handler;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
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
            final HCIslandChunkGeneratorSettings settings = HCIslandChunkGeneratorSettings.Factory.jsonToFactory(event.getWorld().getWorldInfo().getGeneratorOptions()).build();

            final int totalRadiusIsland = settings.island.radius;
            final int totalRadiusIslandBeach = totalRadiusIsland + settings.islandBeach.radius;
            final int totalRadiusOcean = totalRadiusIslandBeach + settings.ocean.radius;
            final int totalRadiusWastelandBeach = totalRadiusOcean + settings.wastelandBeach.radius;
            final int totalRadiusWasteland = totalRadiusWastelandBeach + settings.wasteland.radius;
            final int totalRadiusWastelandEdge = (totalRadiusWasteland + settings.wastelandEdge.radius) * 4; // FIXME: Coordinates use weird scale due to biome layer scaling

            final float distToSpawnSq = event.getX() * event.getX() + event.getZ() * event.getZ();

            if (distToSpawnSq < totalRadiusWastelandEdge * totalRadiusWastelandEdge) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
