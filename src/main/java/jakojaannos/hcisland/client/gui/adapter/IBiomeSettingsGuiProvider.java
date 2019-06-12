package jakojaannos.hcisland.client.gui.adapter;

import jakojaannos.hcisland.client.gui.GuiCustomizeHCWorldBiome;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public interface IBiomeSettingsGuiProvider<TSettings extends BiomeSettings> {
    List<GuiPageButtonList.GuiListEntry> createPage(GuiCustomizeHCWorldBiome screen, int idCounter, TSettings settings, Supplier<TSettings> defaultSettingsSupplier);

    void setEntryValue(GuiCustomizeHCWorldBiome screen, int id, boolean value, TSettings settings);

    void setEntryValue(GuiCustomizeHCWorldBiome screen, int id, float value, TSettings settings);

    void setEntryValue(GuiCustomizeHCWorldBiome screen, int id, String value, TSettings settings);
}
