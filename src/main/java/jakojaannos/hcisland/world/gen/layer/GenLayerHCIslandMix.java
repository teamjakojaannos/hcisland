package jakojaannos.hcisland.world.gen.layer;

import jakojaannos.hcisland.util.UnitHelper;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerHCIslandMix extends GenLayer {
    private final GenLayer originalChain;
    private final GenLayer hcChain;
    private final HCIslandChunkGeneratorSettings settings;

    public GenLayerHCIslandMix(long seed, GenLayer originalChain, GenLayer hcChain, HCIslandChunkGeneratorSettings settings) {
        super(seed);
        this.originalChain = originalChain;
        this.hcChain = hcChain;
        this.settings = settings;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        final int[] originalInts = originalChain.getInts(areaX, areaY, areaWidth, areaHeight);
        final int[] hcInts = hcChain.getInts(areaX, areaY, areaWidth, areaHeight);

        int[] ints = IntCache.getIntCache(areaWidth * areaHeight);

        final int radiusInChunks = settings.getTotalRadialZoneRadius();
        final float radiusInGenLayerUnitsBeforeStretching = radiusInChunks * UnitHelper.CHUNKS_TO_GEN_LAYER_CONVERSION_RATIO;
        final float radiusSq = radiusInGenLayerUnitsBeforeStretching * radiusInGenLayerUnitsBeforeStretching;

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
