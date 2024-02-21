package de.greenman999.gui.handler;

import de.greenman999.LibrarianTradeFinder;
import de.greenman999.gui.screens.ControlUi;
import de.greenman999.gui.screens.NumberFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ResetLecternModeHandler {

    public static final NumberFieldWidget resetDelay = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 28, 14, Text.translatable("tradefinderui.options.reset-lectern-mode.delay.name"));

    public ControlUi ui;

    static {
        resetDelay.setMaxLength(3);
        resetDelay.setText("85");
        resetDelay.setMinValue(85);
        resetDelay.setMaxValue(999);
        resetDelay.setDefaultValue(85);
        resetDelay.setLastText("85");
    }

    public ResetLecternModeHandler(ControlUi ui) {
        this.ui = ui;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta, int screenWidth, int screenHeight) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int ticksWidth = textRenderer.getWidth("ticks");
        int buttonWidth = screenWidth / 2 - 10;
        int x = screenWidth / 2 + 6;
        int y = this.ui.getGrayButtonWidget(6).getBottom() + 5;
        resetDelay.setX(x + buttonWidth - resetDelay.getWidth() - 10 - ticksWidth);
        resetDelay.setY(y + ((20 - resetDelay.getHeight()) / 2));

        if (LibrarianTradeFinder.getConfig().resetLecternMode) {
            context.fill(x, y, x + buttonWidth, y + 20, 0x1AC7C0C0);
            resetDelay.checkValue();
            resetDelay.setVisible(true);
            resetDelay.setTextRenderer(textRenderer);
            resetDelay.renderWidget(context, mouseX, mouseY, delta);
            context.drawTextWithShadow(textRenderer, Text.translatable("tradefinderui.options.set-delay-for-reset-lectern-mode"), x + 5, y + ((20 - textRenderer.fontHeight) / 2) + 1,0xFFFFFF);
            context.drawTextWithShadow(textRenderer,"ticks", resetDelay.getX() + resetDelay.getWidth() + 5, y + ((20 - textRenderer.fontHeight) / 2) + 1,0xFFFFFF);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!LibrarianTradeFinder.getConfig().resetLecternMode) return false;

        boolean fieldSuccess = resetDelay.mouseClicked(mouseX, mouseY, button);
        resetDelay.setFocused(fieldSuccess);
        return fieldSuccess;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!LibrarianTradeFinder.getConfig().resetLecternMode) return false;

        return resetDelay.mouseReleased(mouseX, mouseY, button);
    }

    public void mouseMoved(double mouseX, double mouseY) {
        if (LibrarianTradeFinder.getConfig().resetLecternMode) {
            resetDelay.mouseMoved(mouseX, mouseY);
        }
    }

    public boolean charTyped(char chr, int modifiers) {
        if (!LibrarianTradeFinder.getConfig().resetLecternMode) return false;

        return resetDelay.charTyped(chr, modifiers);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!LibrarianTradeFinder.getConfig().resetLecternMode) return false;

        return resetDelay.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!LibrarianTradeFinder.getConfig().resetLecternMode) return false;
        return resetDelay.keyReleased(keyCode, scanCode, modifiers);
    }

    public static void checkValueWhenClose() {
        resetDelay.checkValueWhenClose();
    }
}
