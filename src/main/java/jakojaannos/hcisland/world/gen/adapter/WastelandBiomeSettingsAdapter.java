package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.world.biome.BiomeHCWastelandBase;
import jakojaannos.hcisland.world.gen.LayeredBiomeSettings;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

@Log4j2
public class WastelandBiomeSettingsAdapter<TSettings extends LayeredBiomeSettings.Wasteland> extends IslandBiomeSettingsAdapter<TSettings> {
    public WastelandBiomeSettingsAdapter(
            ResourceLocation biomeId,
            Supplier<TSettings> defaultSettingsSupplier
    ) {
        super(biomeId, defaultSettingsSupplier);
    }

    @Override
    protected void applyTypedBiomeSettings(TSettings settings) {
        super.applyTypedBiomeSettings(settings);

        val biome = getBiome();
        if (!(biome instanceof BiomeHCWastelandBase)) {
            throw log.throwing(new IllegalStateException("Settings adapter registered for wrong type of biome"));
        }

        log.debug("Applying biome settings (Wasteland)...");
        val biomeWasteland = (BiomeHCWastelandBase) biome;
        biomeWasteland.generateFire = settings.generateFire;
    }
}
