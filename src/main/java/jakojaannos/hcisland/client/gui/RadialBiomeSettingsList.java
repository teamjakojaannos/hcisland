package jakojaannos.hcisland.client.gui;

import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RadialBiomeSettingsList extends GuiListExtended {
    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");

    private static final int SCROLLBAR_OFFSET = 16;
    private static final int LIST_EXTRA_WIDTH = 180;
    private static final int ENTRY_CONTROL_OFFSET = 10;
    private static final int BUTTON_MARGIN = 2;
    private static final int SMALL_BUTTON_SIZE = 16;
    private static final int TEXT_FIELD_WIDTH = 150;
    private static final int RADIUS_SLIDER_WIDTH = 85;


    @Getter private List<Entry> entries = new ArrayList<>();
    @Setter @Getter private int selectedSlotIndex;

    private final GuiCustomizeRadialBiomes owner;

    private Gui focused;
    private int idCounter;

    public RadialBiomeSettingsList(
            int baseId,
            GuiCustomizeRadialBiomes owner,
            int width,
            int height,
            int top,
            int bottom,
            int slotHeight
    ) {
        super(owner.mc, width, height, top, bottom, slotHeight);
        this.idCounter = baseId;
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
        return super.getListWidth() + LIST_EXTRA_WIDTH;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        super.actionPerformed(button);

        for (val entry : entries) {
            entry.actionPerformed(button);
        }

        val removed = entries.stream()
                             .filter(e -> e.removed)
                             .collect(Collectors.toList());

        for (val removedEntry : removed) {
            owner.removeButton(removedEntry.remove);
            owner.removeButton(removedEntry.increaseRadius);
            owner.removeButton(removedEntry.decreaseRadius);
            entries.remove(removedEntry);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
        for (val entry : entries) {
            if (focused != entry.biome) {
                entry.biome.setFocused(false);
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseEvent);
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (focused != null && focused instanceof GuiTextField) {
            ((GuiTextField) focused).textboxKeyTyped(typedChar, keyCode);
        }
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

    public void postDraw() {
        entries.forEach(Entry::postDraw);
    }

    public void addEntry() {
        entries.add(new Entry(new HCIslandChunkGeneratorSettings.IslandRadialBiome.Factory(3, false, "minecraft:forest")));
    }

    public class Entry implements IGuiListEntry, GuiPageButtonList.GuiResponder {
        @Getter private HCIslandChunkGeneratorSettings.IslandRadialBiome.Factory info;
        private String biomeName = null;
        private boolean removed = false;

        private final GuiButton remove;
        private final GuiButton increaseRadius;
        private final GuiSlider radius;
        private final GuiButton decreaseRadius;
        private final GuiButton spawn;
        private final GuiTextField biome;

        public Entry(HCIslandChunkGeneratorSettings.IslandRadialBiome.Factory info) {
            this.info = info;

            remove = addButton(new GuiButtonExt(idCounter++,
                                                0,
                                                ENTRY_CONTROL_OFFSET,
                                                SMALL_BUTTON_SIZE,
                                                SMALL_BUTTON_SIZE,
                                                "X"));

            spawn = addButton(new GuiButtonExt(idCounter++,
                                               0,
                                               ENTRY_CONTROL_OFFSET,
                                               70,
                                               SMALL_BUTTON_SIZE,
                                               "Spawn: Nope.avi"));
            setSpawnDisplayString();

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
                                   (id, name, value) -> String.valueOf(Math.round(value)));
            radius.width = RADIUS_SLIDER_WIDTH;
            radius.height = SMALL_BUTTON_SIZE;

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
            biome.setGuiResponder(this);

            biome.setText(info.getBiomeId());
            setEntryValue(biome.getId(), info.getBiomeId());

            radius.setSliderValue(info.getRadius(), false);
        }

        @Override
        public void setEntryValue(int id, boolean value) {
        }

        @Override
        public void setEntryValue(int id, String value) {
            if (id == biome.getId()) {
                info.setBiomeId(value);
                biomeName = null;

                val biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(value));
                if (biome != null) {
                    biomeName = biome.getBiomeName();
                }
            }
        }

        @Override
        public void setEntryValue(int id, float value) {
            if (id == radius.id) {
                info.setRadius(Math.round(value));
            }
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
            }

            if (increaseRadius.mousePressed(mc, mouseX, mouseY)) {
                newFocused = increaseRadius;
            }

            if (radius.mousePressed(mc, mouseX, mouseY)) {
                newFocused = radius;
            }

            if (decreaseRadius.mousePressed(mc, mouseX, mouseY)) {
                newFocused = decreaseRadius;
            }

            if (spawn.mousePressed(mc, mouseX, mouseY)) {
                newFocused = spawn;
            }

            if (biome.mouseClicked(mouseX, mouseY, mouseEvent)) {
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
            spawn.mouseReleased(mouseX, mouseY);
        }

        @Override
        public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
            val controlY = y + ENTRY_CONTROL_OFFSET;
            remove.y = increaseRadius.y = radius.y = decreaseRadius.y = biome.y = spawn.y = controlY;

            remove.x = x + getListWidth() - SMALL_BUTTON_SIZE - BUTTON_MARGIN - 8;
            decreaseRadius.x = x + 18;
            radius.x = x + 18 + SMALL_BUTTON_SIZE + BUTTON_MARGIN;
            increaseRadius.x = x + 18 + SMALL_BUTTON_SIZE + 2 * BUTTON_MARGIN + radius.width;
            spawn.x = x + 18 + 2 * SMALL_BUTTON_SIZE + 3 * BUTTON_MARGIN + radius.width;
            biome.x = x + 18 + 2 * SMALL_BUTTON_SIZE + 4 * BUTTON_MARGIN + radius.width + spawn.width;
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
            spawn.drawButton(mc, mouseX, mouseY, partialTicks);
            biome.drawTextBox();

            // FIXME: Workaround to avoid overdraw when buttonList is iterated in owner
            remove.visible = false;
            increaseRadius.visible = false;
            decreaseRadius.visible = false;
            spawn.visible = false;
        }

        // FIXME: Workaround to avoid overdraw when buttonList is iterated in owner
        public void postDraw() {
            remove.visible = true;
            increaseRadius.visible = true;
            decreaseRadius.visible = true;
            spawn.visible = true;
        }

        private void drawSelectionBox(int x, int y, int listWidth, boolean isSelected) {
            if (isSelected) {
                Gui.drawRect(x, y, x + listWidth - 6, y + 32, -1601138544);
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

        public void actionPerformed(GuiButton button) {
            if (button.id == increaseRadius.id) {
                radius.setSliderValue(radius.getSliderValue() + 1, true);
            } else if (button.id == decreaseRadius.id) {
                radius.setSliderValue(radius.getSliderValue() - 1, true);
            } else if (button.id == remove.id) {
                removed = true;
            } else if (button.id == spawn.id) {
                toggleSpawn();
            }
        }

        private void toggleSpawn() {
            info.setSpawn(!info.isSpawn());
            setSpawnDisplayString();
        }

        private void setSpawnDisplayString() {
            val selected = info.isSpawn()
                    ? I18n.format("gui.yes")
                    : I18n.format("gui.no");
            spawn.displayString = I18n.format("createWorld.customize.hcisland.radial.spawn", selected);
        }
    }
}
