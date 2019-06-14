package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.world.biome.BiomeHCBase;
import jakojaannos.hcisland.world.gen.LayeredBiomeSettings;
import lombok.val;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class BeachBiomeSettingsAdapter<TSettings extends LayeredBiomeSettings.Beach> extends IslandBiomeSettingsAdapter<TSettings> {
    public BeachBiomeSettingsAdapter(
            ResourceLocation biomeId,
            Supplier<TSettings> defaultSettingsSupplier
    ) {
        super(biomeId, defaultSettingsSupplier);
    }

    @Override
    protected void applyBiomeSettings(TSettings settings) {
        super.applyBiomeSettings(settings);

        val biome = getBiome();
        if (!(biome instanceof BiomeHCBase)) {
            throw new IllegalStateException("Settings adapter registered for wrong type of biome");
        }

        val biomeBeach = (BiomeHCBase) biome;
        biomeBeach.decorator.cactiPerChunk = settings.cactiPerChunk;
    }
}
