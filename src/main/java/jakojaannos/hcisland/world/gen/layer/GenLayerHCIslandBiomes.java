package jakojaannos.hcisland.world.gen.layer;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.init.HCIslandBiomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import javax.vecmath.Vector2d;

public class GenLayerHCIslandBiomes extends GenLayer {

    public GenLayerHCIslandBiomes(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        final int[] parentInts = parent.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] ints = IntCache.getIntCache(areaWidth * areaHeight);

        final Vector2d unitX = new Vector2d(1.0, 0.0);

        for (int x = 0; x < areaWidth; x++) {
            for (int y = 0; y < areaHeight; y++) {
                final long distSq = (long) (x + areaX) * (x + areaX) + (long) (y + areaY) * (y + areaY);
                final int index = x + (y * areaWidth);

                final int totalRadiusIsland = HCIslandConfig.worldGen.island.getRadius();
                final int totalRadiusIslandBeach = totalRadiusIsland + HCIslandConfig.worldGen.islandBeach.getRadius();
                final int totalRadiusOcean = totalRadiusIslandBeach + HCIslandConfig.worldGen.ocean.getRadius();
                final int totalRadiusWastelandBeach = totalRadiusOcean + HCIslandConfig.worldGen.wasteland_beach.getRadius();
                final int totalRadiusWasteland = totalRadiusWastelandBeach + HCIslandConfig.worldGen.wasteland.getRadius();
                final int totalRadiusWastelandEdge = totalRadiusWasteland + HCIslandConfig.worldGen.wasteland_edge.getRadius();

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
