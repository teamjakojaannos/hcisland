package jakojaannos.hcisland.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public abstract class GuiCustomizeWithDefaults<TSettings> extends GuiScreen {
    protected static final int ID_BUTTON_DONE = 0;
    protected static final int ID_BUTTON_DEFAULTS = 1;
    protected static final int ID_BUTTON_CONFIRM = 2;
    protected static final int ID_BUTTON_CANCEL = 3;

    protected String title = "Customize screen with defaults";
    protected String subtitle = "This is a subtitle";

    protected TSettings settings;
    protected TSettings defaultSettings;

    protected GuiButton done;
    protected GuiButton defaults;

    protected GuiButton confirm;
    protected GuiButton cancel;

    protected boolean dirty;
    protected int confirmMode;
    protected boolean confirmDismissed;

    @Override
    public void initGui() {
        buttonList.clear();

        done = addButton(new GuiButton(ID_BUTTON_DONE, width / 2 + 98, height - 27, 90, 20, I18n.format("gui.done")));
        defaults = addButton(new GuiButton(ID_BUTTON_DEFAULTS, width / 2 - 187, height - 27, 90, 20, I18n.format("createWorld.customize.custom.defaults")));
        defaults.enabled = dirty;

        confirm = new GuiButton(ID_BUTTON_CONFIRM, width / 2 - 55, 160, 50, 20, I18n.format("gui.yes"));
        confirm.visible = false;

        cancel = new GuiButton(ID_BUTTON_CANCEL, width / 2 + 5, 160, 50, 20, I18n.format("gui.no"));
        cancel.visible = false;

        buttonList.add(confirm);
        buttonList.add(cancel);

        if (confirmMode != 0) {
            confirm.visible = true;
            cancel.visible = true;
        }

        setSettingsModified(!settings.equals(defaultSettings));
    }

    private void setSettingsModified(boolean modified) {
        dirty = modified;
        defaults.enabled = modified;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled) {
            return;
        }

        switch (button.id) {
            case ID_BUTTON_DONE:
                onDonePressed();
                break;
            case ID_BUTTON_CONFIRM:
                exitConfirmation();
                break;
            case ID_BUTTON_CANCEL:
                confirmMode = 0;
                exitConfirmation();
                break;
            case ID_BUTTON_DEFAULTS:
                if (dirty) {
                    enterConfirmation(ID_BUTTON_DEFAULTS);
                }
                break;
        }
    }

    protected abstract void onDonePressed();

    private void enterConfirmation(int mode) {
        confirmMode = mode;
        setConfirmationControls(true);
    }

    private void exitConfirmation() throws IOException {
        switch (confirmMode) {
            case ID_BUTTON_DONE:
                //actionPerformed((GuiButton) pages.getComponent(ID_BUTTON_DONE));
                break;
            case ID_BUTTON_DEFAULTS:
                restoreDefaults();
                break;
        }

        confirmMode = 0;
        confirmDismissed = true;
        setConfirmationControls(false);
    }

    protected abstract void restoreDefaults();

    protected void setConfirmationControls(boolean visible) {
        confirm.visible = visible;
        cancel.visible = visible;

        done.enabled = !visible;
        defaults.enabled = dirty && !visible;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawTitles(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (confirmMode != 0) {
            drawRect(0, 0, width, height, 0x80000000);
            drawHorizontalLine(width / 2 - 91, width / 2 + 90, 99, -2039584);
            drawHorizontalLine(width / 2 - 91, width / 2 + 90, 185, -6250336);
            drawVerticalLine(width / 2 - 91, 99, 185, -2039584);
            drawVerticalLine(width / 2 + 90, 99, 185, -6250336);

            GlStateManager.disableLighting();
            GlStateManager.disableFog();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            mc.getTextureManager().bindTexture(OPTIONS_BACKGROUND);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double) (width / 2 - 90), 185.0D, 0.0D).tex(0.0D, 2.65625D).color(64, 64, 64, 64).endVertex();
            bufferbuilder.pos((double) (width / 2 + 90), 185.0D, 0.0D).tex(5.625D, 2.65625D).color(64, 64, 64, 64).endVertex();
            bufferbuilder.pos((double) (width / 2 + 90), 100.0D, 0.0D).tex(5.625D, 0.0D).color(64, 64, 64, 64).endVertex();
            bufferbuilder.pos((double) (width / 2 - 90), 100.0D, 0.0D).tex(0.0D, 0.0D).color(64, 64, 64, 64).endVertex();
            tessellator.draw();

            drawCenteredString(fontRenderer, I18n.format("createWorld.customize.custom.confirmTitle"), width / 2, 105, 16777215);
            drawCenteredString(fontRenderer, I18n.format("createWorld.customize.custom.confirm1"), width / 2, 125, 16777215);
            drawCenteredString(fontRenderer, I18n.format("createWorld.customize.custom.confirm2"), width / 2, 135, 16777215);
            confirm.drawButton(mc, mouseX, mouseY, partialTicks);
            cancel.drawButton(mc, mouseX, mouseY, partialTicks);
        }
    }

    protected void drawTitles(int mouseX, int mouseY, float partialTicks) {
        drawCenteredString(fontRenderer, title, width / 2, 2, 16777215);
        drawCenteredString(fontRenderer, subtitle, width / 2, 12, 16777215);
    }
}
