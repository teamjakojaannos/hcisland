package jakojaannos.hcisland.client.gui;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public abstract class GuiCustomizeWithDefaults<TSettings> extends GuiScreen {
    protected String title = "Customize screen with defaults";
    protected String subtitle = "This is a subtitle";

    protected TSettings settings;
    protected TSettings defaultSettings;
    protected int idCounter;

    private GuiButton done;
    private GuiButton defaults;
    private GuiButton confirm;
    private GuiButton cancel;

    private boolean dirty;
    @Getter(AccessLevel.PROTECTED) private int confirmMode;
    @Getter(AccessLevel.PROTECTED) private boolean confirmDismissed;

    protected abstract void onDonePressed();

    @Override
    public void initGui() {
        buttonList.clear();
        createButtons();

        setSettingsModified(!settings.equals(defaultSettings));
    }

    protected void createButtons() {
        done = addButton(new GuiButton(idCounter++, width / 2 + 98, height - 27, 90, 20, I18n.format("gui.done")));
        defaults = addButton(new GuiButton(idCounter++, width / 2 - 187, height - 27, 90, 20, I18n.format("createWorld.customize.custom.defaults")));
        defaults.enabled = dirty;

        confirm = addButton(new GuiButton(idCounter++, width / 2 - 55, 160, 50, 20, I18n.format("gui.yes")));
        confirm.visible = false;

        cancel = addButton(new GuiButton(idCounter++, width / 2 + 5, 160, 50, 20, I18n.format("gui.no")));
        cancel.visible = false;

        if (confirmMode != 0) {
            confirm.visible = true;
            cancel.visible = true;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled) {
            return;
        }

        if (button.id == done.id) {
            onDonePressed();
        } else if (button.id == confirm.id) {
            exitConfirmation();
        } else if (button.id == cancel.id) {
            confirmMode = 0;
            exitConfirmation();
        } else if (button.id == defaults.id) {
            if (dirty) {
                enterConfirmation(defaults.id);
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        // TODO: This is likely copied over from vanilla code. What does it do?
        if (isConfirmDismissed()) {
            confirmDismissed = false;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackgroundLayer(mouseX, mouseY, partialTicks);
        drawTitles(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (confirmMode != 0) {
            drawConfirmDialog(mouseX, mouseY, partialTicks);
        }
    }

    protected void drawConfirmDialog(int mouseX, int mouseY, float partialTicks) {
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

    protected void drawTitles(int mouseX, int mouseY, float partialTicks) {
        drawCenteredString(fontRenderer, title, width / 2, 2, 16777215);
        drawCenteredString(fontRenderer, subtitle, width / 2, 12, 16777215);
    }

    protected void drawBackgroundLayer(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
    }

    protected void setSettingsModified(boolean modified) {
        dirty = modified;

        if (defaults != null) {
            defaults.enabled = modified;
        }
    }

    protected void restoreDefaults() {
        setSettingsModified(false);
    }

    private void enterConfirmation(int mode) {
        confirmMode = mode;
        setConfirmationControls(true);
    }

    private void exitConfirmation() {
        if (confirmMode == done.id) {
            //actionPerformed((GuiButton) pages.getComponent(ID_BUTTON_DONE));
        } else if (confirmMode == defaults.id) {
            restoreDefaults();
        }

        confirmMode = 0;
        confirmDismissed = true;
        setConfirmationControls(false);
    }

    protected void setConfirmationControls(boolean visible) {
        confirm.visible = visible;
        cancel.visible = visible;

        done.enabled = !visible;
        defaults.enabled = dirty && !visible;
    }

    public void removeButton(GuiButton button) {
        buttonList.removeIf(b -> b.id == button.id);
    }
}
