package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class ForestBiomeSettingsAdapter extends IslandBiomeSettingsAdapter {
    public ForestBiomeSettingsAdapter(ResourceLocation biomeId, Supplier<? extends BiomeSettings.Forest> defaultSettingsSupplier) {
        super(biomeId, defaultSettingsSupplier);
    }
}
