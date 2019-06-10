package jakojaannos.hcisland.world.gen.layer;

import jakojaannos.hcisland.world.biome.BiomeLayeredBase;
import lombok.val;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import javax.annotation.Nullable;

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

                val idLeft = parentValues[parentX - 1 + parentY * parentSampleWidth];
                val idRight = parentValues[parentX + 1 + parentY * parentSampleWidth];
                val idUp = parentValues[parentX + (parentY + 1) * parentSampleWidth];
                val idDown = parentValues[parentX + (parentY - 1) * parentSampleWidth];
                int[] neighbors = {idLeft, idRight, idUp, idDown};

                // Only cases for oceanic biomes are two-way beaches where two non-compatible oceanic biomes meet.
                // Two-way case is required in order to avoid odd clipping with liquid blocks in places where sea levels
                // of the two oceanic biomes do not match. We DO NEED to handle non-layered biomes too, as vanilla shore
                // generation does not recognize non-compatible oceanic biomes by itself
                int newBiome;
                if (isOceanic(parentBiomeId, parentBiome)) {
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

    private boolean isOceanic(int biomeId, @Nullable Biome biome) {
        return GenLayer.isBiomeOceanic(biomeId)
                || (biome instanceof BiomeLayeredBase && ((BiomeLayeredBase) biome).isOceanic());
    }

    private int handleOceanic(Biome biome, int[] neighbors) {
        val ownSeaLevel = getSeaLevelFor(biome);
        for (val neighborId : neighbors) {
            val neighbor = Biome.getBiome(neighborId);
            if (neighbor == null || !isOceanic(neighborId, neighbor)) {
                continue;
            }

            val neighborSeaLevel = getSeaLevelFor(neighbor);
            boolean selfIsCompatible = isCompatibleWithOceanic(biome, neighbor);
            boolean neighborIsCompatible = isCompatibleWithOceanic(neighbor, biome);

            if (!selfIsCompatible || !neighborIsCompatible || ownSeaLevel != neighborSeaLevel) {
                val beachBiome = getBeachBiomeFor(biome);

                if (beachBiome == null) {
                    throw new IllegalStateException("Could not determine beach for biome: \"" + biome.getRegistryName() + "\"");
                }

                return Biome.getIdForBiome(biome);
            }
        }

        return -1;
    }

    private Biome getBeachBiomeFor(Biome biome) {
        return biome instanceof BiomeLayeredBase
                ? ((BiomeLayeredBase) biome).getBeachBiome()
                : Biomes.BEACH; // FIXME: Variants!
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

    private int handleNonOceanic(Biome biome, int[] neighbors) {
        if (biome instanceof BiomeLayeredBase) {
            val layeredBiome = (BiomeLayeredBase) biome;

            for (val neighborId : neighbors) {
                val neighbor = Biome.getBiome(neighborId);
                if (neighbor == null) {
                    continue;
                }

                // 1. one-way beach
                if (isOceanic(neighborId, neighbor)) {
                    val beach = getBeachBiomeFor(neighbor);
                    if (beach == null) {
                        throw new IllegalStateException("Could not determine beach for biome: \"" + neighbor.getRegistryName() + "\"");
                    }

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
