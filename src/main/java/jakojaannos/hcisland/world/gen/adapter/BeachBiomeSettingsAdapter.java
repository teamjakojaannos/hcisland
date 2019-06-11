package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class BeachBiomeSettingsAdapter extends IslandBiomeSettingsAdapter {
    public BeachBiomeSettingsAdapter(ResourceLocation biomeId, Supplier<? extends BiomeSettings.Beach> defaultSettingsSupplier) {
        super(biomeId, defaultSettingsSupplier);
    }
}
