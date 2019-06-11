package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.client.gui.adapter.AdvancedBiomeSettingsGuiProvider;
import jakojaannos.hcisland.client.gui.adapter.IBiomeSettingsGuiProvider;
import jakojaannos.hcisland.world.biome.BiomeHCBase;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

public class AdvancedBiomeSettingsAdapter extends BiomeSettingsAdapter {
    public <TSettings extends BiomeSettings> AdvancedBiomeSettingsAdapter(
            ResourceLocation biomeId,
            Supplier<TSettings> defaultSettingsSupplier) {
        super(biomeId, defaultSettingsSupplier);
    }

    @Override
    public void applyBiomeSettings(BiomeSettings settings) {
        ((BiomeHCBase) getBiome()).applySettings(settings);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBiomeSettingsGuiProvider createGuiProvider() {
        return new AdvancedBiomeSettingsGuiProvider();
    }
}
