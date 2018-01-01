package jakojaannos.hcisland.world.biome;

import jakojaannos.api.world.AdvancedBiomeBase;
import jakojaannos.hcisland.config.ConfigWorldGen;
import jakojaannos.hcisland.config.HCIslandConfig;

public class BiomeHCWasteland extends BiomeHCBase {
    public BiomeHCWasteland() {
        this(getProperties(), HCIslandConfig.worldGen.wasteland);
    }

    protected BiomeHCWasteland(BiomeProperties properties, ConfigWorldGen.BiomeConfig config) {
        super(properties, config);

        this.spawnableCreatureList.clear();

        this.decorator.treesPerChunk = -999;
        this.decorator.deadBushPerChunk = 0;
        this.decorator.reedsPerChunk = 0;
        this.decorator.cactiPerChunk = 0;
        this.decorator.generateFalls = false;
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Wasteland");
        props.setBaseHeight(0.4f);
        props.setHeightVariation(0.25f);
        props.setTemperature(HCIslandConfig.worldGen.wasteland.temperature);

        return props;
    }

    public static AdvancedBiomeBase.Config getDefaultConfig() {
        AdvancedBiomeBase.Config cfg = new AdvancedBiomeBase.Config();
        cfg.layers = new String[]{
                "8, minecraft:netherrack"
        };
        cfg.underwaterLayers = new String[]{
                "1, minecraft:obsidian",
                "1, minecraft:sandstone",
                "1, minecraft:hardened_clay",
                "8, minecraft:netherrack"
        };
        cfg.stoneBlock = "minecraft:netherrack";
        cfg.seaLevelFuzzOffset = 0.0f;
        cfg.seaLevelFuzzScale = 0.0f;
        cfg.bedrockDepth = 16;

        return cfg;
    }
}
