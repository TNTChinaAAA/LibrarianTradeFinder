package de.greenman999.screens;

import de.greenman999.LibrarianTradeFinder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ResetLecternModeHandler {

    public static final NumberFieldWidget delay = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 28, 14, Text.translatable("tradefinderui.options.reset-lectern-mode.delay.name"));

    public ControlUi ui;

    static {
        delay.setMaxLength(3);
        delay.setText("83");
        delay.setMinValue(83);
        delay.setMaxValue(999);
        delay.setDefaultValue(83);
        delay.setLastText("83");
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
        delay.setX(x + buttonWidth - delay.getWidth() - 10 - ticksWidth);
        delay.setY(y + ((20 - delay.getHeight()) / 2));

        if (LibrarianTradeFinder.getConfig().resetLecternMode) {
            context.fill(x, y, x + buttonWidth, y + 20, 0x1AC7C0C0);
            delay.checkValue();
            delay.setVisible(true);
            delay.setTextRenderer(textRenderer);
            delay.renderWidget(context, mouseX, mouseY, delta);
            context.drawTextWithShadow(textRenderer, Text.translatable("tradefinderui.options.set-delay-for-reset-lectern-mode"), x + 5, y + ((20 - textRenderer.fontHeight) / 2) + 1,0xFFFFFF);
            context.drawTextWithShadow(textRenderer,"ticks", delay.getX() + delay.getWidth() + 5, y + ((20 - textRenderer.fontHeight) / 2) + 1,0xFFFFFF);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!LibrarianTradeFinder.getConfig().resetLecternMode) return false;

        boolean fieldSuccess = delay.mouseClicked(mouseX, mouseY, button);
        delay.setFocused(fieldSuccess);
        return fieldSuccess;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!LibrarianTradeFinder.getConfig().resetLecternMode) return false;

        return delay.mouseReleased(mouseX, mouseY, button);
    }

    public void mouseMoved(double mouseX, double mouseY) {
        if (LibrarianTradeFinder.getConfig().resetLecternMode) {
            delay.mouseMoved(mouseX, mouseY);
        }
    }

    public boolean charTyped(char chr, int modifiers) {
        if (!LibrarianTradeFinder.getConfig().resetLecternMode) return false;

        return delay.charTyped(chr, modifiers);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!LibrarianTradeFinder.getConfig().resetLecternMode) return false;

        return delay.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!LibrarianTradeFinder.getConfig().resetLecternMode) return false;
        return delay.keyReleased(keyCode, scanCode, modifiers);
    }
}
