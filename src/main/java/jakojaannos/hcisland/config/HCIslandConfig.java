package jakojaannos.hcisland.config;

import jakojaannos.hcisland.ModInfo;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Config root
 */
@Mod.EventBusSubscriber
@Config(modid = ModInfo.MODID)
public class HCIslandConfig {
    @Config.Comment("World generation")
    public static ConfigWorld world = new ConfigWorld();

    @Config.Comment("Clientside only configuration")
    public static ConfigClient client = new ConfigClient();


    public static class ConfigWorld {
        @Config.Comment("Overrides for generator settings default values")
        public String generatorSettingsDefaults = "";

        @Config.RequiresMcRestart
        public float temperatureIsland = 0.8f;

        @Config.RequiresMcRestart
        public float temperatureIslandBeach = 1.0f;

        @Config.RequiresMcRestart
        public float temperatureOcean = 1.25f;

        @Config.RequiresMcRestart
        public float temperatureWasteland = 1.5f;

        @Config.RequiresMcRestart
        public float temperatureWastelandBeach = 1.5f;

        @Config.RequiresMcRestart
        public float temperatureWastelandEdge = 1.25f;

        @Config.Comment("Set to false to prevent spawning of Advent of Ascension mobs on the island")
        public boolean allowAoAMobsOnIsland = true;
    }

    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ModInfo.MODID)) {
            ConfigManager.sync(ModInfo.MODID, Config.Type.INSTANCE);
        }
    }
}
