package jakojaannos.hcisland.event.handler;

import jakojaannos.hcisland.world.WorldTypeHCIsland;
import jakojaannos.hcisland.world.biome.BiomeProviderHCIsland;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import lombok.var;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

@Mod.EventBusSubscriber
@Log4j2
public class SpawnPointEventHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCreateWorldSpawn(WorldEvent.CreateSpawnPosition event) {
        // Run only for overworld and HCIsland world type
        if (event.getWorld().provider.getDimension() != 0
                || !(event.getWorld().getWorldType() instanceof WorldTypeHCIsland)) {
            return;
        }

        // Cancel to prevent vanilla code from running
        event.setCanceled(true);

        val worldServer = (WorldServer) event.getWorld();
        val provider = worldServer.provider;
        val biomeProvider = (BiomeProviderHCIsland) worldServer.getBiomeProvider();
        val settings = HCIslandChunkGeneratorSettings.Factory.jsonToFactory(event.getSettings().getGeneratorOptions()).build();

        worldServer.findingSpawnPoint = true;

        val spawnBiomes = biomeProvider.getBiomesToSpawnIn();
        val random = new Random(provider.getSeed());
        val pos = biomeProvider.findBiomePosition(0,
                                                  0,
                                                  settings.getTotalRadialZoneRadius() * 16,
                                                  spawnBiomes, random);

        var x = 0;
        val y = provider.getAverageGroundLevel();
        var z = 0;

        if (pos != null) {
            x = pos.getX();
            z = pos.getZ();
        } else {
            log.warn("Unable to find spawn biome, forcing spawn at (0,{},0)", y);
        }

        worldServer.getWorldInfo().setSpawn(new BlockPos(x, y, z));
        worldServer.findingSpawnPoint = false;

        if (event.getSettings().isBonusChestEnabled()) {
            worldServer.createBonusChest();
        }
    }
}
