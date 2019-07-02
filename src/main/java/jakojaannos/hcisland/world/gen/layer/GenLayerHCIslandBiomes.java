package jakojaannos.hcisland.world.gen.layer;

import jakojaannos.hcisland.init.ModBiomes;
import lombok.val;
import lombok.var;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import java.util.function.BiFunction;

public class GenLayerHCIslandBiomes extends GenLayer {
    private final double unitScale;
    private final BiFunction<Long, Double, Biome> biomeProvider;

    public GenLayerHCIslandBiomes(
            long seed,
            double unitScale,
            BiFunction<Long, Double, Biome> biomeProvider
    ) {
        super(seed);

        this.unitScale = unitScale;
        this.biomeProvider = biomeProvider;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        var ints = IntCache.getIntCache(areaWidth * areaHeight);
        for (var x = 0; x < areaWidth; x++) {
            for (var y = 0; y < areaHeight; y++) {
                val distSq = (long) (x + areaX) * (x + areaX) + (long) (y + areaY) * (y + areaY);
                val index = x + (y * areaWidth);

                val biome = biomeProvider.apply(distSq, unitScale);
                ints[index] = biome == null ? Biome.getIdForBiome(ModBiomes.__MASK) : Biome.getIdForBiome(biome);
            }
        }

        return ints;
    }
}
