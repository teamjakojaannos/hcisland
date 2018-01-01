package jakojaannos.hcisland.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import jakojaannos.hcisland.init.HCIslandBiomes;
import jakojaannos.hcisland.world.gen.layer.GenLayerHCIslandBiomes;
import jakojaannos.hcisland.world.gen.layer.GenLayerHCIslandMix;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerFuzzyZoom;
import net.minecraft.world.gen.layer.GenLayerSmooth;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class WorldTypeHCIsland extends WorldType {
    private static final String NAME = "hcisland";

    public WorldTypeHCIsland() {
        super(NAME);
        this.enableInfoNotice();
    }

    @Override
    public BiomeProvider getBiomeProvider(World world) {
        return new BiomeProviderHCIsland(world.getWorldInfo());
    }

    private static class BiomeProviderHCIsland extends BiomeProvider {
        BiomeProviderHCIsland(WorldInfo info) {
            super(info);
        }

        @Override
        public List<Biome> getBiomesToSpawnIn() {
            return ImmutableList.of(HCIslandBiomes.ISLAND);
        }

        @Override
        public GenLayer[] getModdedBiomeGenerators(WorldType worldType, long seed, GenLayer[] original) {

            GenLayer hcChain = new GenLayerHCIslandBiomes(1337L, original[0]);
            // TODO: Separate beach generation from main biome layer and add some fuzz before generating beaches

            GenLayer hcMix = new GenLayerHCIslandMix(715517L, original[0], hcChain);
            GenLayer voronoi = new GenLayerVoronoiZoom(10L, hcMix);
            hcMix.initWorldGenSeed(seed);
            voronoi.initWorldGenSeed(seed);

            original[0] = hcMix;
            original[1] = voronoi;

            return super.getModdedBiomeGenerators(worldType, seed, original);
        }
    }
}
