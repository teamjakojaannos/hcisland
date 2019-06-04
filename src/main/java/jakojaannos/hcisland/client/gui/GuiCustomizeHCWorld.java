package jakojaannos.hcisland.client.gui;

import jakojaannos.hcisland.init.ModRegistries;
import jakojaannos.hcisland.world.gen.BiomeSettingsAdapter;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import lombok.val;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

// TODO: Pages:
//        1. general settings: lava ocean block override, sea level override, bedrock level etc.
//        2. radial biomes
//        3. biome settings page (see below)

// TODO: Add single page with buttons for each customizable biome
//       - Iterate biome adapter registry and create page for each
//       - Adapter needs to be able to provide GUI elements on client-side
//       - Chunk generator settings needs to accommodate to possible changes by:
//          1. WorldTypeHCIsland#getBiomeProvider needs to apply changes to all biomes in the world using adapters
//          2. settings themselves need to be updated according to adapters (need entries for all biomes)
//          3. biomes themselves need some way of identifying their own entry from settings (probably hashmap by biome registry name)

@SideOnly(Side.CLIENT)
public class GuiCustomizeHCWorld extends GuiPagedCustomizeWithDefaults<HCIslandChunkGeneratorSettings.Factory> implements GuiSlider.FormatHelper, GuiPageButtonList.GuiResponder {
    private final GuiCreateWorld parent;

    private GuiButton clipboard;

    public GuiCustomizeHCWorld(GuiCreateWorld parent, @Nullable String preset) {
        this.parent = parent;

        this.title = "Customize World Settings";
        this.subtitle = "Page 1 of many";

        this.defaultSettings = new HCIslandChunkGeneratorSettings.Factory(true);

        if (preset != null && !preset.isEmpty()) {
            settings = HCIslandChunkGeneratorSettings.Factory.jsonToFactory(preset);
        } else {
            settings = new HCIslandChunkGeneratorSettings.Factory(true);
        }
    }

    @Override
    public void initGui() {
        title = I18n.format("createWorld.customize.hcisland.title");
        super.initGui();
    }

    @Override
    protected void createButtons() {
        super.createButtons();
        clipboard = addButton(new GuiButton(idCounter++, width / 2 - 92, height - 27, 185, 20, I18n.format("createWorld.customize.hcisland.clipboard")));
    }

    private String saveValues() {
        return settings.toString().replace("\n", "");
    }

    @Override
    protected GuiPageButtonList.GuiListEntry[][] getPages() {
        return new GuiPageButtonList.GuiListEntry[][]{
                createGeneralPage(),
                createRadialPage(),
                createBiomePage()
        };
    }

    @Override
    protected String[] updatePageNames() {
        val pageNames = new String[3];
        for (int i = 0; i < pageNames.length; i++) {
            pageNames[i] = I18n.format("createWorld.customize.hcisland.page" + i);
        }

        return pageNames;
    }

    private GuiPageButtonList.GuiListEntry[] createGeneralPage() {
        return new GuiPageButtonList.GuiListEntry[]{
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    "Nothing at the moment :c",
                                                    true),
                null
        };
    }

    private GuiPageButtonList.GuiListEntry[] createRadialPage() {
        return new GuiPageButtonList.GuiListEntry[]{
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    "Edit biomes",
                                                    true),
                null,
                new ExtendedGuiPageButtonList.GuiActionButtonEntry(idCounter++,
                                                                   "Open Editor",
                                                                   true,
                                                                   () -> mc.displayGuiScreen(new GuiCustomizeRadialBiomes(this))),
                null
        };
    }

    private GuiPageButtonList.GuiListEntry[] createBiomePage() {
        return ForgeRegistries.BIOMES.getValuesCollection()
                                     .stream()
                                     .map(b -> ModRegistries.BIOME_ADAPTERS.getValue(b.getRegistryName()))
                                     .filter(Objects::nonNull)
                                     .flatMap(this::createBiomeEntries)
                                     .toArray(GuiPageButtonList.GuiListEntry[]::new);
    }

    private Stream<GuiPageButtonList.GuiListEntry> createBiomeEntries(BiomeSettingsAdapter adapter) {
        return Stream.of(
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    adapter.getBiome().getBiomeName(), false),
                new ExtendedGuiPageButtonList.GuiActionButtonEntry(idCounter++,
                                                                   "createWorld.customize.hcisland.field.biome.edit",
                                                                   false,
                                                                   () -> openBiomeEditor(adapter))
        );
    }

    private void openBiomeEditor(BiomeSettingsAdapter adapter) {
        mc.displayGuiScreen(new GuiCustomizeHCWorldBiome(this,
                                                         adapter,
                                                         settings.getSettingsFor(adapter.getBiome().getRegistryName()),
                                                         () -> {
                                                             // FIXME: This is not really a sensible way of doing this
                                                             val backup = settings;
                                                             restoreDefaults();
                                                             val result = settings.getSettingsFor(adapter.getBiome().getRegistryName());
                                                             settings = backup;
                                                             return result;
                                                         }));
    }

    @Override
    public void setEntryValue(int id, String valueStr) {
        setSettingsModified(!settings.equals(defaultSettings));
    }

    @Override
    public void setEntryValue(int id, float value) {
        setSettingsModified(!settings.equals(defaultSettings));
    }

    @Override
    public void setEntryValue(int id, boolean value) {
        setSettingsModified(!settings.equals(defaultSettings));
    }


    @Override
    public String getText(int id, String name, float value) {
        return getFormattedValue(id, value);
    }

    @Override
    protected String getFormattedValue(int id, float value) {
        // TODO: Select by id
        return String.format("%d", (int) value);
    }


    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (!button.enabled) {
            return;
        }

        if (button.id == clipboard.id) {
            setClipboardString(saveValues());
        }
    }

    @Override
    protected void onDonePressed() {
        parent.chunkProviderSettingsJson = saveValues();
        mc.displayGuiScreen(parent);
    }

    @Override
    protected void restoreDefaults() {
        settings.setDefaults();
        super.restoreDefaults();
    }

    @Override
    protected void setConfirmationControls(boolean visible) {
        super.setConfirmationControls(visible);

        clipboard.enabled = !visible;
    }
}
