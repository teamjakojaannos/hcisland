package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.client.gui.adapter.IBiomeSettingsGuiProvider;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class BiomeSettingsAdapter implements IForgeRegistryEntry<BiomeSettingsAdapter> {
    private final Supplier<? extends BiomeSettings> defaultSettingsSupplier;
    private final ResourceLocation biomeId;

    @Nullable private Biome cachedBiome;

    public Biome getBiome() {
        if (cachedBiome == null) {
            cachedBiome = ForgeRegistries.BIOMES.getValue(biomeId);

            if (cachedBiome == null) {
                throw new IllegalStateException(String.format("Biome with ID \"%s\" could not be found.", biomeId));
            }
        }

        return cachedBiome;
    }

    public BiomeSettingsAdapter(ResourceLocation biomeId, Supplier<? extends BiomeSettings> defaultSettingsSupplier) {
        this.biomeId = biomeId;
        this.defaultSettingsSupplier = defaultSettingsSupplier;
    }

    public BiomeSettings createDefaultSettingsFactory() {
        return defaultSettingsSupplier.get();
    }

    @Override
    public BiomeSettingsAdapter setRegistryName(ResourceLocation name) {
        throw new IllegalStateException("BiomeSettingsAdapters inherit registry name from their backing biome");
    }

    @Override
    public ResourceLocation getRegistryName() {
        return biomeId;
    }

    @Override
    public Class<BiomeSettingsAdapter> getRegistryType() {
        return BiomeSettingsAdapter.class;
    }

    public abstract void applyBiomeSettings(BiomeSettings settings);

    @SideOnly(Side.CLIENT)
    public abstract IBiomeSettingsGuiProvider createGuiProvider();
}
