package jakojaannos.hcisland.client.gui;

import jakojaannos.hcisland.init.ModRegistries;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import jakojaannos.hcisland.world.gen.BiomeSettingsAdapter;
import lombok.val;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class GuiCustomizeHCWorldBiome extends GuiCustomizeWithDefaults<BiomeSettings.Factory> {
    private final GuiCustomizeHCWorld parent;
    private final BiomeSettingsAdapter adapter;

    public GuiCustomizeHCWorldBiome(
            GuiCustomizeHCWorld parent,
            BiomeSettingsAdapter adapter,
            @Nullable BiomeSettings.Factory preset
    ) {
        this.parent = parent;
        this.adapter = adapter;

        this.defaultSettings = adapter.createDefaultSettingsFactory();

        if (preset == null) {
            this.settings = adapter.createDefaultSettingsFactory();
        } else {
            this.settings = preset;
        }
    }

    @Override
    public void initGui() {
        title = I18n.format("createWorld.customize.hcisland.biome.title");
        subtitle = adapter.getBiome().getBiomeName();

        super.initGui();
    }

    @Override
    protected void onDonePressed() {
        //parent.settings.updateBiomeSettingsFor(adapter.getBiome().getRegistryName(), settings);
        mc.displayGuiScreen(parent);
    }

    @Override
    protected void restoreDefaults() {
        settings = adapter.createDefaultSettingsFactory();
    }
}
