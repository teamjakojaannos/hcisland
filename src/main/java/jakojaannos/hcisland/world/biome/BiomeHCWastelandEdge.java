package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.init.Blocks;

public class BiomeHCWastelandEdge extends BiomeHCWastelandBase {
    public BiomeHCWastelandEdge() {
        super(getProperties());
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Wasteland Edge");
        props.setBaseHeight(0.2f);
        props.setHeightVariation(0.1f);
        props.setTemperature(HCIslandConfig.world.temperatureWastelandEdge);

        return props;
    }
}
