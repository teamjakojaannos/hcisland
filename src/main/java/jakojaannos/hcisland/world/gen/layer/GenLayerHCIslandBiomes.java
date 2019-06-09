package jakojaannos.hcisland.world.gen.layer;

import lombok.val;
import lombok.var;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import java.util.function.BiFunction;

public class GenLayerHCIslandBiomes extends GenLayer {
    private final long totalRadius;
    private final double unitScale;
    private final BiFunction<Long, Double, Biome> biomeProvider;

    public GenLayerHCIslandBiomes(long seed,
                                  GenLayer parent,
                                  long totalRadius,
                                  double unitScale,
                                  BiFunction<Long, Double, Biome> biomeProvider
    ) {
        super(seed);
        this.parent = parent;

        this.totalRadius = totalRadius;
        this.unitScale = unitScale;
        this.biomeProvider = biomeProvider;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        // TODO: Generate parent ints lazily; No need to generate if we are completely on the island
        // TODO: Do not run custom generator at all if whole area is well outside custom generator total radius
        val parentInts = parent.getInts(areaX, areaY, areaWidth, areaHeight);

        val scaledRadius = this.totalRadius * unitScale;
        var ints = IntCache.getIntCache(areaWidth * areaHeight);
        for (var x = 0; x < areaWidth; x++) {
            for (var y = 0; y < areaHeight; y++) {
                val distSq = (long) (x + areaX) * (x + areaX) + (long) (y + areaY) * (y + areaY);
                val index = x + (y * areaWidth);

                if (distSq > scaledRadius * scaledRadius) {
                    ints[index] = parentInts[index];
                } else {
                    val biome = biomeProvider.apply(distSq, unitScale);
                    // TODO: Make fallback biome configurable
                    ints[index] = Biome.getIdForBiome(biome == null ? Biomes.FOREST : biome);
                }
            }
        }

        return ints;
    }
}
