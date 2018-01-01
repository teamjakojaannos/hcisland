package jakojaannos.hcisland.world.biome;

import jakojaannos.api.world.AdvancedBiomeBase;
import jakojaannos.hcisland.config.HCIslandConfig;

public class BiomeHCIsland extends BiomeHCBase {
    public BiomeHCIsland() {
        super(getProperties(), HCIslandConfig.worldGen.island);
        this.decorator.generateFalls = HCIslandConfig.worldGen.generateFallsIsland;
        this.decorator.treesPerChunk = 7;
        this.decorator.grassPerChunk = 4;
        this.decorator.flowersPerChunk = 25;
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Island");
        props.setBaseHeight(0.25f);
        props.setHeightVariation(0.1f);
        props.setTemperature(HCIslandConfig.worldGen.island.temperature);

        return props;
    }

    public static AdvancedBiomeBase.Config getDefaultConfig() {
        AdvancedBiomeBase.Config cfg = new AdvancedBiomeBase.Config();
        cfg.layers = new String[]{
                "1, minecraft:grass",
                "5, minecraft:dirt",
                "1, minecraft:clay",
                "2, minecraft:gravel"
        };
        cfg.underwaterLayers = new String[]{

        };

        return cfg;
    }
}
