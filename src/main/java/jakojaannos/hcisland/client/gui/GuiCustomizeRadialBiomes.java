package jakojaannos.hcisland.client.gui;

import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import lombok.val;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class GuiCustomizeRadialBiomes extends GuiCustomizeWithDefaults<List<HCIslandChunkGeneratorSettings.IslandRadialBiome>> {
    private final GuiCustomizeHCWorld parent;
    private RadialBiomeSettingsList settingsList;

    private GuiButton add;

    public GuiCustomizeRadialBiomes(
            GuiCustomizeHCWorld parent,
            Supplier<List<HCIslandChunkGeneratorSettings.IslandRadialBiome>> defaultSettingsSupplier,
            Consumer<List<HCIslandChunkGeneratorSettings.IslandRadialBiome>> settingsApplier
    ) {
        super(defaultSettingsSupplier, settingsApplier);
        this.title = I18n.format("createWorld.customize.hcisland.radial.title");
        this.subtitle = I18n.format("createWorld.customize.hcisland.radial.subtitle");
        this.parent = parent;
        this.settings = parent.settings.getBiomes();
    }

    public <T extends GuiButton> T addButton(T button) {
        return super.addButton(button);
    }

    @Override
    protected void createButtons() {
        super.createButtons();
        add = addButton(new GuiButton(idCounter++,
                                      width / 2 + 98,
                                      height - 27 - 25,
                                      90,
                                      20,
                                      I18n.format("createWorld.customize.hcisland.radial.add")));
        settingsList = new RadialBiomeSettingsList(idCounter,
                                                   this,
                                                   width,
                                                   height,
                                                   32,
                                                   height - 64,
                                                   36);
        settingsList.updateEntries(settings);
    }

    @Override
    protected void onDonePressed() {
        val newBiomes = settingsList.getEntries()
                                    .stream()
                                    .map(RadialBiomeSettingsList.Entry::getInfo)
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
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        settingsList.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        settingsList.actionPerformed(button);

        if (!button.enabled) {
            return;
        }

        if (button.id == add.id) {
            settingsList.addEntry();
        }
    }

    @Override
    protected void restoreDefaults() {
        super.restoreDefaults();
        settingsList.updateEntries(settings);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        settingsList.postDraw();
    }
}
