package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;

public class BiomeHCIslandBeach extends BiomeHCIslandBase<HCIslandChunkGeneratorSettings.BiomeSettings.Beach> {
    public BiomeHCIslandBeach() {
        super(getProperties(), settings -> settings.islandBeach);

        this.spawnableCreatureList.clear();
        setSeaLevelFuzz(1.5f, 2.0f);
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Island Beach");
        props.setBaseHeight(0.24f);
        props.setHeightVariation(0.0f);
        props.setTemperature(HCIslandConfig.world.temperatureIslandBeach);

        return props;
    }

    @Override
    protected void applyBiomeSettings(HCIslandChunkGeneratorSettings.BiomeSettings.Beach settings) {
        super.applyBiomeSettings(settings);
        this.decorator.cactiPerChunk = settings.cactiPerChunk;
    }
}
