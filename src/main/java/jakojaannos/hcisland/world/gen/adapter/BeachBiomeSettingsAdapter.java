package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.world.biome.BiomeHCBase;
import jakojaannos.hcisland.world.gen.LayeredBiomeSettings;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

@Log4j2
public class BeachBiomeSettingsAdapter<TSettings extends LayeredBiomeSettings.Beach> extends IslandBiomeSettingsAdapter<TSettings> {
    public BeachBiomeSettingsAdapter(
            ResourceLocation biomeId,
            Supplier<TSettings> defaultSettingsSupplier
    ) {
        super(biomeId, defaultSettingsSupplier);
    }

    @Override
    protected void applyTypedBiomeSettings(TSettings settings) {
        super.applyTypedBiomeSettings(settings);

        val biome = getBiome();
        if (!(biome instanceof BiomeHCBase)) {
            throw log.throwing(new IllegalStateException("Settings adapter registered for wrong type of biome"));
        }

        log.debug("Applying biome settings (Beach)...");
        val biomeBeach = (BiomeHCBase) biome;
        biomeBeach.decorator.cactiPerChunk = settings.cactiPerChunk;
    }
}
