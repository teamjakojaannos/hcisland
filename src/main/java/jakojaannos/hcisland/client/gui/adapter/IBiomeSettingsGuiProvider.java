package jakojaannos.hcisland.client.gui.adapter;

import jakojaannos.hcisland.client.gui.GuiCustomizeHCWorldBiome;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IBiomeSettingsGuiProvider<TSettings extends BiomeSettings.Factory> {
    GuiPageButtonList.GuiListEntry[] createPage(GuiCustomizeHCWorldBiome screen, int idCounter, TSettings settings, TSettings defaultSettings);

    void setEntryValue(GuiCustomizeHCWorldBiome screen, int id, boolean value, TSettings settings, TSettings defaultSettings);

    void setEntryValue(GuiCustomizeHCWorldBiome screen, int id, float value, TSettings settings, TSettings defaultSettings);

    void setEntryValue(GuiCustomizeHCWorldBiome screen, int id, String value, TSettings settings, TSettings defaultSettings);
}
