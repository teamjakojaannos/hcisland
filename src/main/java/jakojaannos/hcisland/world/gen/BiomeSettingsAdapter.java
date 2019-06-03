package jakojaannos.hcisland.world.gen;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class BiomeSettingsAdapter implements IForgeRegistryEntry<BiomeSettingsAdapter> {
    private final Supplier<BiomeSettings.Factory> defaultSettingsSupplier;
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

    public BiomeSettingsAdapter(ResourceLocation biomeId, Supplier<BiomeSettings.Factory> defaultSettingsSupplier) {
        this.biomeId = biomeId;
        this.defaultSettingsSupplier = defaultSettingsSupplier;
    }

    public BiomeSettings.Factory createDefaultSettingsFactory() {
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
}
