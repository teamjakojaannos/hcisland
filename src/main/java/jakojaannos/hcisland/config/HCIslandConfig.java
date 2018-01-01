package jakojaannos.hcisland.config;

import jakojaannos.hcisland.ModInfo;
import net.minecraftforge.common.config.Config;

/**
 * Config root
 */
@Config(modid = ModInfo.MODID)
public class HCIslandConfig {
    @Config.Comment("World generation")
    public static ConfigWorldGen worldGen = new ConfigWorldGen();

    @Config.Comment("Clientside only configuration")
    public static ConfigClient client = new ConfigClient();
}
