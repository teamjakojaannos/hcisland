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

        public float temperatureIsland = 0.8f;
        public float temperatureIslandBeach = 1.0f;

        public float temperatureOcean = 1.25f;

        public float temperatureWasteland = 1.5f;
        public float temperatureWastelandBeach = 1.5f;
        public float temperatureWastelandEdge = 1.25f;
    }
}
