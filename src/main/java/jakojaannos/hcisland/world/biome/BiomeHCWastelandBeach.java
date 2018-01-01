package jakojaannos.hcisland.world.biome;

import com.google.common.collect.Lists;
import jakojaannos.api.world.AdvancedBiomeBase;
import jakojaannos.api.world.BlockLayer;
import jakojaannos.hcisland.config.HCIslandConfig;

public class BiomeHCWastelandBeach extends BiomeHCWasteland {
    public BiomeHCWastelandBeach() {
        super(getProperties(), HCIslandConfig.worldGen.wasteland_beach);
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Wasteland Beach");
        props.setBaseHeight(0.4f);
        props.setHeightVariation(0.25f);
        props.setTemperature(HCIslandConfig.worldGen.wasteland_beach.temperature);

        return props;
    }

    public static AdvancedBiomeBase.Config getDefaultConfig() {
        AdvancedBiomeBase.Config cfg = new AdvancedBiomeBase.Config();
        cfg.fallBackFillerDepth = 8;
        cfg.fallbackTopBlock = "minecraft:netherrack";
        cfg.fallbackFillerBlock = "minecraft:netherrack";
        cfg.layers = new String[]{
                "16, minecraft:netherrack"
        };
        cfg.underwaterLayers = new String[]{
                "1, minecraft:obsidian",
                "1, minecraft:sandstone",
                "1, minecraft:hardened_clay",
                "10, minecraft:netherrack"
        };
        cfg.seaLevelFuzzOffset = 0.0f;
        cfg.seaLevelFuzzScale = 0.0f;
        cfg.bedrockDepth = 16;

        return cfg;
    }
}
