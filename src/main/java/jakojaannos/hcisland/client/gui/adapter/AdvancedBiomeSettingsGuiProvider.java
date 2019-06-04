package jakojaannos.hcisland.client.gui.adapter;

import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AdvancedBiomeSettingsGuiProvider<TSettings extends BiomeSettings.Factory> implements IBiomeSettingsGuiProvider<TSettings> {
    private int idSeaLevel = -1;
    private int idStoneBlock = -1;

    @Override
    public GuiPageButtonList.GuiListEntry[] createPage(int idCounter, TSettings settings) {
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
                new GuiPageButtonList.GuiSlideEntry(idSeaLevel = idCounter,
                                                    "",
                                                    true,
                                                    (id, name, value) -> String.format("%d", (int) value),
                                                    1, 255,
                                                    settings.seaLevel)
        };
    }

    @Override
    public void setEntryValue(int id, boolean value, TSettings settings) {

    }

    @Override
    public void setEntryValue(int id, float value, TSettings settings) {
        if (id == idSeaLevel) {
            settings.seaLevel = (int) value;
        }
    }

    @Override
    public void setEntryValue(int id, String value, TSettings settings) {
        if (id == idStoneBlock) {
            settings.stoneBlock = value;
        }
    }
}
