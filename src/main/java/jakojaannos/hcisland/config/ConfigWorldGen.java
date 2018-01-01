package jakojaannos.hcisland.config;

import jakojaannos.api.world.AdvancedBiomeBase;
import jakojaannos.hcisland.ModInfo;
import jakojaannos.hcisland.world.biome.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

import javax.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
@Config(modid = ModInfo.MODID, category = "worldgen")
public class ConfigWorldGen {
    public BiomeConfig island = new BiomeConfig(2, 6, 1.25f, BiomeHCIsland.getDefaultConfig());

    public BiomeConfig islandBeach = new BiomeConfig(2, 5, 1.5f, BiomeHCIslandBeach.getDefaultConfig());

    public BiomeConfig ocean = new BiomeConfig(16, 128, 1.5f, BiomeHCOcean.getDefaultConfig());

    public BiomeConfig wasteland = new BiomeConfig(2, 64, 1.5f, BiomeHCWasteland.getDefaultConfig());

    public BiomeConfig wasteland_beach = new BiomeConfig(2, 5, 1.5f, BiomeHCWastelandBeach.getDefaultConfig());

    public BiomeConfig wasteland_edge = new BiomeConfig(2, 8, 1.25f, BiomeHCWastelandEdge.getDefaultConfig());


    @Comment("Block used as lake liquid.")
    public String blockLakeLiquid = "minecraft:lava";


    @Comment("Should lakes generate in the island biome")
    public boolean generateLakesIsland = false;

    @Comment("Should waterfalls generate in the island biome")
    public boolean generateFallsIsland = false;


    @Comment("Should lava lakes generate in the wasteland biome")
    public boolean generateLakesWasteland = false;

    @Comment("Should fire generate in wasteland biome")
    public boolean generateFireWasteland = true;


    @Comment("Liquid level of the lake")
    public int lakeSeaLevel = 48;


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Helper accessors
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static class BiomeConfig {
        private final int radiusMin;

        public int radius;
        public float temperature;
        public AdvancedBiomeBase.Config config;

        public BiomeConfig(int radiusMin, int radius, float temperature, AdvancedBiomeBase.Config config) {
            this.radiusMin = radiusMin;

            this.radius = radius;
            this.temperature = temperature;
            this.config = config;
        }

        public int getRadius() {
            return radius < radiusMin ? radiusMin : radius;
        }
    }
}

