package jakojaannos.hcisland.client.gui;

import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import lombok.val;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class GuiCustomizeRadialBiomes extends GuiCustomizeWithDefaults<List<HCIslandChunkGeneratorSettings.IslandRadialBiome.Factory>> {
    private final GuiCustomizeHCWorld parent;
    private GuiListRadialBiomeSettings settingsList;

    public GuiCustomizeRadialBiomes(GuiCustomizeHCWorld parent) {
        this.parent = parent;
        this.settings = parent.settings.getBiomes();
    }

    @Override
    public void initGui() {
        super.initGui();

        settingsList = new GuiListRadialBiomeSettings(this, width, height, 32, height - 64, 36);
        settingsList.updateEntries(settings);
    }

    @Override
    protected void onDonePressed() {
        val newBiomes = settingsList.getEntries()
                                    .stream()
                                    .map(GuiListRadialBiomeSettings.Entry::getInfo)
                                    .collect(Collectors.toList());

        parent.settings.setBiomes(newBiomes);
        mc.displayGuiScreen(parent);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        settingsList.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        settingsList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        settingsList.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void drawBackgroundLayer(int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundLayer(mouseX, mouseY, partialTicks);
        settingsList.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void restoreDefaults() {
        super.restoreDefaults();
        // TODO
    }
}
