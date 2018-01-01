package jakojaannos.hcisland.world.biome;

import jakojaannos.api.world.AdvancedBiomeBase;
import jakojaannos.hcisland.config.HCIslandConfig;

public class BiomeHCOcean extends BiomeHCBase {
    public BiomeHCOcean() {
        super(getProperties(), HCIslandConfig.worldGen.ocean);

        this.decorator.treesPerChunk = -999;
        this.decorator.deadBushPerChunk = 0;
        this.decorator.reedsPerChunk = 0;
        this.decorator.cactiPerChunk = 0;
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Ocean");
        props.setBaseHeight(-1.8f);
        props.setHeightVariation(0.0f);
        props.setTemperature(HCIslandConfig.worldGen.ocean.temperature);

        return props;
    }

    public static AdvancedBiomeBase.Config getDefaultConfig() {
        AdvancedBiomeBase.Config cfg = new AdvancedBiomeBase.Config();
        cfg.layers = new String[]{
                "8, minecraft:netherrack"
        };
        cfg.underwaterLayers = new String[]{
                "2, minecraft:obsidian",
                "1, minecraft:sandstone",
                "1, minecraft:hardened_clay",
                "4, minecraft:netherrack"
        };
        cfg.seaLevelFuzzOffset = 0.0f;
        cfg.seaLevelFuzzScale = 0.0f;
        cfg.bedrockDepth = 16;

        return cfg;
    }
}
