package jakojaannos.hcisland.world.gen.layer;

import jakojaannos.hcisland.config.HCIslandConfig;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerHCIslandMix extends GenLayer {

    private final GenLayer originalChain;
    private final GenLayer hcChain;

    public GenLayerHCIslandMix(long seed, GenLayer originalChain, GenLayer hcChain) {
        super(seed);
        this.originalChain = originalChain;
        this.hcChain = hcChain;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        final int[] originalInts = originalChain.getInts(areaX, areaY, areaWidth, areaHeight);
        final int[] hcInts = hcChain.getInts(areaX, areaY, areaWidth, areaHeight);

        int[] ints = IntCache.getIntCache(areaWidth * areaHeight);


        final int radius = HCIslandConfig.worldGen.island.getRadius()
                + HCIslandConfig.worldGen.islandBeach.getRadius()
                + HCIslandConfig.worldGen.ocean.getRadius()
                + HCIslandConfig.worldGen.wasteland_beach.getRadius()
                + HCIslandConfig.worldGen.wasteland.getRadius()
                + HCIslandConfig.worldGen.wasteland_edge.getRadius();
        final int radiusSq = radius * radius;

        for (int x = 0; x < areaWidth; x++) {
            for (int y = 0; y < areaHeight; y++) {
                final long distSq = (x + areaX) * (x + areaX) + (y + areaY) * (y + areaY);
                final int index = x + (y * areaWidth);

                if (distSq > radiusSq) {
                    ints[index] = originalInts[index];
                } else {
                    ints[index] = hcInts[index];
                }
            }
        }

        return ints;
    }
}
