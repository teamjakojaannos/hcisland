package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.client.gui.adapter.IBiomeSettingsGuiProvider;
import jakojaannos.hcisland.client.gui.adapter.LayeredBiomeSettingsGuiProvider;
import jakojaannos.hcisland.world.biome.BiomeLayeredBase;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import jakojaannos.hcisland.world.gen.LayeredBiomeSettings;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

@Log4j2
public class LayeredBiomeSettingsAdapter<TSettings extends LayeredBiomeSettings> extends BiomeSettingsAdapter {
    public LayeredBiomeSettingsAdapter(
            ResourceLocation biomeId,
            Supplier<TSettings> defaultSettingsSupplier
    ) {
        super(biomeId, defaultSettingsSupplier);
    }

    @Override
    public final void applyBiomeSettings(BiomeSettings settings) {
        super.applyBiomeSettings(settings);
        applyTypedBiomeSettings((TSettings) settings);
    }

    protected void applyTypedBiomeSettings(TSettings settings) {
        val biome = getBiome();
        if (!(biome instanceof BiomeLayeredBase)) {
            throw log.throwing(new IllegalStateException("Settings adapter registered for wrong type of biome"));
        }

        log.debug("Applying biome settings (Layered)...");
        val biomeLayered = (BiomeLayeredBase) biome;
        biomeLayered.setSeaLevelOverride(settings.seaLevel);
        biomeLayered.setStoneBlock(settings.stoneBlock);
        biomeLayered.setOceanBlock(settings.oceanBlock);
        biomeLayered.setLayers(settings.layers, settings.layersUnderwater);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBiomeSettingsGuiProvider createGuiProvider() {
        return new LayeredBiomeSettingsGuiProvider();
    }
}
