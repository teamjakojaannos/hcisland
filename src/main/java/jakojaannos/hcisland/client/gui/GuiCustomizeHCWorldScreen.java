package jakojaannos.hcisland.client.gui;

import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiCustomizeHCWorldScreen extends GuiScreen implements GuiSlider.FormatHelper, GuiPageButtonList.GuiResponder {
    private final GuiCreateWorld parent;
    private final HCIslandChunkGeneratorSettings.Factory defaultSettings;

    protected String title = "Customize World Settings";
    protected String subtitle = "Page %s of %s";
    protected String pageTitle = "Basic settings";
    protected String[] pageNames = new String[2];

    private ExtendedGuiPageButtonList pages;
    private GuiButton done;
    private GuiButton defaults;
    private GuiButton previousPage;
    private GuiButton nextPage;
    private GuiButton confirm;
    private GuiButton cancel;

    private boolean dirty;
    private int confirmMode;
    private boolean confirmDismissed;

    private HCIslandChunkGeneratorSettings.Factory settings;

    public GuiCustomizeHCWorldScreen(GuiCreateWorld parent, @Nullable String preset) {
        this.parent = parent;

        HCIslandChunkGeneratorSettings.Factory.refreshOverrides();
        defaultSettings = new HCIslandChunkGeneratorSettings.Factory();

        loadValues(preset);
    }

    @Override
    public void initGui() {
        final int page = pages != null ? pages.getPage() : 0;
        final int scroll = pages != null ? pages.getAmountScrolled() : 0;

        title = I18n.format("createWorld.customize.hcisland.title");
        buttonList.clear();

        previousPage = addButton(new GuiButton(ID_BUTTON_PREVIOUS, 20, 5, 80, 20, I18n.format("createWorld.customize.custom.prev")));
        nextPage = addButton(new GuiButton(ID_BUTTON_NEXT, width - 100, 5, 80, 20, I18n.format("createWorld.customize.custom.next")));
        done = addButton(new GuiButton(ID_BUTTON_DONE, width / 2 + 98, height - 27, 90, 20, I18n.format("gui.done")));

        defaults = addButton(new GuiButton(ID_BUTTON_DEFAULTS, width / 2 - 187, height - 27, 90, 20, I18n.format("createWorld.customize.custom.defaults")));
        defaults.enabled = dirty;

        confirm = new GuiButton(ID_BUTTON_CONFIRM, width / 2 - 55, 160, 50, 20, I18n.format("gui.yes"));
        confirm.visible = false;

        cancel = new GuiButton(ID_BUTTON_CANCEL, width / 2 + 5, 160, 50, 20, I18n.format("gui.no"));
        cancel.visible = false;

        buttonList.add(confirm);
        buttonList.add(cancel);

        if (confirmMode != 0) {
            confirm.visible = true;
            cancel.visible = true;
        }

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

    public String saveValues() {
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
                new GuiPageButtonList.GuiSlideEntry(ID_SEA_LEVEL_OVERRIDE,
                        I18n.format("createWorld.customize.hcisland.field.seaLevelOverride"),
                        true, this,
                        1.0f, 64.0f,
                        settings.seaLevelOverride),
                new GuiPageButtonList.EditBoxEntry(ID_OCEAN_BLOCK_OVERRIDE,
                        I18n.format("createWorld.customize.hcisland.field.oceanBlockOverride"),
                        true, s -> true)
        };
    }

    private GuiPageButtonList.GuiListEntry[] createBiomePage() {
        List<GuiPageButtonList.GuiListEntry> entries = new ArrayList<>();
        entries.addAll(createBiomeEntries(ID_BASE_BIOME_ISLAND, "island", settings.island, defaultSettings.island));
        entries.addAll(createBiomeEntries(ID_BASE_BIOME_ISLAND_BEACH, "islandBeach", settings.islandBeach, defaultSettings.islandBeach));
        entries.addAll(createBiomeEntries(ID_BASE_BIOME_OCEAN, "ocean", settings.ocean, defaultSettings.ocean));
        entries.addAll(createBiomeEntries(ID_BASE_BIOME_WASTELAND, "wasteland", settings.wasteland, defaultSettings.wasteland));
        entries.addAll(createBiomeEntries(ID_BASE_BIOME_WASTELAND_BEACH, "wastelandBeach", settings.wastelandBeach, defaultSettings.wastelandBeach));
        entries.addAll(createBiomeEntries(ID_BASE_BIOME_WASTELAND_EDGE, "wastelandEdge", settings.wastelandEdge, defaultSettings.wastelandEdge));

        return entries.toArray(new GuiPageButtonList.GuiListEntry[entries.size()]);
    }

    private List<GuiPageButtonList.GuiListEntry> createBiomeEntries(int baseId, String biome, HCIslandChunkGeneratorSettings.Factory.BiomeSettingsFactory config, HCIslandChunkGeneratorSettings.Factory.BiomeSettingsFactory defaultConfig) {
        return Lists.newArrayList(
                new GuiPageButtonList.GuiLabelEntry(baseId + OFFSET_BIOME_LABEL,
                        I18n.format("createWorld.customize.hcisland.label.biome." + biome), false),
                null,
                new GuiPageButtonList.GuiSlideEntry(baseId + OFFSET_BIOME_RADIUS,
                        "createWorld.customize.hcisland.field.biome.radius",
                        false, this,
                        2.0f, 512.0f,
                        config.radius),
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

    private void openLayersEditor(HCIslandChunkGeneratorSettings.Factory.BiomeSettingsFactory config, HCIslandChunkGeneratorSettings.Factory.BiomeSettingsFactory defaultConfig, boolean underwater) {
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
            case OFFSET_BIOME_RADIUS + ID_BASE_BIOME_ISLAND:
                settings.island.radius = (int) value;
                break;
            case OFFSET_BIOME_RADIUS + ID_BASE_BIOME_ISLAND_BEACH:
                settings.islandBeach.radius = (int) value;
                break;
            case OFFSET_BIOME_RADIUS + ID_BASE_BIOME_OCEAN:
                settings.ocean.radius = (int) value;
                break;
            case OFFSET_BIOME_RADIUS + ID_BASE_BIOME_WASTELAND:
                settings.wasteland.radius = (int) value;
                break;
            case OFFSET_BIOME_RADIUS + ID_BASE_BIOME_WASTELAND_BEACH:
                settings.wastelandBeach.radius = (int) value;
                break;
            case OFFSET_BIOME_RADIUS + ID_BASE_BIOME_WASTELAND_EDGE:
                settings.wastelandEdge.radius = (int) value;
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
        return name + ": " + this.getFormattedValue(id, value);
    }

    private String getFormattedValue(int id, float value) {
        // TODO: Select by id
        return String.format("%.3f", value);
    }


    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
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
            case ID_BUTTON_DONE:
                parent.chunkProviderSettingsJson = saveValues();
                mc.displayGuiScreen(parent);
                break;
            case ID_BUTTON_CONFIRM:
                exitConfirmation();
                break;
            case ID_BUTTON_CANCEL:
                confirmMode = 0;
                exitConfirmation();
                break;
            case ID_BUTTON_DEFAULTS:
                if (dirty) {
                    enterConfirmation(ID_BUTTON_DEFAULTS);
                }
                break;
        }
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

    private void enterConfirmation(int mode) {
        confirmMode = mode;
        setConfirmationControls(true);
    }

    private void exitConfirmation() throws IOException {
        switch (confirmMode) {
            case ID_BUTTON_DONE:
                actionPerformed((GuiButton) pages.getComponent(ID_BUTTON_DONE));
                break;
            case ID_BUTTON_DEFAULTS:
                restoreDefaults();
                break;
        }

        confirmMode = 0;
        confirmDismissed = true;
        setConfirmationControls(false);
    }

    private void restoreDefaults() {
        settings.setDefaults();
        createPagedList();
        setSettingsModified(false);
    }

    private void setConfirmationControls(boolean visible) {
        confirm.visible = visible;
        cancel.visible = visible;

        done.enabled = !visible;
        previousPage.enabled = !visible;
        nextPage.enabled = !visible;
        defaults.enabled = dirty && !visible;

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
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        pages.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRenderer, title, width / 2, 2, 16777215);
        drawCenteredString(fontRenderer, subtitle, width / 2, 12, 16777215);
        drawCenteredString(fontRenderer, pageTitle, width / 2, 22, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (confirmMode != 0) {
            drawRect(0, 0, width, height, 0x80000000);
            drawHorizontalLine(width / 2 - 91, width / 2 + 90, 99, -2039584);
            drawHorizontalLine(width / 2 - 91, width / 2 + 90, 185, -6250336);
            drawVerticalLine(width / 2 - 91, 99, 185, -2039584);
            drawVerticalLine(width / 2 + 90, 99, 185, -6250336);

            GlStateManager.disableLighting();
            GlStateManager.disableFog();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            mc.getTextureManager().bindTexture(OPTIONS_BACKGROUND);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double) (width / 2 - 90), 185.0D, 0.0D).tex(0.0D, 2.65625D).color(64, 64, 64, 64).endVertex();
            bufferbuilder.pos((double) (width / 2 + 90), 185.0D, 0.0D).tex(5.625D, 2.65625D).color(64, 64, 64, 64).endVertex();
            bufferbuilder.pos((double) (width / 2 + 90), 100.0D, 0.0D).tex(5.625D, 0.0D).color(64, 64, 64, 64).endVertex();
            bufferbuilder.pos((double) (width / 2 - 90), 100.0D, 0.0D).tex(0.0D, 0.0D).color(64, 64, 64, 64).endVertex();
            tessellator.draw();

            drawCenteredString(fontRenderer, I18n.format("createWorld.customize.custom.confirmTitle"), width / 2, 105, 16777215);
            drawCenteredString(fontRenderer, I18n.format("createWorld.customize.custom.confirm1"), width / 2, 125, 16777215);
            drawCenteredString(fontRenderer, I18n.format("createWorld.customize.custom.confirm2"), width / 2, 135, 16777215);
            confirm.drawButton(mc, mouseX, mouseY, partialTicks);
            cancel.drawButton(mc, mouseX, mouseY, partialTicks);
        }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// IDs
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int ID_BUTTON_NEXT = 0;
    private static final int ID_BUTTON_PREVIOUS = 1;
    private static final int ID_BUTTON_DONE = 2;
    private static final int ID_BUTTON_CONFIRM = 3;
    private static final int ID_BUTTON_CANCEL = 4;
    private static final int ID_BUTTON_DEFAULTS = 5;

    private static final int ID_SEA_LEVEL_OVERRIDE = 6;
    private static final int ID_OCEAN_BLOCK_OVERRIDE = 7;

    private static final int BIOME_PAGE_FIELD_COUNT = 5;
    private static final int ID_BASE_BIOME_ISLAND = 8;
    private static final int ID_BASE_BIOME_ISLAND_BEACH = ID_BASE_BIOME_ISLAND + BIOME_PAGE_FIELD_COUNT;
    private static final int ID_BASE_BIOME_OCEAN = ID_BASE_BIOME_ISLAND_BEACH + BIOME_PAGE_FIELD_COUNT;
    private static final int ID_BASE_BIOME_WASTELAND = ID_BASE_BIOME_OCEAN + BIOME_PAGE_FIELD_COUNT;
    private static final int ID_BASE_BIOME_WASTELAND_BEACH = ID_BASE_BIOME_WASTELAND + BIOME_PAGE_FIELD_COUNT;
    private static final int ID_BASE_BIOME_WASTELAND_EDGE = ID_BASE_BIOME_WASTELAND_BEACH + BIOME_PAGE_FIELD_COUNT;

    private static final int OFFSET_BIOME_LABEL = 0;
    private static final int OFFSET_BIOME_RADIUS = 1;
    private static final int OFFSET_BIOME_STONE_BLOCK = 2;
    private static final int OFFSET_BIOME_LAYERS = 3;
    private static final int OFFSET_BIOME_LAYERS_UNDERWATER = 4;
}
