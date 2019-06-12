package jakojaannos.hcisland.client.gui.adapter;

import com.google.common.collect.Lists;
import jakojaannos.hcisland.client.gui.ExtendedGuiPageButtonList;
import jakojaannos.hcisland.client.gui.GuiCustomizeBiomeLayers;
import jakojaannos.hcisland.client.gui.GuiCustomizeHCWorldBiome;
import jakojaannos.hcisland.util.BlockHelper;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public class LayeredBiomeSettingsGuiProvider<TSettings extends BiomeSettings> implements IBiomeSettingsGuiProvider<TSettings> {
    private int idSeaLevel = -1;
    private int idOceanBlock = -1;
    private int idStoneBlock = -1;

    @Override
    public List<GuiPageButtonList.GuiListEntry> createPage(GuiCustomizeHCWorldBiome screen, int idCounter, TSettings settings, Supplier<TSettings> defaultSettingsSupplier) {
        return Lists.newArrayList(
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.biome.field.stoneBlock"),
                                                    true),
                new GuiPageButtonList.EditBoxEntry(idStoneBlock = idCounter++,
                                                   settings.stoneBlock.getBlock().getRegistryName().toString(),
                                                   true,
                                                   s -> true),
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.biome.field.seaLevel"),
                                                    true),
                new GuiPageButtonList.GuiSlideEntry(idSeaLevel = idCounter++,
                                                    "",
                                                    true,
                                                    (id, name, value) -> String.format("%d", (int) value),
                                                    1, 64,
                                                    settings.seaLevel),
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.biome.field.oceanBlock"),
                                                    true),
                new GuiPageButtonList.EditBoxEntry(idOceanBlock = idCounter++,
                                                   settings.oceanBlock.getBlock().getRegistryName().toString(),
                                                   true,
                                                   s -> true),
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.biome.field.layers"),
                                                    true),
                new ExtendedGuiPageButtonList.GuiActionButtonEntry(idCounter++,
                                                                   "createWorld.customize.hcisland.openEditorButtonText",
                                                                   true,
                                                                   () -> openLayersEditor(screen, settings, defaultSettingsSupplier, false)),
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.biome.field.layersUnderwater"),
                                                    true),
                new ExtendedGuiPageButtonList.GuiActionButtonEntry(idCounter++,
                                                                   "createWorld.customize.hcisland.openEditorButtonText",
                                                                   true,
                                                                   () -> openLayersEditor(screen, settings, defaultSettingsSupplier, true))
        );
    }

    private void openLayersEditor(GuiCustomizeHCWorldBiome screen, TSettings settings, Supplier<TSettings> defaultSettingsSupplier, boolean underwater) {
        screen.mc.displayGuiScreen(new GuiCustomizeBiomeLayers(screen,
                                                               settings.layers,
                                                               () -> defaultSettingsSupplier.get().layers,
                                                               (layers) -> settings.layers = layers,
                                                               underwater));
    }

    @Override
    public void setEntryValue(GuiCustomizeHCWorldBiome screen, int id, boolean value, TSettings settings) {

    }

    @Override
    public void setEntryValue(GuiCustomizeHCWorldBiome screen, int id, float value, TSettings settings) {
        if (id == idSeaLevel) {
            settings.seaLevel = (int) value;
        }
    }

    @Override
    public void setEntryValue(GuiCustomizeHCWorldBiome screen, int id, String value, TSettings settings) {
        if (id == idStoneBlock) {
            settings.stoneBlock = BlockHelper.stringToBlockstateWithFallback(Blocks.STONE.getDefaultState(), value);
        } else if (id == idOceanBlock) {
            settings.oceanBlock = BlockHelper.stringToBlockstateWithFallback(Blocks.STONE.getDefaultState(), value);
        }
    }
}
