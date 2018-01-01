package jakojaannos.hcisland.world.biome;

import jakojaannos.api.world.AdvancedBiomeBase;
import jakojaannos.hcisland.config.HCIslandConfig;

public class BiomeHCIslandBeach extends BiomeHCBase {
    public BiomeHCIslandBeach() {
        super(getProperties(), HCIslandConfig.worldGen.islandBeach);

        this.decorator.generateFalls = HCIslandConfig.worldGen.generateFallsIsland;

        this.spawnableCreatureList.clear();
        this.decorator.treesPerChunk = -999;
        this.decorator.deadBushPerChunk = 0;
        this.decorator.reedsPerChunk = 0;
        this.decorator.cactiPerChunk = 10;
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Island Beach");
        props.setBaseHeight(0.24f);
        props.setHeightVariation(0.0f);
        props.setTemperature(HCIslandConfig.worldGen.islandBeach.temperature);

        return props;
    }

    public static AdvancedBiomeBase.Config getDefaultConfig() {
        AdvancedBiomeBase.Config cfg = new AdvancedBiomeBase.Config();
        cfg.fallbackFillerBlock = "minecraft:sand";
        cfg.fallbackTopBlock = "minecraft:sand";
        cfg.fallBackFillerDepth = 10;

        cfg.layers = new String[]{
                "10, minecraft:sand",
                "2, minecraft:sandstone",
                "1, minecraft:clay",
                "2, minecraft:gravel"
        };
        cfg.underwaterLayers = new String[]{
                "2, minecraft:obsidian",
                "1, minecraft:sandstone",
                "1, minecraft:hardened_clay",
                "2, minecraft:netherrack"
        };

        cfg.seaLevelFuzzOffset = -2.0f;
        cfg.seaLevelFuzzScale = 1.5f;

        return cfg;
    }
}
