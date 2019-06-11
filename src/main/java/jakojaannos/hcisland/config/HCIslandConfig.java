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

        @Config.Comment({
                "Silently adds this offset to radii of all biomes. This is to avoid biomes \"disappearing\" during ",
                "fuzzy zoom biome generation layer. This also mitigates issues with biomes becoming noticeably ",
                "smaller in some cases where world gen seed happens to be biased towards scaling down biomes.",
        })
        public int generatorBiomeRadiusOffset = 1;

        @Config.RequiresMcRestart
        public float temperatureIsland = 0.8f;

        @Config.RequiresMcRestart
        public float temperatureIslandBeach = 1.0f;

        @Config.RequiresMcRestart
        public float temperatureOcean = 1.25f;

        @Config.RequiresMcRestart
        public float temperatureWasteland = 1.5f;

        @Config.RequiresMcRestart
        public float temperatureWastelandEdge = 1.25f;

        @Config.Comment("Set to true to completely disable mob spawn prevention")
        public boolean disableBlockingMobSpawns = false;

        @Config.Comment("Radius inside which blacklisted mobs are not allowed to spawn")
        public int mobSpawnPreventionRadius = 6;

        @Config.Comment("Blacklist of mod IDs from which mobs are not allowed to spawn inside prevention radius")
        public String[] mobSpawnPreventionModIdBlacklist = {"aoa3"};

        @Config.Comment("Blacklist of mob entity keys which are not allowed to spawn inside prevention radius. e.g. \"minecraft:zombie\" would prevent zombies from spawning")
        public String[] mobSpawnPreventionBlacklist = {};
    }

    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ModInfo.MODID)) {
            ConfigManager.sync(ModInfo.MODID, Config.Type.INSTANCE);
        }
    }
}
