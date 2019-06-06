package jakojaannos.hcisland.event.handler;

import jakojaannos.hcisland.config.HCIslandConfig;
import lombok.val;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EntityEventHandler {
    @SubscribeEvent
    public static void onEntity(LivingSpawnEvent.CheckSpawn event) {
        if (event.getWorld().provider.getDimension() != 0 || HCIslandConfig.world.disableBlockingMobSpawns) {
            return;
        }

        // FIXME: Add actual optional dependency on Aoa and do a cleaner check with types
        if (event.getEntity().getClass().getName().contains("net.tslat.aoa3")) {
            val x = event.getX();
            val z = event.getZ();
            double distSq = x * x + z * z;
            val preventionRadius = HCIslandConfig.world.disableAoAMobSpawningInRadius * 16;
            if (distSq < preventionRadius * preventionRadius) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
