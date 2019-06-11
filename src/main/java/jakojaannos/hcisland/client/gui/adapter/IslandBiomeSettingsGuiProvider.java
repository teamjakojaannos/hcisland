package jakojaannos.hcisland.client.gui.adapter;

import jakojaannos.hcisland.client.gui.GuiCustomizeHCWorldBiome;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import lombok.val;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.resources.I18n;

import java.util.Arrays;
import java.util.List;

public class IslandBiomeSettingsGuiProvider<TSettings extends BiomeSettings.Island> extends AdvancedBiomeSettingsGuiProvider<TSettings> {
    private int idGenerateFalls = -1;
    private int idGenerateLakes = -1;
    private int idGenerateLakesLava = -1;

    @Override
    public List<GuiPageButtonList.GuiListEntry> createPage(GuiCustomizeHCWorldBiome screen, int idCounter, TSettings settings, TSettings defaultSettings) {
        val list = super.createPage(screen, idCounter, settings, defaultSettings);
        idCounter += list.size();

        list.addAll(Arrays.asList(
                new GuiPageButtonList.GuiButtonEntry(idGenerateFalls = idCounter++,
                                                     I18n.format("createWorld.customize.hcisland.field.biome.generateFalls"),
                                                     true,
                                                     settings.generateFalls),
                new GuiPageButtonList.GuiButtonEntry(idGenerateLakes = idCounter++,
                                                     I18n.format("createWorld.customize.hcisland.field.biome.generateLakes"),
                                                     true,
                                                     settings.generateLakes),
                new GuiPageButtonList.GuiButtonEntry(idGenerateLakesLava = idCounter++,
                                                     I18n.format("createWorld.customize.hcisland.field.biome.generateLakesLava"),
                                                     true,
                                                     settings.generateLakesLava)
        ));

        return list;
    }

    @Override
    public void setEntryValue(GuiCustomizeHCWorldBiome screen, int id, boolean value, TSettings settings, TSettings defaultSettings) {
        super.setEntryValue(screen, id, value, settings, defaultSettings);

        if (id == idGenerateFalls) {
            settings.generateFalls = value;
        } else if (id == idGenerateLakes) {
            settings.generateLakes = value;
        } else if (id == idGenerateLakesLava) {
            settings.generateLakesLava = value;
        }
    }
}
