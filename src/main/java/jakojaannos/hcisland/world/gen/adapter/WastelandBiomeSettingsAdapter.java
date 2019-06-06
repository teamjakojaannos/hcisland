package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class WastelandBiomeSettingsAdapter extends IslandBiomeSettingsAdapter {
    public WastelandBiomeSettingsAdapter(ResourceLocation biomeId, Supplier<? extends BiomeSettings.Wasteland.Factory> defaultSettingsSupplier) {
        super(biomeId, defaultSettingsSupplier);
    }
}
