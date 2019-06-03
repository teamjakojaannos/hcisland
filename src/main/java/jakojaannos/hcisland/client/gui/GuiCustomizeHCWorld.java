package jakojaannos.hcisland.client.gui;

import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import jakojaannos.hcisland.init.ModRegistries;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import jakojaannos.hcisland.world.gen.BiomeSettingsAdapter;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import lombok.val;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
public class GuiCustomizeHCWorld extends GuiCustomizeWithDefaults<HCIslandChunkGeneratorSettings.Factory> implements GuiSlider.FormatHelper, GuiPageButtonList.GuiResponder {
    private final GuiCreateWorld parent;

    private String pageTitle = "Basic settings";
    private String[] pageNames = new String[2];

    private ExtendedGuiPageButtonList pages;
    private GuiButton previousPage;
    private GuiButton nextPage;
    private GuiButton clipboard;

    private boolean confirmDismissed;


    public GuiCustomizeHCWorld(GuiCreateWorld parent, @Nullable String preset) {
        this.parent = parent;

        this.title = "Customize World Settings";
        this.subtitle = "Page 1 of many";

        HCIslandChunkGeneratorSettings.Factory.refreshOverrides();
        defaultSettings = new HCIslandChunkGeneratorSettings.Factory();

        loadValues(preset);
    }

    @Override
    public void initGui() {
        final int page = pages != null ? pages.getPage() : 0;
        final int scroll = pages != null ? pages.getAmountScrolled() : 0;

        title = I18n.format("createWorld.customize.hcisland.title");
        super.initGui();

        previousPage = addButton(new GuiButton(ID_BUTTON_PREVIOUS, 20, 5, 80, 20, I18n.format("createWorld.customize.custom.prev")));
        nextPage = addButton(new GuiButton(ID_BUTTON_NEXT, width - 100, 5, 80, 20, I18n.format("createWorld.customize.custom.next")));

        clipboard = addButton(new GuiButton(ID_BUTTON_CLIPBOARD, width / 2 - 92, height - 27, 185, 20, I18n.format("createWorld.customize.hcisland.clipboard")));

        createPagedList();
        setSettingsModified(!settings.equals(defaultSettings));

        if (page != 0) {
            pages.setPage(page);
            pages.scrollBy(scroll);
            updatePageControls();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        pages.handleMouseInput();
    }

    private String saveValues() {
        return settings.toString().replace("\n", "");
    }

    private void loadValues(@Nullable String preset) {
        if (preset != null && !preset.isEmpty()) {
            settings = HCIslandChunkGeneratorSettings.Factory.jsonToFactory(preset);
        } else {
            settings = new HCIslandChunkGeneratorSettings.Factory();
        }
    }

    private void createPagedList() {
        pages = new ExtendedGuiPageButtonList(
                mc,
                width,
                height,
                32,
                height - 32,
                25,
                this,
                createGeneralPage(),
                createBiomePage());

        for (int i = 0; i < pageNames.length; i++) {
            pageNames[i] = I18n.format("createWorld.customize.hcisland.page" + i);
        }

        updatePageControls();
    }

    private GuiPageButtonList.GuiListEntry[] createGeneralPage() {
        return new GuiPageButtonList.GuiListEntry[]{
                new GuiPageButtonList.GuiLabelEntry(ID_SEA_LEVEL_OVERRIDE_LABEL,
                                                    I18n.format("createWorld.customize.hcisland.field.seaLevelOverride"),
                                                    true),
                new GuiPageButtonList.GuiSlideEntry(ID_SEA_LEVEL_OVERRIDE,
                                                    I18n.format("createWorld.customize.hcisland.field.seaLevelOverride"),
                                                    true, this,
                                                    1.0f, 64.0f,
                                                    settings.seaLevelOverride),
                new GuiPageButtonList.GuiLabelEntry(ID_OCEAN_BLOCK_OVERRIDE_LABEL,
                                                    I18n.format("createWorld.customize.hcisland.field.oceanBlockOverride"),
                                                    true),
                new GuiPageButtonList.EditBoxEntry(ID_OCEAN_BLOCK_OVERRIDE,
                                                   settings.oceanBlockOverride,
                                                   true, s -> true)
        };
    }

    private int idCounter;

    private GuiPageButtonList.GuiListEntry[] createBiomePage() {
        List<GuiPageButtonList.GuiListEntry> entries = new ArrayList<>();

        idCounter = ID_BASE_BIOME;

        ForgeRegistries.BIOMES.getValuesCollection()
                              .stream()
                              .map(b -> ModRegistries.BIOME_ADAPTERS.getValue(b.getRegistryName()))
                              .filter(Objects::nonNull)
                              .map(this::createBiomeEntries)
                              .forEach(entries::addAll);

        return entries.toArray(new GuiPageButtonList.GuiListEntry[0]);
    }

    private List<GuiPageButtonList.GuiListEntry> createBiomeEntries(BiomeSettingsAdapter adapter) {
        val result = Lists.newArrayList(
                new GuiPageButtonList.GuiLabelEntry(idCounter + OFFSET_BIOME_LABEL,
                                                    adapter.getBiome().getBiomeName(), false),
                new ExtendedGuiPageButtonList.GuiActionButtonEntry(idCounter + OFFSET_BIOME_EDIT,
                                                                   "createWorld.customize.hcisland.field.biome.edit",
                                                                   false, () -> openBiomeEditor(adapter))
        );
        idCounter += result.size();
        return result;
    }

    private void openBiomeEditor(BiomeSettingsAdapter adapter) {
        mc.displayGuiScreen(new GuiCustomizeHCWorldBiome(this, adapter, null /* settings.getSettingsFor(adapter.getBiome().getRegistryName()) */));
    }

    private List<GuiPageButtonList.GuiListEntry> createBiomeEntries(int baseId, String biome, BiomeSettings.Factory config, BiomeSettings.Factory defaultConfig) {
        return Lists.newArrayList(
                new GuiPageButtonList.GuiLabelEntry(baseId + OFFSET_BIOME_LABEL,
                                                    I18n.format("createWorld.customize.hcisland.label.biome." + biome), false),
                null,
                new GuiPageButtonList.GuiLabelEntry(baseId + OFFSET_BIOME_RADIUS_LABEL,
                                                    I18n.format("createWorld.customize.hcisland.field.biome.radius"),
                                                    false),
                /*new GuiPageButtonList.GuiSlideEntry(baseId + OFFSET_BIOME_RADIUS,
                        "",
                        false, this,
                        2.0f, 512.0f,
                        config.radius),*/
                new GuiPageButtonList.GuiLabelEntry(baseId + OFFSET_BIOME_STONE_BLOCK_LABEL,
                                                    I18n.format("createWorld.customize.hcisland.field.biome.stoneBlock"),
                                                    false),
                new GuiPageButtonList.EditBoxEntry(baseId + OFFSET_BIOME_STONE_BLOCK,
                                                   config.stoneBlock,
                                                   false, s -> true),
                new ExtendedGuiPageButtonList.GuiActionButtonEntry(baseId + OFFSET_BIOME_LAYERS,
                                                                   "createWorld.customize.hcisland.field.biome.layers",
                                                                   false, () -> openLayersEditor(config, defaultConfig, false)),
                new ExtendedGuiPageButtonList.GuiActionButtonEntry(baseId + OFFSET_BIOME_LAYERS_UNDERWATER,
                                                                   "createWorld.customize.hcisland.field.biome.layersUnderwater",
                                                                   false, () -> openLayersEditor(config, defaultConfig, true))
        );
    }

    private void openLayersEditor(BiomeSettings.Factory config, BiomeSettings.Factory defaultConfig, boolean underwater) {
        mc.displayGuiScreen(new GuiCustomizeBiomeLayers(this, config, defaultConfig, underwater));
    }

    private void updatePageControls() {
        previousPage.enabled = pages.getPage() != 0;
        nextPage.enabled = pages.getPage() != pages.getPageCount() - 1;
        subtitle = I18n.format("book.pageIndicator", pages.getPage() + 1, pages.getPageCount());
        pageTitle = pageNames[pages.getPage()];
    }


    @Override
    public void setEntryValue(int id, String valueStr) {
        switch (id) {
            case ID_OCEAN_BLOCK_OVERRIDE:
                this.settings.oceanBlockOverride = valueStr;
                break;
            case OFFSET_BIOME_STONE_BLOCK + ID_BASE_BIOME_ISLAND:
                this.settings.island.stoneBlock = valueStr;
                break;
            case OFFSET_BIOME_STONE_BLOCK + ID_BASE_BIOME_ISLAND_BEACH:
                this.settings.islandBeach.stoneBlock = valueStr;
                break;
            case OFFSET_BIOME_STONE_BLOCK + ID_BASE_BIOME_OCEAN:
                this.settings.ocean.stoneBlock = valueStr;
                break;
            case OFFSET_BIOME_STONE_BLOCK + ID_BASE_BIOME_WASTELAND:
                this.settings.wasteland.stoneBlock = valueStr;
                break;
            case OFFSET_BIOME_STONE_BLOCK + ID_BASE_BIOME_WASTELAND_BEACH:
                this.settings.wastelandBeach.stoneBlock = valueStr;
                break;
            case OFFSET_BIOME_STONE_BLOCK + ID_BASE_BIOME_WASTELAND_EDGE:
                this.settings.wastelandEdge.stoneBlock = valueStr;
                break;
        }

        setSettingsModified(!settings.equals(defaultSettings));
    }

    @Override
    public void setEntryValue(int id, float value) {
        switch (id) {
            case ID_SEA_LEVEL_OVERRIDE:
                settings.seaLevelOverride = (int) value;
                break;
        }

        setSettingsModified(!settings.equals(defaultSettings));
    }

    @Override
    public void setEntryValue(int id, boolean value) {
        setSettingsModified(!settings.equals(defaultSettings));
    }

    private void setSettingsModified(boolean modified) {
        dirty = modified;
        defaults.enabled = modified;
    }


    @Override
    public String getText(int id, String name, float value) {
        return getFormattedValue(id, value);
    }

    private String getFormattedValue(int id, float value) {
        // TODO: Select by id
        return String.format("%d", (int) value);
    }


    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (!button.enabled) {
            return;
        }

        switch (button.id) {
            case ID_BUTTON_NEXT:
                pages.nextPage();
                updatePageControls();
                break;
            case ID_BUTTON_PREVIOUS:
                pages.previousPage();
                updatePageControls();
                break;
            case ID_BUTTON_CLIPBOARD:
                setClipboardString(saveValues());
                break;
        }
    }

    @Override
    protected void onDonePressed() {
        parent.chunkProviderSettingsJson = saveValues();
        mc.displayGuiScreen(parent);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (confirmMode == 0 && !confirmDismissed) {
            pages.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        if (confirmDismissed) {
            confirmDismissed = false;
        } else if (confirmMode == 0) {
            pages.mouseReleased(mouseX, mouseY, state);
        }
    }

    protected void restoreDefaults() {
        settings.setDefaults();
        createPagedList();
        setSettingsModified(false);
    }

    protected void setConfirmationControls(boolean visible) {
        super.setConfirmationControls(visible);

        previousPage.enabled = !visible;
        nextPage.enabled = !visible;
        clipboard.enabled = !visible;

        pages.setActive(!visible);
    }


    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        // Modifies text field float values using up/down arrow keys and modifier keys
        if (confirmMode == 0) {
            switch (keyCode) {
                case 200:
                    modifyFocusValue(1.0f);
                    break;
                case 208:
                    modifyFocusValue(-1.0f);
                    break;
                default:
                    pages.onKeyPressed(typedChar, keyCode);
            }
        }
    }

    private void modifyFocusValue(float multiplier) {
        Gui gui = pages.getFocusedControl();

        if (gui instanceof GuiTextField) {
            float modifier = multiplier;

            if (GuiScreen.isShiftKeyDown()) {
                modifier *= 0.1f;

                if (GuiScreen.isCtrlKeyDown()) {
                    modifier *= 0.1f;
                }
            } else if (isCtrlKeyDown()) {
                modifier *= 10.0f;

                if (GuiScreen.isAltKeyDown()) {
                    modifier *= 10.0f;
                }
            }

            GuiTextField textField = (GuiTextField) gui;
            Float value = Floats.tryParse(textField.getText());

            if (value != null) {
                value += modifier;
                int id = textField.getId();
                String formatted = getFormattedValue(id, value);
                textField.setText(formatted);
                setEntryValue(id, formatted);
            }
        }
    }

    @Override
    protected void drawTitles(int mouseX, int mouseY, float partialTicks) {
        pages.drawScreen(mouseX, mouseY, partialTicks);
        super.drawTitles(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRenderer, pageTitle, width / 2, 22, 16777215);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// IDs
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int ID_BUTTON_NEXT = 4;
    private static final int ID_BUTTON_PREVIOUS = 5;
    private static final int ID_BUTTON_CLIPBOARD = 6;

    private static final int ID_SEA_LEVEL_OVERRIDE = 7;
    private static final int ID_SEA_LEVEL_OVERRIDE_LABEL = 8;
    private static final int ID_OCEAN_BLOCK_OVERRIDE = 9;
    private static final int ID_OCEAN_BLOCK_OVERRIDE_LABEL = 10;

    private static final int BIOME_PAGE_FIELD_COUNT = 7;
    private static final int ID_BASE_BIOME_ISLAND = 11;
    private static final int ID_BASE_BIOME_ISLAND_BEACH = ID_BASE_BIOME_ISLAND + BIOME_PAGE_FIELD_COUNT;
    private static final int ID_BASE_BIOME_OCEAN = ID_BASE_BIOME_ISLAND_BEACH + BIOME_PAGE_FIELD_COUNT;
    private static final int ID_BASE_BIOME_WASTELAND = ID_BASE_BIOME_OCEAN + BIOME_PAGE_FIELD_COUNT;
    private static final int ID_BASE_BIOME_WASTELAND_BEACH = ID_BASE_BIOME_WASTELAND + BIOME_PAGE_FIELD_COUNT;
    private static final int ID_BASE_BIOME_WASTELAND_EDGE = ID_BASE_BIOME_WASTELAND_BEACH + BIOME_PAGE_FIELD_COUNT;

    private static final int ID_BASE_BIOME = 11;
    private static final int OFFSET_BIOME_LABEL = 0;
    private static final int OFFSET_BIOME_EDIT = 1;
    private static final int OFFSET_BIOME_RADIUS_LABEL = 1;
    private static final int OFFSET_BIOME_STONE_BLOCK_LABEL = 2;
    private static final int OFFSET_BIOME_STONE_BLOCK = 3;
    private static final int OFFSET_BIOME_LAYERS = 4;
    private static final int OFFSET_BIOME_LAYERS_UNDERWATER = 5;
}
