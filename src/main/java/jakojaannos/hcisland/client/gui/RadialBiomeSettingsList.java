package jakojaannos.hcisland.client.gui;

import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RadialBiomeSettingsList extends GuiListExtended {
    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");

    private static final int SCROLLBAR_OFFSET = 10;
    private static final int LIST_PADDING = 16;
    private static final int ENTRY_CONTROL_OFFSET = 8;
    private static final int BUTTON_MARGIN = 2;
    private static final int SMALL_BUTTON_SIZE = 16;
    private static final int TEXT_FIELD_WIDTH = 128;
    private static final int RADIUS_SLIDER_WIDTH = 64;


    @Getter private List<Entry> entries = new ArrayList<>();
    @Setter @Getter private int selectedSlotIndex;

    private final GuiCustomizeRadialBiomes owner;

    private Gui focused;
    private int idCounter;

    public RadialBiomeSettingsList(
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
        return getListWidth() + SCROLLBAR_OFFSET;
    }

    @Override
    public int getListWidth() {
        return width - LIST_PADDING;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        super.actionPerformed(button);


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

    private GuiButton addButton(GuiButton button) {
        return owner.addButton(button);
    }

    public class Entry implements IGuiListEntry, GuiPageButtonList.GuiResponder {
        @Getter private HCIslandChunkGeneratorSettings.IslandRadialBiome.Factory info;
        private String biomeName = null;

        private final GuiButton remove;
        private final GuiButton increaseRadius;
        private final GuiSlider radius;
        private final GuiButton decreaseRadius;
        private final GuiTextField biome;

        public Entry(HCIslandChunkGeneratorSettings.IslandRadialBiome.Factory info) {
            this.info = info;

            remove = addButton(new GuiButtonExt(idCounter++,
                                                0,
                                                ENTRY_CONTROL_OFFSET,
                                                SMALL_BUTTON_SIZE,
                                                SMALL_BUTTON_SIZE,
                                                "X"));
            increaseRadius = addButton(new GuiButtonExt(idCounter++,
                                                        0,
                                                        ENTRY_CONTROL_OFFSET,
                                                        SMALL_BUTTON_SIZE,
                                                        SMALL_BUTTON_SIZE,
                                                        "+"));
            radius = new GuiSlider(this,
                                   idCounter++,
                                   0,
                                   ENTRY_CONTROL_OFFSET,
                                   "",
                                   1,
                                   64,
                                   3,
                                   (id, name, value) -> String.valueOf((int) value));
            radius.width = RADIUS_SLIDER_WIDTH;

            decreaseRadius = addButton(new GuiButtonExt(idCounter++,
                                                        0,
                                                        ENTRY_CONTROL_OFFSET,
                                                        SMALL_BUTTON_SIZE,
                                                        SMALL_BUTTON_SIZE,
                                                        "-"));

            biome = new GuiTextField(idCounter,
                                     mc.fontRenderer,
                                     0,
                                     ENTRY_CONTROL_OFFSET,
                                     TEXT_FIELD_WIDTH,
                                     SMALL_BUTTON_SIZE);

            biome.setText(info.getBiomeId());
            radius.setSliderValue(info.getRadius(), false);
        }

        @Override
        public void setEntryValue(int id, boolean value) {
        }

        @Override
        public void setEntryValue(int id, String value) {
        }

        @Override
        public void setEntryValue(int id, float value) {

        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            if (relativeX <= 32) {
                if (relativeX < 16 && relativeY < 16 && canMoveUp(slotIndex)) {
                    moveEntryUp(this, slotIndex, GuiScreen.isShiftKeyDown());
                    return true;
                }

                if (relativeX < 16 && relativeY > 16 && canMoveDown(slotIndex)) {
                    moveEntryDown(this, slotIndex, GuiScreen.isShiftKeyDown());
                    return true;
                }
            }

            Gui newFocused = null;
            if (remove.mousePressed(mc, mouseX, mouseY)) {
                newFocused = remove;
            } else if (increaseRadius.mousePressed(mc, mouseX, mouseY)) {
                newFocused = increaseRadius;
            } else if (radius.mousePressed(mc, mouseX, mouseY)) {
                newFocused = radius;
            } else if (decreaseRadius.mousePressed(mc, mouseX, mouseY)) {
                newFocused = decreaseRadius;
            } else if (biome.mouseClicked(mouseX, mouseY, mouseEvent)) {
                newFocused = biome;
            }

            if (newFocused != null) {
                focused = newFocused;
                return true;
            }

            selectEntry(slotIndex);
            return true;
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            remove.mouseReleased(mouseX, mouseY);
            increaseRadius.mouseReleased(mouseX, mouseY);
            radius.mouseReleased(mouseX, mouseY);
            decreaseRadius.mouseReleased(mouseX, mouseY);
        }

        @Override
        public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
            val controlY = y + ENTRY_CONTROL_OFFSET;
            remove.y = increaseRadius.y = radius.y = decreaseRadius.y = biome.y = controlY;

            remove.x = x + getListWidth() - SMALL_BUTTON_SIZE - BUTTON_MARGIN;
            increaseRadius.x = x + 18;
            decreaseRadius.x = x + 18 + SMALL_BUTTON_SIZE + 2 * BUTTON_MARGIN + radius.width;

            radius.x = x + 18 + SMALL_BUTTON_SIZE + BUTTON_MARGIN;
            biome.x = x + getListWidth() - TEXT_FIELD_WIDTH - SMALL_BUTTON_SIZE - BUTTON_MARGIN * 2;
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            updatePosition(slotIndex, x, y, partialTicks);

            val biomeDisplayName = biomeName != null ? biomeName : "ERROR";
            mc.fontRenderer.drawString(String.format("Biome: %s, radius: %d", biomeDisplayName, info.getRadius()), x + 32 + 3, y + 1, 16777215);

            mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);

            drawSelectionBox(x, y, listWidth, isSelected);

            drawOrderControls(slotIndex, x, y, mouseX, mouseY);

            remove.drawButton(mc, mouseX, mouseY, partialTicks);
            increaseRadius.drawButton(mc, mouseX, mouseY, partialTicks);
            radius.drawButton(mc, mouseX, mouseY, partialTicks);
            decreaseRadius.drawButton(mc, mouseX, mouseY, partialTicks);
            biome.drawTextBox();
        }

        private void drawSelectionBox(int x, int y, int listWidth, boolean isSelected) {
            if (isSelected) {
                Gui.drawRect(x, y, x + listWidth - 8, y + 32, -1601138544);
            }
        }

        private void drawOrderControls(int slotIndex, int x, int y, int mouseX, int mouseY) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int localX = mouseX - x;
            int localY = mouseY - y;

            if (canMoveUp(slotIndex)) {
                if (localX < 16 && localY < 16) {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }

            if (canMoveDown(slotIndex)) {
                if (localX < 16 && localY > 16) {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }
        }
    }
}