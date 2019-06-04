package jakojaannos.hcisland.client.gui.adapter;

import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IBiomeSettingsGuiProvider<TSettings extends BiomeSettings.Factory> {
    GuiPageButtonList.GuiListEntry[] createPage(int idCounter, TSettings settings);

    void setEntryValue(int id, boolean value, TSettings settings);

    void setEntryValue(int id, float value, TSettings settings);

    void setEntryValue(int id, String value, TSettings settings);
}
