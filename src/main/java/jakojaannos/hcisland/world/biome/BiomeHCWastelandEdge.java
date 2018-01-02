package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.config.HCIslandConfig;
import net.minecraft.init.Blocks;

public class BiomeHCWastelandEdge extends BiomeHCWasteland {
    public BiomeHCWastelandEdge() {
        super(getProperties(), settings -> settings.wastelandEdge);

        setSeaLevelOverride(-1);
        setStoneBlock(Blocks.STONE.getDefaultState());
        setOceanBlock(Blocks.WATER.getDefaultState());
    }

    // TODO: Gradually fade from wasteland filler/stone block substitutes to stone/grass/sand when approaching the outer edge
    //      -> requires figuring out the actual radius of biomes so that distance to edge can be calculated
    //      -> should be doable by trial-and-error and a bit of digging GenLayer code
    //      -> Actual radius seems to be roughly 4 x configured radius, but that's not yet accurate enough

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Wasteland Edge");
        props.setBaseHeight(0.2f);
        props.setHeightVariation(0.1f);
        props.setTemperature(HCIslandConfig.world.temperatureWastelandEdge);

        return props;
    }
}
