package jakojaannos.hcisland.client.gui;

import com.google.common.primitives.Floats;
import lombok.val;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public abstract class GuiCustomizeWithDefaults<TSettings> extends GuiScreen implements GuiPageButtonList.GuiResponder {
    protected String title = "Customize screen with defaults";
    protected String subtitle = "This is a subtitle";
    protected String pageTitle = "Basic settings";
    private String[] pageNames;

    protected TSettings settings;
    protected TSettings defaultSettings;

    protected GuiButton done;
    protected GuiButton defaults;

    protected GuiButton confirm;
    protected GuiButton cancel;

    protected ExtendedGuiPageButtonList pages;
    protected GuiButton previousPage;
    protected GuiButton nextPage;

    protected boolean dirty;
    protected int confirmMode;
    protected boolean confirmDismissed;

    protected int idCounter;

    @Override
    public void initGui() {
        buttonList.clear();

        done = addButton(new GuiButton(idCounter++, width / 2 + 98, height - 27, 90, 20, I18n.format("gui.done")));
        defaults = addButton(new GuiButton(idCounter++, width / 2 - 187, height - 27, 90, 20, I18n.format("createWorld.customize.custom.defaults")));
        defaults.enabled = dirty;

        confirm = addButton(new GuiButton(idCounter++, width / 2 - 55, 160, 50, 20, I18n.format("gui.yes")));
        confirm.visible = false;

        cancel = addButton(new GuiButton(idCounter++, width / 2 + 5, 160, 50, 20, I18n.format("gui.no")));
        cancel.visible = false;

        previousPage = addButton(new GuiButton(idCounter++, 20, 5, 80, 20, I18n.format("createWorld.customize.custom.prev")));
        nextPage = addButton(new GuiButton(idCounter++, width - 100, 5, 80, 20, I18n.format("createWorld.customize.custom.next")));

        if (confirmMode != 0) {
            confirm.visible = true;
            cancel.visible = true;
        }

        createButtons();

        createPagedList();
        setSettingsModified(!settings.equals(defaultSettings));

        val page = pages != null ? pages.getPage() : 0;
        if (page != 0) {
            val scroll = pages != null ? pages.getAmountScrolled() : 0;
            pages.setPage(page);
            pages.scrollBy(scroll);
            updatePageControls();
        }
    }

    protected abstract void createButtons();

    protected abstract GuiPageButtonList.GuiListEntry[][] getPages();

    protected abstract String[] updatePageNames();

    protected abstract void onDonePressed();

    protected abstract String getFormattedValue(int id, float value);

    protected void updatePageControls() {
        if (pages.getPageCount() > 1) {
            subtitle = I18n.format("book.pageIndicator", pages.getPage() + 1, pages.getPageCount());
        } else {
            previousPage.visible = false;
            nextPage.visible = false;
            subtitle = "";
        }

        previousPage.enabled = pages.getPage() != 0;
        nextPage.enabled = pages.getPage() != pages.getPageCount() - 1;
        pageTitle = pageNames[pages.getPage()];
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        pages.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled) {
            return;
        }

        if (button.id == done.id) {
            onDonePressed();
        } else if (button.id == nextPage.id) {
            pages.nextPage();
            updatePageControls();
        } else if (button.id == previousPage.id) {
            pages.previousPage();
            updatePageControls();
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
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (confirmMode == 0 && !confirmDismissed) {
            pages.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        if (confirmDismissed) {
            confirmDismissed = false;
        } else if (confirmMode == 0) {
            pages.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        // Modifies text field float values using up/down arrow keys and modifier keys
        if (confirmMode == 0) {
            switch (keyCode) {
                case 200:
                    modifyFocusValue(1.0f);
                    break;
                case 208:
                    modifyFocusValue(-1.0f);
                    break;
                default:
                    pages.onKeyPressed(typedChar, keyCode);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        pages.drawScreen(mouseX, mouseY, partialTicks);

        drawCenteredString(fontRenderer, title, width / 2, 2, 16777215);
        drawCenteredString(fontRenderer, subtitle, width / 2, 12, 16777215);
        drawCenteredString(fontRenderer, pageTitle, width / 2, 22, 16777215);
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

    protected void setSettingsModified(boolean modified) {
        dirty = modified;
        defaults.enabled = modified;
    }

    protected void restoreDefaults() {
        createPagedList();
        setSettingsModified(false);
    }

    protected void setConfirmationControls(boolean visible) {
        confirm.visible = visible;
        cancel.visible = visible;
        previousPage.enabled = !visible;
        nextPage.enabled = !visible;

        done.enabled = !visible;
        defaults.enabled = dirty && !visible;

        pages.setActive(!visible);
    }

    private void createPagedList() {
        pages = new ExtendedGuiPageButtonList(
                mc,
                width,
                height,
                32,
                height - 32,
                25,
                this,
                getPages());

        pageNames = updatePageNames();
        updatePageControls();
    }

    private void modifyFocusValue(float multiplier) {
        Gui gui = pages.getFocusedControl();

        if (gui instanceof GuiTextField) {
            float modifier = multiplier;

            if (GuiScreen.isShiftKeyDown()) {
                modifier *= 0.1f;

                if (GuiScreen.isCtrlKeyDown()) {
                    modifier *= 0.1f;
                }
            } else if (isCtrlKeyDown()) {
                modifier *= 10.0f;

                if (GuiScreen.isAltKeyDown()) {
                    modifier *= 10.0f;
                }
            }

            GuiTextField textField = (GuiTextField) gui;
            Float value = Floats.tryParse(textField.getText());

            if (value != null) {
                value += modifier;
                int id = textField.getId();
                String formatted = getFormattedValue(id, value);
                textField.setText(formatted);
                setEntryValue(id, formatted);
            }
        }
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
}
