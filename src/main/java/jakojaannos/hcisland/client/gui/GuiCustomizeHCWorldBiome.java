package jakojaannos.hcisland.client.gui;

import jakojaannos.hcisland.client.gui.adapter.IBiomeSettingsGuiProvider;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import jakojaannos.hcisland.world.gen.BiomeSettingsAdapter;
import lombok.val;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public class GuiCustomizeHCWorldBiome extends GuiPagedCustomizeWithDefaults<BiomeSettings.Factory> implements GuiPageButtonList.GuiResponder {
    private final GuiCustomizeHCWorld parent;
    private final BiomeSettingsAdapter adapter;
    private final IBiomeSettingsGuiProvider guiProvider;

    private final Supplier<BiomeSettings.Factory> defaultSettingsSupplier;

    public GuiCustomizeHCWorldBiome(
            GuiCustomizeHCWorld parent,
            BiomeSettingsAdapter adapter,
            BiomeSettings.Factory settings,
            Supplier<BiomeSettings.Factory> defaultSettingsSupplier
    ) {
        this.parent = parent;
        this.adapter = adapter;
        this.defaultSettingsSupplier = defaultSettingsSupplier;
        this.guiProvider = adapter.createGuiProvider();

        this.settings = settings;
        this.defaultSettings = defaultSettingsSupplier.get();
    }

    @Override
    public void initGui() {
        title = I18n.format("createWorld.customize.hcisland.biome.title");
        super.initGui();
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
                guiProvider.createPage(this, idCounter, settings, defaultSettings)
        };

        idCounter += result.length;
        return result;
    }

    @Override
    public void setEntryValue(int id, boolean value) {
        guiProvider.setEntryValue(this, id, value, settings, defaultSettings);
        setSettingsModified(!settings.equals(defaultSettings));
    }

    @Override
    public void setEntryValue(int id, float value) {
        guiProvider.setEntryValue(this, id, value, settings, defaultSettings);
        setSettingsModified(!settings.equals(defaultSettings));
    }

    @Override
    public void setEntryValue(int id, String value) {
        guiProvider.setEntryValue(this, id, value, settings, defaultSettings);
        setSettingsModified(!settings.equals(defaultSettings));
    }

    @Override
    protected void onDonePressed() {
        parent.settings.updateBiomeSettingsFor(adapter.getBiome().getRegistryName(), settings);
        mc.displayGuiScreen(parent);
    }

    @Override
    protected void restoreDefaults() {
        settings = defaultSettingsSupplier.get();
        super.restoreDefaults();
    }
}
