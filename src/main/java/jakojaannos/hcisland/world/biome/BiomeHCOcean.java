package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.init.ModBiomes;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.world.biome.Biome;

public class BiomeHCOcean extends BiomeHCBase<BiomeSettings> {
    public BiomeHCOcean() {
        super(getProperties());

        this.decorator.treesPerChunk = -999;
        this.decorator.deadBushPerChunk = 0;
        this.decorator.reedsPerChunk = 0;
        this.decorator.cactiPerChunk = 0;

        setSeaLevelFuzz(1.0f, 4.0f);
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Ocean");
        props.setBaseHeight(-1.8f);
        props.setHeightVariation(0.1f);
        props.setTemperature(HCIslandConfig.world.temperatureOcean);

        return props;
    }

    @Override
    public boolean isOceanic() {
        return true;
    }

    @Override
    public Biome getBeachBiome() {
        return ModBiomes.BEACH;
    }
}
