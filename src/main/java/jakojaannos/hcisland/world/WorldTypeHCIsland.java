package jakojaannos.hcisland.world;

import com.google.common.collect.ImmutableList;
import jakojaannos.hcisland.client.gui.GuiCustomizeHCWorld;
import jakojaannos.hcisland.init.ModBiomes;
import jakojaannos.hcisland.init.ModRegistries;
import jakojaannos.hcisland.world.biome.BiomeHCBase;
import jakojaannos.hcisland.world.gen.BiomeSettingsAdapter;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import jakojaannos.hcisland.world.gen.layer.GenLayerHCIslandBiomes;
import jakojaannos.hcisland.world.gen.layer.GenLayerHCIslandMix;
import lombok.Getter;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

;

public class WorldTypeHCIsland extends WorldType {
    private static final String NAME = "hcisland";

    @Getter private HCIslandChunkGeneratorSettings settings;

    public WorldTypeHCIsland() {
        super(NAME);
        enableInfoNotice();
    }

    @Override
    public boolean isCustomizable() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onCustomizeButton(net.minecraft.client.Minecraft mc, net.minecraft.client.gui.GuiCreateWorld guiCreateWorld) {
        mc.displayGuiScreen(new GuiCustomizeHCWorld(guiCreateWorld, guiCreateWorld.chunkProviderSettingsJson));
    }

    @Override
    public BiomeProvider getBiomeProvider(World world) {
        this.settings = HCIslandChunkGeneratorSettings.Factory.jsonToFactory(world.getWorldInfo().getGeneratorOptions()).build();

        if (!world.isRemote) {
            ModRegistries.BIOME_ADAPTERS.getValuesCollection()
                                        .forEach(a -> a.applyBiomeSettings(settings.getSettingsFor(a.getBiome().getRegistryName())));
        }

        return new BiomeProviderHCIsland(world.getWorldInfo(), settings);
    }

    private class BiomeProviderHCIsland extends BiomeProvider {
        private final HCIslandChunkGeneratorSettings settings;

        BiomeProviderHCIsland(WorldInfo info, HCIslandChunkGeneratorSettings settings) {
            super(info);
            this.settings = settings;
        }

        @Override
        public List<Biome> getBiomesToSpawnIn() {
            return ImmutableList.copyOf(settings.getSpawnBiomes());
        }

        @Override
        public GenLayer[] getModdedBiomeGenerators(WorldType worldType, long seed, GenLayer[] original) {

            GenLayer hcChain = new GenLayerHCIslandBiomes(1337L, original[0], settings);
            // TODO: Separate beach generation from main biome layer and add some fuzz before generating beaches

            GenLayer hcMix = new GenLayerHCIslandMix(715517L, original[0], hcChain, settings);
            GenLayer voronoi = new GenLayerVoronoiZoom(10L, hcMix);
            hcMix.initWorldGenSeed(seed);
            voronoi.initWorldGenSeed(seed);

            original[0] = hcMix;
            original[1] = voronoi;

            return super.getModdedBiomeGenerators(worldType, seed, original);
        }
    }
}
