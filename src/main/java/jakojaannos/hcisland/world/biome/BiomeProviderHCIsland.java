package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.init.ModBiomes;
import jakojaannos.hcisland.world.WorldTypeHCIsland;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import jakojaannos.hcisland.world.gen.layer.GenLayerBiomeLayeredEdges;
import jakojaannos.hcisland.world.gen.layer.GenLayerHCIslandBiomes;
import lombok.val;
import lombok.var;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.*;
import net.minecraft.world.storage.WorldInfo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BiomeProviderHCIsland extends BiomeProvider {
    private List<Biome> spawnBiomes;

    public BiomeProviderHCIsland(WorldInfo info, HCIslandChunkGeneratorSettings settings) {
        super(info);
        this.spawnBiomes = settings.getSpawnBiomes();
    }

    @Override
    public List<Biome> getBiomesToSpawnIn() {
        return spawnBiomes.stream()
                          .filter(Objects::nonNull)
                          .collect(Collectors.toList());
    }

    @Override
    public GenLayer[] getModdedBiomeGenerators(WorldType worldType, long seed, GenLayer[] original) {
        val islandWorldType = (WorldTypeHCIsland) worldType;
        GenLayer hcChain = createGenLayerChain(islandWorldType, seed, original);

        // Finalize vanilla layers (GenLayer.java, rows 87-89)
        GenLayer voronoi = new GenLayerVoronoiZoom(10L, hcChain);
        hcChain.initWorldGenSeed(seed);
        voronoi.initWorldGenSeed(seed);

        original[0] = hcChain;
        original[1] = voronoi;

        return super.getModdedBiomeGenerators(worldType, seed, original);
    }

    private GenLayer createGenLayerChain(WorldTypeHCIsland worldType, long seed, GenLayer[] original) {
        val settings = worldType.getSettings();

        val islandShapeFuzz = settings.getIslandShapeFuzz();
        val smoothBiomeEdges = settings.isSmoothBiomeEdges();
        val generateEdges = settings.isGenerateEdges();
        val shoreScale = settings.getShoreScale();

        // Without any zooming (fuzz and shoreScale at zero):
        //  - conversion ratio at biome layer is 1:4 (multiplication by 4.0)
        // As zoom increases:
        //  - conversion ratio is divided by 2 for each zoom (zoom magnifies the world by 2x)
        // -> unit scale is 1 / pow(2, 2 - number_of_zooms)
        val numberOfZooms = islandShapeFuzz + (generateEdges ? shoreScale : 0);
        var biomeUnitConversionRatio = 4.0;
        for (var i = 0; i < numberOfZooms; i++) {
            biomeUnitConversionRatio /= 2.0;
        }

        GenLayer chain = new GenLayerHCIslandBiomes(1337L,
                                                    biomeUnitConversionRatio,
                                                    settings::getBiomeAtDistanceSq);
        for (var i = 0; i < islandShapeFuzz; i++) {
            chain = new GenLayerFuzzyZoom(2000L + i, chain);
        }
        if (smoothBiomeEdges) {
            chain = new GenLayerSmooth(1000L, chain);
        }

        if (generateEdges) {
            chain = new GenLayerShore(1000L, chain);
            chain = new GenLayerBiomeLayeredEdges(1337L, chain, settings);
            for (var i = 0; i < shoreScale; i++) {
                chain = new GenLayerZoom(715517L + i, chain);
            }
            chain = new GenLayerBiomeLayeredEdges(1337L, chain, settings);

            if (smoothBiomeEdges) {
                chain = new GenLayerSmooth(1000L, chain);
            }
        }

        return new MixGenLayer(original[0],
                               chain,
                               seed,
                               biomeUnitConversionRatio,
                               settings.getTotalRadialZoneRadius());
    }

    private static class MixGenLayer extends GenLayer {
        private final GenLayer original;
        private final GenLayer custom;
        private final int totalRadius;

        public MixGenLayer(GenLayer original, GenLayer custom, long seed, double unitScale, int totalRadius) {
            super(seed);
            this.original = original;
            this.custom = custom;
            this.totalRadius = totalRadius;
        }

        @Override
        public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
            int[] originalInts = null;
            int[] customInts = null;
            val maskId = Biome.getIdForBiome(ModBiomes.__MASK);

            // HACK: due to zooms, units here are roughly 4 times smaller than chunks. This means that we need
            //       to multiply by 4 to get roughly the actual radius. However, high fuzz levels may overshoot
            //       estimated radius quite a lot, so add extra to the multiplier to accommodate that.
            val scaledRadius = totalRadius * (4 + 2);
            var ints = IntCache.getIntCache(areaWidth * areaHeight);
            for (var x = 0; x < areaWidth; x++) {
                for (var y = 0; y < areaHeight; y++) {
                    val distSq = (long) (x + areaX) * (x + areaX) + (long) (y + areaY) * (y + areaY);
                    val index = x + (y * areaWidth);

                    if (distSq > scaledRadius * scaledRadius) {
                        ints[index] = originalInts == null
                                ? (originalInts = original.getInts(areaX, areaY, areaWidth, areaHeight))[index]
                                : originalInts[index];
                    } else {
                        var value = customInts == null
                                ? (customInts = custom.getInts(areaX, areaY, areaWidth, areaHeight))[index]
                                : customInts[index];

                        if (value == maskId) {
                            value = originalInts == null
                                    ? (originalInts = original.getInts(areaX, areaY, areaWidth, areaHeight))[index]
                                    : originalInts[index];
                        }

                        ints[index] = value;
                    }
                }
            }

            return ints;
        }
    }
}
