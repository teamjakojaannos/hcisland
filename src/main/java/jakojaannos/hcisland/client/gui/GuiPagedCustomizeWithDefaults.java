package jakojaannos.hcisland.client.gui;

import com.google.common.primitives.Floats;
import lombok.val;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public abstract class GuiPagedCustomizeWithDefaults<TSettings> extends GuiCustomizeWithDefaults<TSettings> implements GuiPageButtonList.GuiResponder {
    protected String pageTitle = "Settings Page";
    private String[] pageNames;

    private ExtendedGuiPageButtonList pages;
    private GuiButton previousPage;
    private GuiButton nextPage;

    public GuiPagedCustomizeWithDefaults(Supplier<TSettings> defaultSettingsSupplier, Consumer<TSettings> settingsApplier) {
        super(defaultSettingsSupplier, settingsApplier);
    }

    @Override
    public void initGui() {
        super.initGui();

        createPagedList();

        val page = pages != null ? pages.getPage() : 0;
        if (page != 0) {
            val scroll = pages != null ? pages.getAmountScrolled() : 0;
            pages.setPage(page);
            pages.scrollBy(scroll);
            updatePageControls();
        }
    }

    @Override
    protected void createButtons() {
        super.createButtons();

        previousPage = addButton(new GuiButton(idCounter++, 20, 5, 80, 20, I18n.format("createWorld.customize.custom.prev")));
        nextPage = addButton(new GuiButton(idCounter++, width - 100, 5, 80, 20, I18n.format("createWorld.customize.custom.next")));
    }

    protected abstract GuiPageButtonList.GuiListEntry[][] getPages();

    protected abstract String[] updatePageNames();

    protected abstract String getFormattedValue(int id, float value);

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        pages.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (!button.enabled) {
            return;
        }

        if (button.id == nextPage.id) {
            pages.nextPage();
            updatePageControls();
        } else if (button.id == previousPage.id) {
            pages.previousPage();
            updatePageControls();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (getConfirmMode() == 0 && !isConfirmDismissed()) {
            pages.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        if (!isConfirmDismissed() && getConfirmMode() == 0) {
            pages.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        // Modifies text field float values using up/down arrow keys and modifier keys
        if (getConfirmMode() == 0) {
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
    protected void drawBackgroundLayer(int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundLayer(mouseX, mouseY, partialTicks);
        pages.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawTitles(int mouseX, int mouseY, float partialTicks) {
        super.drawTitles(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRenderer, pageTitle, width / 2, 22, 16777215);
    }

    @Override
    protected void restoreDefaults() {
        super.restoreDefaults();
        createPagedList();
    }

    @Override
    protected void setConfirmationControls(boolean visible) {
        super.setConfirmationControls(visible);
        previousPage.enabled = !visible;
        nextPage.enabled = !visible;

        pages.setActive(!visible);
    }

    private void updatePageControls() {
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
}
