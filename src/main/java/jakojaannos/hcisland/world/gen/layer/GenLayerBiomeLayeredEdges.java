package jakojaannos.hcisland.world.gen.layer;

import jakojaannos.hcisland.world.biome.BiomeLayeredBase;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.var;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GenLayerBiomeLayeredEdges extends GenLayer {
    private static final int SAMPLE_EXTENT = 1;

    private final GenLayer parent;

    public GenLayerBiomeLayeredEdges(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        val parentSampleStartX = areaX - SAMPLE_EXTENT;
        val parentSampleStartY = areaY - SAMPLE_EXTENT;
        val parentSampleWidth = areaWidth + 2 * SAMPLE_EXTENT;
        val parentSampleHeight = areaHeight + 2 * SAMPLE_EXTENT;

        val parentValues = this.parent.getInts(parentSampleStartX, parentSampleStartY, parentSampleWidth, parentSampleHeight);
        val values = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                this.initChunkSeed(x + areaX, y + areaY);
                val parentX = x + SAMPLE_EXTENT;
                val parentY = y + SAMPLE_EXTENT;
                val parentBiomeId = parentValues[parentX + parentY * parentSampleWidth];
                val parentBiome = Biome.getBiome(parentBiomeId);
                if (parentBiome == null) {
                    continue;
                }

                val sb = Stream.<Integer>builder();
                for (var dy = -SAMPLE_EXTENT; dy <= SAMPLE_EXTENT; dy++) {
                    for (var dx = -SAMPLE_EXTENT; dx <= SAMPLE_EXTENT; dx++) {
                        if (dx == 0 && dy == 0) {
                            continue;
                        }
                        sb.add(parentValues[parentX + dx + (parentY + dy) * parentSampleWidth]);
                    }
                }
                val neighbors = sb.build();

                // Only cases for oceanic biomes are two-way beaches where two non-compatible oceanic biomes meet.
                // Two-way case is required in order to avoid odd clipping with liquid blocks in places where sea levels
                // of the two oceanic biomes do not match. We DO NEED to handle non-layered biomes too, as vanilla shore
                // generation does not recognize non-compatible oceanic biomes by itself
                int newBiome;
                if (isVanillaOceanic(parentBiome) || isLayeredOceanic(parentBiome)) {
                    newBiome = handleOceanic(parentBiome, neighbors);
                }
                // non-oceanic biomes need to be handled only if they are layered. There are two possible cases:
                //  1. one-way beaches
                //  2. neighboring non-compatible non-oceanic biome
                else {
                    newBiome = handleNonOceanic(parentBiome, neighbors);
                }

                values[x + y * areaWidth] = newBiome != -1
                        ? newBiome
                        : parentBiomeId;
            }
        }

        return values;
    }

    private int handleOceanic(Biome biome, Stream<Integer> neighbors) {
        return neighbors.map(Biome::getBiome)
                        .filter(Objects::nonNull)
                        .filter(neighbor -> isLayeredOceanic(neighbor) || isVanillaOceanic(neighbor))
                        .filter(neighbor -> !isCompatibleWithOceanic(biome, neighbor) || !isCompatibleWithOceanic(neighbor, biome))
                        .filter(neighbor -> getSeaLevelFor(biome) != getSeaLevelFor(neighbor))
                        .findFirst()
                        .map(this::getBeachBiomeFor)
                        .map(Biome::getIdForBiome)
                        .orElse(-1);
    }

    private boolean isVanillaOceanic(Biome biome) {
        return isBiomeOceanic(Biome.getIdForBiome(biome));
    }

    private boolean isLayeredOceanic(Biome biome) {
        return biome instanceof BiomeLayeredBase && ((BiomeLayeredBase) biome).isOceanic();
    }

    private Biome getBeachBiomeFor(Biome biome) {
        val result = biome instanceof BiomeLayeredBase
                ? ((BiomeLayeredBase) biome).getBeachBiome()
                : Biomes.BEACH; // FIXME: Variants!

        if (result == null) {
            throw new IllegalStateException("Could not determine beach for biome: \"" + biome.getRegistryName() + "\"");
        }
        return result;
    }

    private boolean isCompatibleWithOceanic(Biome biome, Biome other) {
        return !(biome instanceof BiomeLayeredBase) || ((BiomeLayeredBase) biome).isCompatibleWithOceanic(other);
    }

    private int getSeaLevelFor(Biome biome) {
        // FIXME: Get from generator settings
        val worldDefaultSeaLevel = 64;

        if (biome instanceof BiomeLayeredBase) {
            val override = ((BiomeLayeredBase) biome).getSeaLevelOverride();
            return override >= 0 ? override : worldDefaultSeaLevel;
        } else {
            return worldDefaultSeaLevel;
        }
    }

    private int handleNonOceanic(Biome biome, Stream<Integer> neighbors) {
        if (biome instanceof BiomeLayeredBase) {
            val layeredBiome = (BiomeLayeredBase) biome;
            val iterator = neighbors.iterator();
            while (iterator.hasNext()) {
                val neighbor = Biome.getBiome(iterator.next());
                if (neighbor == null) {
                    continue;
                }

                // 1. one-way beach
                if (isVanillaOceanic(neighbor) || isLayeredOceanic(neighbor)) {
                    val beach = getBeachBiomeFor(neighbor);
                    return Biome.getIdForBiome(beach);
                }
                // 2. self is layered and other is non-compatible non-oceanic biome
                else {
                    val edgeBiome = layeredBiome.getEdgeBiome();
                    if (edgeBiome != null && !layeredBiome.isCompatibleWith(neighbor)) {
                        return Biome.getIdForBiome(edgeBiome);
                    }
                }
            }
        }

        return -1;
    }
}
