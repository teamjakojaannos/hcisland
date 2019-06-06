package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class IslandBiomeSettingsAdapter extends AdvancedBiomeSettingsAdapter {
    public IslandBiomeSettingsAdapter(ResourceLocation biomeId, Supplier<? extends BiomeSettings.Island.Factory> defaultSettingsSupplier) {
        super(biomeId, defaultSettingsSupplier);
    }
}
