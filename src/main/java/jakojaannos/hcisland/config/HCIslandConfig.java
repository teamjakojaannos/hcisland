package jakojaannos.hcisland.config;

import jakojaannos.hcisland.ModInfo;
import net.minecraftforge.common.config.Config;

/**
 * Config root
 */
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
    }
}
