package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.config.HCIslandConfig;

public class BiomeHCWastelandBeach extends BiomeHCWasteland {
    public BiomeHCWastelandBeach() {
        super(getProperties(), settings -> settings.wastelandBeach);
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Wasteland Beach");
        props.setBaseHeight(0.4f);
        props.setHeightVariation(0.25f);
        props.setTemperature(HCIslandConfig.world.temperatureWastelandBeach);

        return props;
    }
}
