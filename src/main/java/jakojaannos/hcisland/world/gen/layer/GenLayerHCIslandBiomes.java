package jakojaannos.hcisland.world.gen.layer;

import jakojaannos.hcisland.init.HCIslandBiomes;
import jakojaannos.hcisland.util.UnitHelper;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import lombok.val;
import net.minecraft.init.Biomes;
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

                final float totalRadius = settings.getTotalRadialZoneRadius() * UnitHelper.CHUNKS_TO_GEN_LAYER_CONVERSION_RATIO;

                if (distSq > totalRadius * totalRadius) {
                    ints[index] = parentInts[index];
                } else {
                    val biome = settings.getBiomeAtDistanceSq(distSq);
                    // TODO: Make fallback biome configurable
                    ints[index] = Biome.getIdForBiome(biome == null ? Biomes.FOREST : biome);
                }
            }
        }

        return ints;
    }
}
