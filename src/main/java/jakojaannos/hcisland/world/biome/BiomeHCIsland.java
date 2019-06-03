package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.world.gen.BiomeSettings;

public class BiomeHCIsland extends BiomeHCIslandBase<BiomeSettings.Forest> {
    public BiomeHCIsland() {
        super(getProperties());
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Island");
        props.setBaseHeight(0.25f);
        props.setHeightVariation(0.1f);
        props.setTemperature(HCIslandConfig.world.temperatureIsland);

        return props;
    }

    @Override
    protected void applyBiomeSettings(BiomeSettings.Forest settings) {
        super.applyBiomeSettings(settings);
        this.decorator.treesPerChunk = settings.treesPerChunk;
        this.decorator.grassPerChunk = settings.grassPerChunk;
        this.decorator.flowersPerChunk = settings.flowersPerChunk;
    }
}
