package jakojaannos.hcisland.client.gui;

import jakojaannos.hcisland.client.gui.adapter.IBiomeSettingsGuiProvider;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import jakojaannos.hcisland.world.gen.adapter.BiomeSettingsAdapter;
import lombok.val;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public class GuiCustomizeHCWorldBiome extends GuiPagedCustomizeWithDefaults<BiomeSettings> implements GuiPageButtonList.GuiResponder {
    private final GuiCustomizeHCWorld parent;
    private final BiomeSettingsAdapter adapter;
    private final IBiomeSettingsGuiProvider guiProvider;

    public GuiCustomizeHCWorldBiome(
            GuiCustomizeHCWorld parent,
            BiomeSettingsAdapter adapter,
            BiomeSettings settings,
            Supplier<BiomeSettings> defaultSettingsSupplier,
            Consumer<BiomeSettings> settingsApplier
    ) {
        super(defaultSettingsSupplier, settingsApplier);
        this.parent = parent;
        this.adapter = adapter;
        this.guiProvider = adapter.createGuiProvider();

        this.settings = settings;
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
    protected GuiPageButtonList.GuiListEntry[][] getPages() {
        List<GuiPageButtonList.GuiListEntry> list = guiProvider.createPage(this, idCounter, settings, defaultSettingsSupplier);
        val result = new GuiPageButtonList.GuiListEntry[][]{
                list.toArray(new GuiPageButtonList.GuiListEntry[0])
        };

        idCounter += result.length;
        return result;
    }

    @Override
    public void setEntryValue(int id, boolean value) {
        guiProvider.setEntryValue(this, id, value, settings);
        checkSettingsModified();
    }

    @Override
    public void setEntryValue(int id, float value) {
        guiProvider.setEntryValue(this, id, value, settings);
        checkSettingsModified();
    }

    @Override
    public void setEntryValue(int id, String value) {
        guiProvider.setEntryValue(this, id, value, settings);
        checkSettingsModified();
    }

    @Override
    protected void onDonePressed() {
        super.onDonePressed();
        mc.displayGuiScreen(parent);
    }
}
