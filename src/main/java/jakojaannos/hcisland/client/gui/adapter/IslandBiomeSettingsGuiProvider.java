package jakojaannos.hcisland.client.gui.adapter;

import jakojaannos.hcisland.client.gui.GuiCustomizeHCWorldBiome;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import jakojaannos.hcisland.world.gen.LayeredBiomeSettings;
import lombok.val;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.resources.I18n;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class IslandBiomeSettingsGuiProvider<TSettings extends LayeredBiomeSettings.Island> extends LayeredBiomeSettingsGuiProvider<TSettings> {
    private int idGenerateFalls = -1;
    private int idGenerateLakes = -1;
    private int idGenerateLakesLava = -1;

    @Override
    public List<GuiPageButtonList.GuiListEntry> createPage(GuiCustomizeHCWorldBiome screen, int idCounter, TSettings settings, Supplier<TSettings> defaultSettingsSupplier) {
        val list = super.createPage(screen, idCounter, settings, defaultSettingsSupplier);
        idCounter += list.size();

        list.addAll(Arrays.asList(
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.biome.field.generateFalls"),
                                                    true),
                new GuiPageButtonList.GuiButtonEntry(idGenerateFalls = idCounter++,
                                                     I18n.format("createWorld.customize.hcisland.enabled"),
                                                     true,
                                                     settings.generateFalls),
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.biome.field.generateLakes"),
                                                    true),
                new GuiPageButtonList.GuiButtonEntry(idGenerateLakes = idCounter++,
                                                     I18n.format("createWorld.customize.hcisland.enabled"),
                                                     true,
                                                     settings.generateLakes),
                new GuiPageButtonList.GuiLabelEntry(idCounter++,
                                                    I18n.format("createWorld.customize.hcisland.biome.field.generateLakesLava"),
                                                    true),
                new GuiPageButtonList.GuiButtonEntry(idGenerateLakesLava = idCounter++,
                                                     I18n.format("createWorld.customize.hcisland.enabled"),
                                                     true,
                                                     settings.generateLakesLava)
        ));

        return list;
    }

    @Override
    public void setEntryValue(GuiCustomizeHCWorldBiome screen, int id, boolean value, TSettings settings) {
        super.setEntryValue(screen, id, value, settings);

        if (id == idGenerateFalls) {
            settings.generateFalls = value;
        } else if (id == idGenerateLakes) {
            settings.generateLakes = value;
        } else if (id == idGenerateLakesLava) {
            settings.generateLakesLava = value;
        }
    }
}
