package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;

public class BiomeHCOcean extends BiomeHCBase<HCIslandChunkGeneratorSettings.BiomeSettings> {
    public BiomeHCOcean() {
        super(getProperties(), settings -> settings.ocean);

        this.decorator.treesPerChunk = -999;
        this.decorator.deadBushPerChunk = 0;
        this.decorator.reedsPerChunk = 0;
        this.decorator.cactiPerChunk = 0;

        setSeaLevelFuzz(1.0f, 4.0f);
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Ocean");
        props.setBaseHeight(-1.8f);
        props.setHeightVariation(0.0f);
        props.setTemperature(HCIslandConfig.world.temperatureOcean);

        return props;
    }
}
