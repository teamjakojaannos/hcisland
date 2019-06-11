package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class BeachBiomeSettingsAdapter extends IslandBiomeSettingsAdapter {
    public <TSettings extends BiomeSettings.Beach> BeachBiomeSettingsAdapter(
            ResourceLocation biomeId,
            Supplier<TSettings> defaultSettingsSupplier
    ) {
        super(biomeId, defaultSettingsSupplier);
    }
}
