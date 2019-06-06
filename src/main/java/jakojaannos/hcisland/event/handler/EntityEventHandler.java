package jakojaannos.hcisland.event.handler;

import jakojaannos.hcisland.config.HCIslandConfig;
import lombok.val;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

@Mod.EventBusSubscriber
public class EntityEventHandler {
    @SubscribeEvent
    public static void onEntity(LivingSpawnEvent.CheckSpawn event) {
        if (event.getWorld().provider.getDimension() != 0 || HCIslandConfig.world.disableBlockingMobSpawns) {
            return;
        }

        val entityKey = EntityList.getKey(event.getEntity());
        if (entityKey == null) {
            return;
        }

        if (Arrays.stream(HCIslandConfig.world.mobSpawnPreventionModIdBlacklist)
                  .anyMatch(blacklistEntry -> keyMatches(entityKey, blacklistEntry))) {
            val x = event.getX();
            val z = event.getZ();
            double distSq = x * x + z * z;
            val preventionRadius = HCIslandConfig.world.mobSpawnPreventionRadius * 16;
            if (distSq < preventionRadius * preventionRadius) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    private static boolean keyMatches(ResourceLocation entityKey, String blacklistEntry) {
        return entityKey.getResourceDomain().equalsIgnoreCase(blacklistEntry);
    }
}
