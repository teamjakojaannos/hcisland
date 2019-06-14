package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.client.gui.adapter.IBiomeSettingsGuiProvider;
import jakojaannos.hcisland.client.gui.adapter.IslandBiomeSettingsGuiProvider;
import jakojaannos.hcisland.world.biome.BiomeHCIslandBase;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import jakojaannos.hcisland.world.gen.LayeredBiomeSettings;
import lombok.val;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

public class IslandBiomeSettingsAdapter<TSettings extends LayeredBiomeSettings.Island> extends LayeredBiomeSettingsAdapter<TSettings> {
    public IslandBiomeSettingsAdapter(
            ResourceLocation biomeId,
            Supplier<TSettings> defaultSettingsSupplier
    ) {
        super(biomeId, defaultSettingsSupplier);
    }

    @Override
    protected void applyBiomeSettings(TSettings settings) {
        super.applyBiomeSettings(settings);

        val biome = getBiome();
        if (!(biome instanceof BiomeHCIslandBase)) {
            throw new IllegalStateException("Settings adapter registered for wrong type of biome");
        }

        val biomeIsland = (BiomeHCIslandBase)biome;
        biomeIsland.decorator.generateFalls = settings.generateFalls;
        biomeIsland.generateLakes = settings.generateLakes;
        biomeIsland.generateLakesLava = settings.generateLakesLava;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBiomeSettingsGuiProvider createGuiProvider() {
        return new IslandBiomeSettingsGuiProvider();
    }
}
