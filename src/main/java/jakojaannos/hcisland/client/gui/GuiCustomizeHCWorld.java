package jakojaannos.hcisland.client.gui;

import jakojaannos.hcisland.init.ModRegistries;
import jakojaannos.hcisland.util.world.gen.GeneratorSettingsHelper;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import jakojaannos.hcisland.world.gen.adapter.BiomeSettingsAdapter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
@Log4j2
public class GuiCustomizeHCWorld extends GuiPagedCustomizeWithDefaults<HCIslandChunkGeneratorSettings> implements GuiPageButtonList.GuiResponder {
    private final GuiCreateWorld parent;

    private GuiButton clipboard;

    private int idIslandShapeFuzz = -1;
    private int idSmoothEdges = -1;
    private int idGenerateEdges = -1;
    private int idShoreScale = -1;
    private int idBeachSize = -1;

    public GuiCustomizeHCWorld(GuiCreateWorld parent, @Nullable String preset, Consumer<HCIslandChunkGeneratorSettings> settingsApplier) {
        super(GeneratorSettingsHelper::createOverriddenDefaults, settingsApplier);
        this.parent = parent;

        this.title = "Customize World Settings";
        this.subtitle = "Page 1 of many";

        // TODO: Initial settings do not respect overrides
        log.info("Creating generator settings for customization. Preset is {}", (preset == null ? "null" : (preset.isEmpty() ? "empty" : preset)));
        this.settings = (preset != null && !preset.isEmpty())
                ? GeneratorSettingsHelper.fromJson(preset)
                : GeneratorSettingsHelper.createOverriddenDefaults();
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
                                                    I18n.format("createWorld.customize.hcisland.general.field.islandShapeFuzz"),
                                                    true),
                new GuiPageButtonList.GuiSlideEntry(idIslandShapeFuzz = idCounter++,
                                                    "",
                                                    true,
                                                    (id, name, value) -> String.valueOf(Math.round(value)),
                                                    0,
                                                    4,
                                                    settings.getIslandShapeFuzz()),
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.general.field.smoothBiomeEdges"),
                                                    true),
                new GuiPageButtonList.GuiButtonEntry(idSmoothEdges = idCounter++,
                                                     I18n.format("createWorld.customize.hcisland.enabled"),
                                                     true,
                                                     settings.isSmoothBiomeEdges()),
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.general.field.generateEdges"),
                                                    true),
                new GuiPageButtonList.GuiButtonEntry(idGenerateEdges = idCounter++,
                                                     I18n.format("createWorld.customize.hcisland.enabled"),
                                                     true,
                                                     settings.isGenerateEdges()),
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.general.field.shoreScale"),
                                                    true),
                new GuiPageButtonList.GuiSlideEntry(idShoreScale = idCounter++,
                                                    "",
                                                    true,
                                                    (id, name, value) -> String.valueOf(Math.round(value)),
                                                    0,
                                                    2,
                                                    settings.getShoreScale()),
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.general.field.beachSize"),
                                                    true),
                new GuiPageButtonList.GuiSlideEntry(idBeachSize = idCounter++,
                                                    "",
                                                    true,
                                                    (id, name, value) -> String.valueOf(Math.round(value)),
                                                    0,
                                                    2,
                                                    settings.getBeachSize()),
        };
    }

    private GuiPageButtonList.GuiListEntry[] createRadialPage() {
        return new GuiPageButtonList.GuiListEntry[]{
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.radial.openEditorLabel"),
                                                    true),
                new ExtendedGuiPageButtonList.GuiActionButtonEntry(idCounter++,
                                                                   I18n.format("createWorld.customize.hcisland.openEditorButtonText"),
                                                                   true,
                                                                   () -> mc.displayGuiScreen(new GuiCustomizeRadialBiomes(this,
                                                                                                                          () -> defaultSettingsSupplier.get().getBiomes(),
                                                                                                                          s -> settings.setBiomes(s))))
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
                                                    adapter.getBiome().getBiomeName(),
                                                    false),
                new ExtendedGuiPageButtonList.GuiActionButtonEntry(idCounter++,
                                                                   "createWorld.customize.hcisland.biome.edit",
                                                                   false,
                                                                   () -> openBiomeEditor(adapter))
        );
    }

    private void openBiomeEditor(BiomeSettingsAdapter adapter) {
        mc.displayGuiScreen(new GuiCustomizeHCWorldBiome(this,
                                                         adapter,
                                                         settings.getSettingsFor(adapter.getBiome().getRegistryName()),
                                                         () -> defaultSettingsSupplier.get().getSettingsFor(adapter.getBiome().getRegistryName()),
                                                         s -> settings.setSettingsFor(adapter.getBiome().getRegistryName(), s)));
    }

    @Override
    public void setEntryValue(int id, String valueStr) {
        checkSettingsModified();
    }

    @Override
    public void setEntryValue(int id, float value) {
        if (id == idIslandShapeFuzz) {
            settings.setIslandShapeFuzz((int) value);
        } else if (id == idShoreScale) {
            settings.setShoreScale((int) value);
        } else if (id == idBeachSize) {
            settings.setBeachSize((int) value);
        }

        checkSettingsModified();
    }

    @Override
    public void setEntryValue(int id, boolean value) {
        if (id == idSmoothEdges) {
            settings.setSmoothBiomeEdges(value);
        } else if (id == idGenerateEdges) {
            settings.setGenerateEdges(value);
        }

        checkSettingsModified();
    }


    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (!button.enabled) {
            return;
        }

        if (button.id == clipboard.id) {
            setClipboardString(GeneratorSettingsHelper.toJson(settings).replace("\n", ""));
        }
    }

    @Override
    protected void onDonePressed() {
        super.onDonePressed();
        mc.displayGuiScreen(parent);
    }

    @Override
    protected void setConfirmationControls(boolean visible) {
        super.setConfirmationControls(visible);

        clipboard.enabled = !visible;
    }
}
