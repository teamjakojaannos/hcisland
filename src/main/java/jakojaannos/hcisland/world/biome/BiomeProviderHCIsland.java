package jakojaannos.hcisland.world.biome;

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
                                                    original[0],
                                                    settings.getTotalRadialZoneRadius(),
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

        return chain;
    }
}
