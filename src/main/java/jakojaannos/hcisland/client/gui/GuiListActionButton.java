package jakojaannos.hcisland.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class GuiListActionButton extends GuiButton {
    private final Action action;

    public GuiListActionButton(int id, int x, int y, String localizationString, Action action) {
        super(id, x, y, 150, 20, "");
        this.action = action;
        this.displayString = I18n.format(localizationString);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            action.exec();
            return true;
        }

        return false;
    }

    public interface Action {
        void exec();
    }
}
