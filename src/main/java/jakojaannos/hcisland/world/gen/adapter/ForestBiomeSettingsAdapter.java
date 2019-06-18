package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.world.biome.BiomeHCForest;
import jakojaannos.hcisland.world.biome.BiomeHCIslandBase;
import jakojaannos.hcisland.world.gen.LayeredBiomeSettings;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

@Log4j2
public class ForestBiomeSettingsAdapter<TSettings extends LayeredBiomeSettings.Forest> extends IslandBiomeSettingsAdapter<TSettings> {
    public ForestBiomeSettingsAdapter(
            ResourceLocation biomeId,
            Supplier<TSettings> defaultSettingsSupplier
    ) {
        super(biomeId, defaultSettingsSupplier);
    }

    @Override
    protected void applyTypedBiomeSettings(TSettings settings) {
        super.applyTypedBiomeSettings(settings);

        val biome = getBiome();
        if (!(biome instanceof BiomeHCForest)) {
            throw log.throwing(new IllegalStateException("Settings adapter registered for wrong type of biome"));
        }

        log.debug("Applying biome settings (Forest)...");
        val biomeForest = (BiomeHCIslandBase) biome;
        biomeForest.decorator.treesPerChunk = settings.treesPerChunk;
        biomeForest.decorator.grassPerChunk = settings.grassPerChunk;
        biomeForest.decorator.flowersPerChunk = settings.flowersPerChunk;
    }
}
