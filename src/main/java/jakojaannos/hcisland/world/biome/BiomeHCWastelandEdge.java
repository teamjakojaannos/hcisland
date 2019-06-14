package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.world.gen.LayeredBiomeSettings;

public class BiomeHCWastelandEdge<TSettings extends LayeredBiomeSettings.Wasteland> extends BiomeHCWastelandBase {
    public BiomeHCWastelandEdge() {
        super(getProperties());
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Wasteland Edge");
        props.setTemperature(HCIslandConfig.world.temperatureWastelandEdge);

        return props;
    }
}
