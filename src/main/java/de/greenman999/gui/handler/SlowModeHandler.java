package de.greenman999.gui.handler;

import de.greenman999.LibrarianTradeFinder;
import de.greenman999.gui.screens.ControlUi;
import de.greenman999.gui.screens.NumberFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class SlowModeHandler {

    public static final NumberFieldWidget placeDelay = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 28, 14, Text.translatable("tradefinderui.options.slow-mode.place-delay.name"));
    public static final NumberFieldWidget interactDelay = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 28, 14, Text.translatable("tradefinderui.options.slow-mode.interact-delay.name"));

    static {
        placeDelay.setMaxLength(3);
        interactDelay.setMaxLength(3);
        placeDelay.setText("2");
        placeDelay.setMinValue(2);
        placeDelay.setMaxValue(999);
        placeDelay.setDefaultValue(2);
        placeDelay.setLastText("2");
        interactDelay.setText("2");
        interactDelay.setMinValue(2);
        interactDelay.setMaxValue(999);
        interactDelay.setDefaultValue(2);
        interactDelay.setLastText("2");
    }

    public ControlUi ui;

    public SlowModeHandler(ControlUi ui) {
        this.ui = ui;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta, int screenWidth, int screenHeight) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int ticksWidth = textRenderer.getWidth("ticks");
        int buttonWidth = screenWidth / 2 - 10;
        int x = screenWidth / 2 + 6;
        placeDelay.setX(x + buttonWidth - placeDelay.getWidth() - 10 - ticksWidth);
        placeDelay.setY(125 + ((20 - placeDelay.getHeight()) / 2));
        interactDelay.setX(x + buttonWidth - interactDelay.getWidth() - 10 - ticksWidth);
        interactDelay.setY(150 + ((20 - interactDelay.getHeight()) / 2));

        if (LibrarianTradeFinder.getConfig().slowMode) {
            context.fill(x, 125, x + buttonWidth, 125 + 20, 0x1AC7C0C0);
            placeDelay.checkValue();
            placeDelay.setVisible(true);
            placeDelay.setTextRenderer(textRenderer);
            placeDelay.renderWidget(context, mouseX, mouseY, delta);
            context.drawTextWithShadow(textRenderer, Text.translatable("tradefinderui.options.slow-mode.place-delay.tooltip"), x + 5, 125 + ((20 - textRenderer.fontHeight) / 2) + 1,0xFFFFFF);
            context.drawTextWithShadow(textRenderer,"ticks", placeDelay.getX() + placeDelay.getWidth() + 5, 125 + ((20 - textRenderer.fontHeight) / 2) + 1,0xFFFFFF);

            context.fill(x, 150, x + buttonWidth, 150 + 20, 0x1AC7C0C0);
            interactDelay.checkValue();
            interactDelay.setVisible(true);
            interactDelay.setTextRenderer(textRenderer);
            interactDelay.renderWidget(context, mouseX, mouseY, delta);
            context.drawTextWithShadow(textRenderer, Text.translatable("tradefinderui.options.slow-mode.interact-delay.tooltip"), x + 5, 150 + ((20 - textRenderer.fontHeight) / 2) + 1,0xFFFFFF);
            context.drawTextWithShadow(textRenderer,"ticks", interactDelay.getX() + interactDelay.getWidth() + 5, 150 + ((20 - textRenderer.fontHeight) / 2) + 1,0xFFFFFF);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!LibrarianTradeFinder.getConfig().slowMode) return false;

        boolean fieldSuccess_1 = placeDelay.mouseClicked(mouseX, mouseY, button);
        boolean fieldSuccess_2 = interactDelay.mouseClicked(mouseX, mouseY, button);
        placeDelay.setFocused(fieldSuccess_1);
        interactDelay.setFocused(fieldSuccess_2);
        return fieldSuccess_1 || fieldSuccess_2;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!LibrarianTradeFinder.getConfig().slowMode) return false;

        boolean a = placeDelay.mouseReleased(mouseX, mouseY, button), b = interactDelay.mouseReleased(mouseX, mouseY, button);
        return a || b;
    }

    public void mouseMoved(double mouseX, double mouseY) {
        if (LibrarianTradeFinder.getConfig().slowMode) {
            placeDelay.mouseMoved(mouseX, mouseY);
            interactDelay.mouseMoved(mouseX, mouseY);
        }
    }

    public boolean charTyped(char chr, int modifiers) {
        if (!LibrarianTradeFinder.getConfig().slowMode) return false;

        boolean a = placeDelay.charTyped(chr, modifiers), b = interactDelay.charTyped(chr, modifiers);
        return a || b;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!LibrarianTradeFinder.getConfig().slowMode) return false;

        boolean a = placeDelay.keyPressed(keyCode, scanCode, modifiers), b = interactDelay.keyPressed(keyCode, scanCode, modifiers);
        return a || b;
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!LibrarianTradeFinder.getConfig().slowMode) return false;
        boolean a = placeDelay.keyReleased(keyCode, scanCode, modifiers), b = interactDelay.keyReleased(keyCode, scanCode, modifiers);
        return a || b;
    }

    public static void checkValueWhenClose() {
        placeDelay.checkValueWhenClose();
        interactDelay.checkValueWhenClose();
    }
}
