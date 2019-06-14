package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.world.biome.BiomeHCWastelandBase;
import jakojaannos.hcisland.world.biome.BiomeLayeredBase;
import jakojaannos.hcisland.world.gen.LayeredBiomeSettings;
import lombok.val;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class WastelandBiomeSettingsAdapter<TSettings extends LayeredBiomeSettings.Wasteland> extends IslandBiomeSettingsAdapter<TSettings> {
    public WastelandBiomeSettingsAdapter(
            ResourceLocation biomeId,
            Supplier<TSettings> defaultSettingsSupplier
    ) {
        super(biomeId, defaultSettingsSupplier);
    }

    @Override
    protected void applyBiomeSettings(TSettings settings) {
        super.applyBiomeSettings(settings);

        val biome = getBiome();
        if (!(biome instanceof BiomeHCWastelandBase)) {
            throw new IllegalStateException("Settings adapter registered for wrong type of biome");
        }

        val biomeWasteland = (BiomeHCWastelandBase) biome;
        biomeWasteland.generateFire = settings.generateFire;
    }
}
