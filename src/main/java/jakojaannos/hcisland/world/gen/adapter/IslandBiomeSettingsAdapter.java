package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.client.gui.adapter.IBiomeSettingsGuiProvider;
import jakojaannos.hcisland.client.gui.adapter.IslandBiomeSettingsGuiProvider;
import jakojaannos.hcisland.world.biome.BiomeHCIslandBase;
import jakojaannos.hcisland.world.gen.LayeredBiomeSettings;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

@Log4j2
public class IslandBiomeSettingsAdapter<TSettings extends LayeredBiomeSettings.Island> extends LayeredBiomeSettingsAdapter<TSettings> {
    public IslandBiomeSettingsAdapter(
            ResourceLocation biomeId,
            Supplier<TSettings> defaultSettingsSupplier
    ) {
        super(biomeId, defaultSettingsSupplier);
    }

    @Override
    protected void applyTypedBiomeSettings(TSettings settings) {
        super.applyTypedBiomeSettings(settings);

        val biome = getBiome();
        if (!(biome instanceof BiomeHCIslandBase)) {
            throw log.throwing(new IllegalStateException("Settings adapter registered for wrong type of biome"));
        }

        log.debug("Applying biome settings (Island)...");
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
