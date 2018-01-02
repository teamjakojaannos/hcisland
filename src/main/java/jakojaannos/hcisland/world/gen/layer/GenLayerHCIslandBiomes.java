package jakojaannos.hcisland.world.gen.layer;

import jakojaannos.hcisland.init.HCIslandBiomes;
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

                final int totalRadiusIsland = this.settings.island.radius;
                final int totalRadiusIslandBeach = totalRadiusIsland + this.settings.islandBeach.radius;
                final int totalRadiusOcean = totalRadiusIslandBeach + this.settings.ocean.radius;
                final int totalRadiusWastelandBeach = totalRadiusOcean + this.settings.wastelandBeach.radius;
                final int totalRadiusWasteland = totalRadiusWastelandBeach + this.settings.wasteland.radius;
                final int totalRadiusWastelandEdge = totalRadiusWasteland + this.settings.wastelandEdge.radius;

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

    private static int sq(int a) {
        return a * a;
    }
}
