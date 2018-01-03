package jakojaannos.hcisland.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class ExtendedGuiPageButtonList extends GuiPageButtonList {

    public ExtendedGuiPageButtonList(Minecraft mc, int width, int height, int top, int bottom, int slotHeight, GuiResponder responder, GuiListEntry[]... pages) {
        super(mc, width, height, top, bottom, slotHeight, responder, pages);
    }

    @Nullable
    @Override
    protected Gui createEntry(@Nullable GuiListEntry entry, int xOffset, boolean isDoubleWidth) {
        final Gui result = super.createEntry(entry, xOffset, isDoubleWidth);
        if (result != null) {
            return result;
        }

        if (entry instanceof GuiActionButtonEntry) {
            return createActionButton(width / 2 - 155 + xOffset, 0, (GuiActionButtonEntry) entry);
        }

        return null;
    }

    private GuiListActionButton createActionButton(int x, int y, GuiActionButtonEntry entry) {
        GuiListActionButton button = new GuiListActionButton(entry.getId(), x, y, entry.getCaption(), entry.getAction());
        button.visible = entry.shouldStartVisible();
        return button;
    }

    public static class GuiActionButtonEntry extends GuiListEntry {
        private final GuiListActionButton.Action action;

        public GuiActionButtonEntry(int id, String label, boolean startVisible, GuiListActionButton.Action action) {
            super(id, label, startVisible);
            this.action = action;
        }

        private GuiListActionButton.Action getAction() {
            return action;
        }
    }
}
