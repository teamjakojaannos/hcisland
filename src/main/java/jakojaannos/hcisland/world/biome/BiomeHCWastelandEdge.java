package jakojaannos.hcisland.world.biome;

import com.google.common.collect.Lists;
import jakojaannos.api.helpers.BlockHelper;
import jakojaannos.api.world.AdvancedBiomeBase;
import jakojaannos.api.world.BlockLayer;
import jakojaannos.hcisland.config.HCIslandConfig;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class BiomeHCWastelandEdge extends BiomeHCWasteland {
    public BiomeHCWastelandEdge() {
        super(getProperties(), HCIslandConfig.worldGen.wasteland_edge);
        setSeaLevelOverride(HCIslandConfig.worldGen.wasteland_edge.config.seaLevelOverride);

        setOceanBlock(BlockHelper.stringToBlockstateWithFallback(Blocks.WATER.getDefaultState(),
                HCIslandConfig.worldGen.wasteland_edge.config.oceanBlock));
    }

    // TODO: Gradually fade from wasteland filler/stone block substitutes to stone when approaching the outer edge
    //      -> requires figuring out the actual radius of biomes so that distance to edge can be calculated
    //      -> should be doable by trial-and-error and a bit of digging GenLayer code
    //      -> Actual radius seems to be roughly 4 x configured radius

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Wasteland Edge");
        props.setBaseHeight(0.2f);
        props.setHeightVariation(0.1f);
        props.setTemperature(HCIslandConfig.worldGen.wasteland_edge.temperature);

        return props;
    }

    public static AdvancedBiomeBase.Config getDefaultConfig() {
        AdvancedBiomeBase.Config cfg = new AdvancedBiomeBase.Config();
        cfg.layers = new String[]{
                "8, minecraft:netherrack",
                "2, minecraft:gravel"
        };
        cfg.underwaterLayers = new String[]{
                "8, minecraft:netherrack"
        };
        cfg.stoneBlock = "minecraft:stone";
        cfg.oceanBlock = "minecraft:water";
        cfg.seaLevelFuzzOffset = 0.0f;
        cfg.seaLevelFuzzScale = 0.0f;
        cfg.bedrockDepth = 16;
        cfg.seaLevelOverride = -1;

        return cfg;
    }
}
