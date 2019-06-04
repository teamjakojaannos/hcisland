package jakojaannos.hcisland.client.gui;

import jakojaannos.hcisland.client.gui.adapter.IBiomeSettingsGuiProvider;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import jakojaannos.hcisland.world.gen.BiomeSettingsAdapter;
import lombok.val;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class GuiCustomizeHCWorldBiome extends GuiCustomizeWithDefaults<BiomeSettings.Factory> implements GuiPageButtonList.GuiResponder {
    private final GuiCustomizeHCWorld parent;
    private final BiomeSettingsAdapter adapter;
    private final IBiomeSettingsGuiProvider guiProvider;

    public GuiCustomizeHCWorldBiome(
            GuiCustomizeHCWorld parent,
            BiomeSettingsAdapter adapter,
            @Nullable BiomeSettings.Factory preset
    ) {
        this.parent = parent;
        this.adapter = adapter;

        this.defaultSettings = adapter.createDefaultSettingsFactory();
        this.guiProvider = adapter.createGuiProvider();

        if (preset == null) {
            this.settings = adapter.createDefaultSettingsFactory();
        } else {
            this.settings = preset;
        }
    }

    @Override
    public void initGui() {
        title = I18n.format("createWorld.customize.hcisland.biome.title");
        super.initGui();
    }

    @Override
    protected void createButtons() {

    }

    @Override
    protected String[] updatePageNames() {
        return new String[]{adapter.getBiome().getBiomeName()};
    }

    @Override
    protected String getFormattedValue(int id, float value) {
        return String.valueOf(value);
    }

    @Override
    protected GuiPageButtonList.GuiListEntry[][] getPages() {
        val result = new GuiPageButtonList.GuiListEntry[][]{
                guiProvider.createPage(idCounter, settings)
        };

        idCounter += result.length;
        return result;
    }

    @Override
    public void setEntryValue(int id, boolean value) {
        guiProvider.setEntryValue(id, value, settings);
    }

    @Override
    public void setEntryValue(int id, float value) {
        guiProvider.setEntryValue(id, value, settings);
    }

    @Override
    public void setEntryValue(int id, String value) {
        guiProvider.setEntryValue(id, value, settings);
    }

    @Override
    protected void onDonePressed() {
        parent.settings.updateBiomeSettingsFor(adapter.getBiome().getRegistryName(), settings);
        mc.displayGuiScreen(parent);
    }

    @Override
    protected void restoreDefaults() {
        settings = adapter.createDefaultSettingsFactory();
    }
}
