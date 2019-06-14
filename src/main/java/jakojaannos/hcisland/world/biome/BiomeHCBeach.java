package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.world.gen.LayeredBiomeSettings;

public class BiomeHCBeach extends BiomeHCIslandBase {
    public BiomeHCBeach() {
        super(getProperties());

        this.spawnableCreatureList.clear();
        setSeaLevelFuzz(0.5f, -1.0f);
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Island Beach");
        props.setTemperature(HCIslandConfig.world.temperatureIslandBeach);

        return props;
    }
}
