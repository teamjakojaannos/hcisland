package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.config.HCIslandConfig;

public class BiomeHCIslandLowlands extends BiomeHCIslandBase {
    public BiomeHCIslandLowlands() {
        super(getProperties());
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Island Lowlands");
        props.setTemperature(HCIslandConfig.world.temperatureIsland);

        return props;
    }
}
