package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.world.gen.LayeredBiomeSettings;

public class BiomeHCForest extends BiomeHCIslandBase {
    public BiomeHCForest() {
        super(getProperties());
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Island");
        props.setTemperature(HCIslandConfig.world.temperatureIsland);

        return props;
    }
}
