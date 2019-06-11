package jakojaannos.hcisland.world.gen.adapter;

import jakojaannos.hcisland.client.gui.adapter.IBiomeSettingsGuiProvider;
import jakojaannos.hcisland.client.gui.adapter.IslandBiomeSettingsGuiProvider;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

public class IslandBiomeSettingsAdapter extends AdvancedBiomeSettingsAdapter {
    public <TSettings extends BiomeSettings.Island> IslandBiomeSettingsAdapter(
            ResourceLocation biomeId,
            Supplier<TSettings> defaultSettingsSupplier
    ) {
        super(biomeId, defaultSettingsSupplier);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBiomeSettingsGuiProvider createGuiProvider() {
        return new IslandBiomeSettingsGuiProvider();
    }
}
