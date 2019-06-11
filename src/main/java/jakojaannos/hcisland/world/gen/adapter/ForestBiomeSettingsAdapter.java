package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class ForestBiomeSettingsAdapter extends IslandBiomeSettingsAdapter {
    public <TSettings extends BiomeSettings.Forest> ForestBiomeSettingsAdapter(
            ResourceLocation biomeId,
            Supplier<TSettings> defaultSettingsSupplier
    ) {
        super(biomeId, defaultSettingsSupplier);
    }
}
