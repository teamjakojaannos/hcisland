package jakojaannos.hcisland.client.gui.adapter;

import jakojaannos.hcisland.client.gui.ExtendedGuiPageButtonList;
import jakojaannos.hcisland.client.gui.GuiCustomizeBiomeLayers;
import jakojaannos.hcisland.client.gui.GuiCustomizeHCWorldBiome;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AdvancedBiomeSettingsGuiProvider<TSettings extends BiomeSettings.Factory> implements IBiomeSettingsGuiProvider<TSettings> {
    private int idSeaLevel = -1;
    private int idOceanBlock = -1;
    private int idStoneBlock = -1;

    @Override
    public GuiPageButtonList.GuiListEntry[] createPage(GuiCustomizeHCWorldBiome screen, int idCounter, TSettings settings, TSettings defaultSettings) {
        return new GuiPageButtonList.GuiListEntry[]{
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.field.biome.stoneBlock"),
                                                    true),
                new GuiPageButtonList.EditBoxEntry(idStoneBlock = idCounter++,
                                                   settings.stoneBlock,
                                                   true,
                                                   s -> true),
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.field.biome.seaLevel"),
                                                    true),
                new GuiPageButtonList.GuiSlideEntry(idSeaLevel = idCounter++,
                                                    "",
                                                    true,
                                                    (id, name, value) -> String.format("%d", (int) value),
                                                    1, 64,
                                                    settings.seaLevel),
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.field.biome.oceanBlock"),
                                                    true),
                new GuiPageButtonList.EditBoxEntry(idOceanBlock = idCounter++,
                                                   settings.oceanBlock,
                                                   true,
                                                   s -> true),
                new ExtendedGuiPageButtonList.GuiActionButtonEntry(idCounter++,
                                                                   "createWorld.customize.hcisland.field.biome.layers",
                                                                   true,
                                                                   () -> openLayersEditor(screen, settings, defaultSettings, false)),
                new ExtendedGuiPageButtonList.GuiActionButtonEntry(idCounter++,
                                                                   "createWorld.customize.hcisland.field.biome.layersUnderwater",
                                                                   true,
                                                                   () -> openLayersEditor(screen, settings, defaultSettings, true))
        };
    }

    private void openLayersEditor(GuiCustomizeHCWorldBiome screen, TSettings settings, TSettings defaultSettings, boolean underwater) {
        screen.mc.displayGuiScreen(new GuiCustomizeBiomeLayers(screen, settings, defaultSettings, underwater));
    }

    @Override
    public void setEntryValue(GuiCustomizeHCWorldBiome screen, int id, boolean value, TSettings settings, TSettings defaultSettings) {

    }

    @Override
    public void setEntryValue(GuiCustomizeHCWorldBiome screen, int id, float value, TSettings settings, TSettings defaultSettings) {
        if (id == idSeaLevel) {
            settings.seaLevel = (int) value;
        }
    }

    @Override
    public void setEntryValue(GuiCustomizeHCWorldBiome screen, int id, String value, TSettings settings, TSettings defaultSettings) {
        if (id == idStoneBlock) {
            settings.stoneBlock = value;
        } else if (id == idOceanBlock) {
            settings.oceanBlock = value;
        }
    }
}
