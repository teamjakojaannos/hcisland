package jakojaannos.hcisland.world.gen.layer;

import jakojaannos.hcisland.init.HCIslandBiomes;
import jakojaannos.hcisland.util.UnitHelper;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerHCIslandBiomes extends GenLayer {

    private HCIslandChunkGeneratorSettings settings;

    public GenLayerHCIslandBiomes(long seed, GenLayer parent, HCIslandChunkGeneratorSettings settings) {
        super(seed);
        this.settings = settings;
        this.parent = parent;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        final int[] parentInts = parent.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] ints = IntCache.getIntCache(areaWidth * areaHeight);

        for (int x = 0; x < areaWidth; x++) {
            for (int y = 0; y < areaHeight; y++) {
                final long distSq = (long) (x + areaX) * (x + areaX) + (long) (y + areaY) * (y + areaY);
                final int index = x + (y * areaWidth);

                final float totalRadiusIsland = this.settings.island.radius * UnitHelper.CHUNKS_TO_GEN_LAYER_CONVERSION_RATIO;
                final float totalRadiusIslandBeach = totalRadiusIsland + this.settings.islandBeach.radius * UnitHelper.CHUNKS_TO_GEN_LAYER_CONVERSION_RATIO;
                final float totalRadiusOcean = totalRadiusIslandBeach + this.settings.ocean.radius * UnitHelper.CHUNKS_TO_GEN_LAYER_CONVERSION_RATIO;
                final float totalRadiusWastelandBeach = totalRadiusOcean + this.settings.wastelandBeach.radius * UnitHelper.CHUNKS_TO_GEN_LAYER_CONVERSION_RATIO;
                final float totalRadiusWasteland = totalRadiusWastelandBeach + this.settings.wasteland.radius * UnitHelper.CHUNKS_TO_GEN_LAYER_CONVERSION_RATIO;
                final float totalRadiusWastelandEdge = totalRadiusWasteland + this.settings.wastelandEdge.radius * UnitHelper.CHUNKS_TO_GEN_LAYER_CONVERSION_RATIO;

                if (distSq > sq(totalRadiusWastelandEdge)) {
                    ints[index] = parentInts[index];
                } else if (distSq > sq(totalRadiusWasteland)) {
                    ints[index] = Biome.getIdForBiome(HCIslandBiomes.WASTELAND_EDGE);
                } else if (distSq > sq(totalRadiusWastelandBeach)) {
                    ints[index] = Biome.getIdForBiome(HCIslandBiomes.WASTELAND);
                } else if (distSq > sq(totalRadiusOcean)) {
                    ints[index] = Biome.getIdForBiome(HCIslandBiomes.WASTELAND_BEACH);
                } else if (distSq > sq(totalRadiusIslandBeach)) {
                    ints[index] = Biome.getIdForBiome(HCIslandBiomes.OCEAN);
                } else if (distSq > sq(totalRadiusIsland)) {
                    ints[index] = Biome.getIdForBiome(HCIslandBiomes.ISLAND_BEACH);
                } else {
                    ints[index] = Biome.getIdForBiome(HCIslandBiomes.ISLAND);
                }
            }
        }

        return ints;
    }

    private static float sq(float a) {
        return a * a;
    }
}
