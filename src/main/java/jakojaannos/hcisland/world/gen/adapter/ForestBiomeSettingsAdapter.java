package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.world.biome.BiomeHCForest;
import jakojaannos.hcisland.world.biome.BiomeHCIslandBase;
import jakojaannos.hcisland.world.gen.LayeredBiomeSettings;
import lombok.val;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class ForestBiomeSettingsAdapter<TSettings extends LayeredBiomeSettings.Forest> extends IslandBiomeSettingsAdapter<TSettings> {
    public ForestBiomeSettingsAdapter(
            ResourceLocation biomeId,
            Supplier<TSettings> defaultSettingsSupplier
    ) {
        super(biomeId, defaultSettingsSupplier);
    }

    @Override
    protected void applyBiomeSettings(TSettings settings) {
        super.applyBiomeSettings(settings);

        val biome = getBiome();
        if (!(biome instanceof BiomeHCForest)) {
            throw new IllegalStateException("Settings adapter registered for wrong type of biome");
        }

        val biomeForest = (BiomeHCIslandBase) biome;
        biomeForest.decorator.treesPerChunk = settings.treesPerChunk;
        biomeForest.decorator.grassPerChunk = settings.grassPerChunk;
        biomeForest.decorator.flowersPerChunk = settings.flowersPerChunk;
    }
}
