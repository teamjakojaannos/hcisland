package jakojaannos.hcisland.client.gui;

import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuiListRadialBiomeSettings extends GuiListExtended {
    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
    private static final int SCROLLBAR_OFFSET = 30;
    private static final int LIST_EXTRA_WIDTH = 85;

    private final GuiCustomizeRadialBiomes owner;
    @Getter private List<Entry> entries = new ArrayList<>();
    @Setter @Getter private int selectedSlotIndex;

    public GuiListRadialBiomeSettings(
            GuiCustomizeRadialBiomes owner,
            int width,
            int height,
            int top,
            int bottom,
            int slotHeight
    ) {
        super(owner.mc, width, height, top, bottom, slotHeight);
        this.owner = owner;
    }

    @Override
    public IGuiListEntry getListEntry(int index) {
        return entries.get(index);
    }

    @Override
    protected int getSize() {
        return entries.size();
    }

    protected boolean isSelected(int slotIndex) {
        return selectedSlotIndex == slotIndex;
    }

    @Override
    protected int getScrollBarX() {
        return super.getScrollBarX() + SCROLLBAR_OFFSET;
    }

    @Override
    public int getListWidth() {
        return super.getListWidth() + LIST_EXTRA_WIDTH;
    }

    public void updateEntries(List<HCIslandChunkGeneratorSettings.IslandRadialBiome.Factory> settings) {
        entries = settings.stream()
                          .map(Entry::new)
                          .collect(Collectors.toList());
    }

    private boolean canMoveDown(int slotIndex) {
        return slotIndex < getSize() - 1;
    }

    private boolean canMoveUp(int slotIndex) {
        return slotIndex > 0;
    }

    private void moveEntryUp(Entry entry, int slotIndex, boolean shiftKeyDown) {
        swapEntries(entry, slotIndex, shiftKeyDown ? 0 : slotIndex - 1);
    }

    private void moveEntryDown(Entry entry, int slotIndex, boolean shiftKeyDown) {
        swapEntries(entry, slotIndex, shiftKeyDown ? entries.size() - 1 : slotIndex + 1);
    }

    private void swapEntries(Entry entry, int slotIndex, int newIndex) {
        if (selectedSlotIndex == slotIndex) {
            selectedSlotIndex = newIndex;
        }

        entries.set(slotIndex, entries.get(newIndex));
        entries.set(newIndex, entry);
    }

    private void selectEntry(int slotIndex) {
        this.selectedSlotIndex = slotIndex;
    }

    @AllArgsConstructor
    public class Entry implements IGuiListEntry {
        @Getter private HCIslandChunkGeneratorSettings.IslandRadialBiome.Factory info;

        @Override
        public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            mc.fontRenderer.drawString(String.format("%s -> %d", info.getBiomeId(), info.getRadius()), x + 32 + 3, y + 1, 16777215);

            mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);

            if (isSelected) {
                Gui.drawRect(x, y, x + listWidth - 8, y + 32, -1601138544);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int k1 = mouseX - x;
            int l1 = mouseY - y;

            if (canMoveUp(slotIndex)) {
                if (k1 < 16 && l1 < 16) {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }

            if (canMoveDown(slotIndex)) {
                if (k1 < 16 && l1 > 16) {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            if (relativeX <= 32)
            {
                if (relativeX < 16 && relativeY < 16 && canMoveUp(slotIndex))
                {
                    moveEntryUp(this, slotIndex, GuiScreen.isShiftKeyDown());
                    return true;
                }

                if (relativeX < 16 && relativeY > 16 && canMoveDown(slotIndex))
                {
                    moveEntryDown(this, slotIndex, GuiScreen.isShiftKeyDown());
                    return true;
                }
            }

            selectEntry(slotIndex);
            return true;
        }
        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        }
    }
}
