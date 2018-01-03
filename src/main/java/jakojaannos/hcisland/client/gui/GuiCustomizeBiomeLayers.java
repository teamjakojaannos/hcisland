package jakojaannos.hcisland.client.gui;

import jakojaannos.hcisland.world.biome.BlockLayer;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiCustomizeBiomeLayers extends GuiScreen implements GuiPageButtonList.GuiResponder {
    private final GuiCustomizeHCWorldScreen parent;
    private final HCIslandChunkGeneratorSettings.Factory.BiomeSettingsFactory config;
    private final HCIslandChunkGeneratorSettings.Factory.BiomeSettingsFactory defaultConfig;
    private final boolean underwater;

    private GuiLayerList layerList;
    private GuiButton done;
    private GuiButton add;
    private GuiButton defaults;

    private Gui focused;
    private int idCounter;

    public GuiCustomizeBiomeLayers(GuiCustomizeHCWorldScreen parent, HCIslandChunkGeneratorSettings.Factory.BiomeSettingsFactory config, HCIslandChunkGeneratorSettings.Factory.BiomeSettingsFactory defaultConfig, boolean underwater) {
        this.parent = parent;
        this.config = config;
        this.defaultConfig = defaultConfig;
        this.underwater = underwater;
    }

    @Override
    public void initGui() {
        done = addButton(new GuiButton(0, width / 2 - 187, height - 27, 90, 20, I18n.format("gui.done")));

        add = addButton(new GuiButton(1, width - 30, height - 27, 20, 20, " + "));

        defaults = addButton(new GuiButton(2, width / 2 - 87, height - 27, 90, 20, I18n.format("createWorld.customize.custom.defaults")));

        idCounter = 3;

        layerList = new GuiLayerList(mc, width, height, 32, height - 32, 25);
        for (String layer : underwater ? config.layersUnderwater : config.layers) {
            final BlockLayer bl = new BlockLayer(layer);
            layerList.addEntry(bl.getDepth(), bl.getBlock().getBlock().getRegistryName().toString());
            layerList.addEntry(bl.getDepth(), bl.getBlock().getBlock().getRegistryName().toString());
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        layerList.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        layerList.mouseClicked(mouseX, mouseY, mouseButton);

        for (LayerEntry entry : layerList.entries) {
            if (focused != entry.block) {
                entry.block.setFocused(false);
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        layerList.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (focused != null && focused instanceof GuiTextField) {
            ((GuiTextField) focused).textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        layerList.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void setEntryValue(int id, boolean value) {
    }

    @Override
    public void setEntryValue(int id, float value) {
    }

    @Override
    public void setEntryValue(int id, String value) {
    }

    private class GuiLayerList extends GuiListExtended {
        private List<LayerEntry> entries = new ArrayList<>();

        private GuiLayerList(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
            super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        }

        @Override
        public IGuiListEntry getListEntry(int index) {
            return entries.get(index);
        }

        @Override
        protected int getSize() {
            return entries.size();
        }

        @Override
        public int getListWidth() {
            return width;
        }

        @Override
        protected int getScrollBarX() {
            return width - 6;
        }

        private void addEntry(int depth, String block) {
            LayerEntry entry = new LayerEntry(idCounter);
            entry.block.setText(block);
            entry.depth.setSliderValue(depth, false);
            entries.add(entry);
            idCounter += 3;
        }
    }

    private class LayerEntry implements GuiListExtended.IGuiListEntry, GuiSlider.FormatHelper {
        private final GuiButton remove;
        private final GuiSlider depth;
        private final GuiTextField block;

        private final int sliderId;

        private LayerEntry(int baseId) {
            this.remove = new GuiButtonExt(baseId++, width - 35, 0, 25, 25, "X");
            this.sliderId = baseId++;
            this.depth = new GuiSlider(GuiCustomizeBiomeLayers.this, sliderId, 10, 0, "Depth", 1, 255, 1, this);
            this.block = new GuiTextField(baseId, fontRenderer, 170, 0, width - 215, 25);
        }

        @Override
        public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
            remove.y = y;
            depth.y = y;
            block.y = y;
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            remove.y = y;
            depth.y = y;
            block.y = y;
            remove.drawButton(mc, mouseX, mouseY, partialTicks);
            depth.drawButton(mc, mouseX, mouseY, partialTicks);
            block.drawTextBox();
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            boolean flag;
            if (flag = remove.mousePressed(mc, mouseX, mouseY)) {
                focused = remove;
            }

            boolean flag2;
            if (flag2 = depth.mousePressed(mc, mouseX, mouseY)) {
                focused = depth;
            }

            boolean flag3;
            if (flag3 = block.mouseClicked(mouseX, mouseY, mouseEvent)) {
                focused = block;
            }

            return (flag || flag2 || flag3);
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            remove.mouseReleased(x, y);
            depth.mouseReleased(x, y);
        }

        @Override
        public String getText(int id, String name, float value) {
            return I18n.format(name) + ": " + (int) value;
        }
    }
}
