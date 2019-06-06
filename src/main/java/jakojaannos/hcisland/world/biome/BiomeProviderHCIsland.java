package jakojaannos.hcisland.world.biome;

import com.google.common.collect.ImmutableList;
import jakojaannos.hcisland.world.WorldTypeHCIsland;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import jakojaannos.hcisland.world.gen.layer.GenLayerHCIslandBiomes;
import jakojaannos.hcisland.world.gen.layer.GenLayerHCIslandMix;
import lombok.val;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.storage.WorldInfo;

import java.util.List;

public class BiomeProviderHCIsland extends BiomeProvider {
    private List<Biome> spawnBiomes;

    public BiomeProviderHCIsland(WorldInfo info, HCIslandChunkGeneratorSettings settings) {
        super(info);
        this.spawnBiomes = settings.getSpawnBiomes();
    }

    @Override
    public List<Biome> getBiomesToSpawnIn() {
        return ImmutableList.copyOf(spawnBiomes);
    }

    @Override
    public GenLayer[] getModdedBiomeGenerators(WorldType worldType, long seed, GenLayer[] original) {
        val generatorSettings = ((WorldTypeHCIsland) worldType).getSettings();

        GenLayer hcChain = new GenLayerHCIslandBiomes(1337L, original[0], generatorSettings);
        // TODO: Separate beach generation from main biome layer and add some fuzz before generating beaches

        GenLayer hcMix = new GenLayerHCIslandMix(715517L, original[0], hcChain, generatorSettings);
        GenLayer voronoi = new GenLayerVoronoiZoom(10L, hcMix);
        hcMix.initWorldGenSeed(seed);
        voronoi.initWorldGenSeed(seed);

        original[0] = hcMix;
        original[1] = voronoi;

        return super.getModdedBiomeGenerators(worldType, seed, original);
    }
}
