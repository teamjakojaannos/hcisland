package jakojaannos.hcisland.world.gen;

import jakojaannos.hcisland.world.biome.BiomeHCBase;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class AdvancedBiomeSettingsAdapter extends BiomeSettingsAdapter {
    public AdvancedBiomeSettingsAdapter(ResourceLocation biomeId, Supplier<BiomeSettings.Factory> defaultSettingsSupplier) {
        super(biomeId, defaultSettingsSupplier);
    }

    @Override
    public void applyBiomeSettings(BiomeSettings settings) {
        ((BiomeHCBase) getBiome()).applySettings(settings);
    }
}
