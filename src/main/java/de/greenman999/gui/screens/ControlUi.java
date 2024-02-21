package de.greenman999.gui.screens;

import de.greenman999.LibrarianTradeFinder;
import de.greenman999.gui.handler.GrayButtonHandler;
import de.greenman999.gui.handler.ResetLecternModeHandler;
import de.greenman999.gui.handler.SlowModeHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ControlUi extends Screen {

    public final Screen parent;

    public EnchantmentsListWidget enchantmentsListWidget;

    public SlowModeHandler slowModeHandler;

    public ResetLecternModeHandler resetLecternModeHandler;

    public GrayButtonHandler grayButtonHandler;

    public ControlUi(Screen parent) {
        super(Text.translatable("tradefinderui.screen.title"));
        this.parent = parent;
        this.slowModeHandler = new SlowModeHandler(this);
        this.resetLecternModeHandler = new ResetLecternModeHandler(this);
        this.grayButtonHandler = new GrayButtonHandler();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawVerticalLine(this.width / 2, 4, this.height - 5, 0xFFC7C0C0);
        super.renderBackground(context, mouseX, mouseY, delta);
        this.grayButtonHandler.render(this, context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.slowModeHandler.render(context, mouseX, mouseY, delta, this.width, this.height);
        this.resetLecternModeHandler.render(context, mouseX, mouseY, delta, this.width, this.height);
    }

    @Override
    protected void init() {
        this.grayButtonHandler.init(this);
        super.init();
    }

    public <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        return super.addDrawableChild(drawableElement);
    }

    public void updateButtonTexts() {
        for(Element element : this.children()) {
            if(!(element instanceof GrayButtonWidget buttonWidget)) continue;
            switch (buttonWidget.getId()) {
                case 2 ->
                        buttonWidget.setMessage(getButtonText("tradefinderui.options.tp-to-villager", LibrarianTradeFinder.getConfig().tpToVillager));
                case 3 ->
                        buttonWidget.setMessage(getButtonText("tradefinderui.options.prevent-axe-break", LibrarianTradeFinder.getConfig().preventAxeBreaking));
                case 4 ->
                        buttonWidget.setMessage(getButtonText("tradefinderui.options.legit-mode", LibrarianTradeFinder.getConfig().legitMode));
                case 5 ->
                        buttonWidget.setMessage(getButtonText("tradefinderui.options.slow-mode", LibrarianTradeFinder.getConfig().slowMode));
                case 6 ->
                        buttonWidget.setMessage(getButtonText("tradefinderui.options.reset-lectern-mode", LibrarianTradeFinder.getConfig().resetLecternMode));
                case 7 ->
                        buttonWidget.setMessage(getButtonText("tradefinderui.options.auto-trade-mode", LibrarianTradeFinder.getConfig().autoTradeMode));
                case 8 ->
                        buttonWidget.setMessage(Text.translatable("tradefinderui.options.reset-all-params-for-bug"));

            }
        }
    }

    public GrayButtonWidget getGrayButtonWidget(int id) {
        for(Element element : this.children()) {
            if (!(element instanceof GrayButtonWidget buttonWidget)) continue;
            if (id == buttonWidget.getId()) return buttonWidget;
        }

        return null;
    }

    public Text getButtonText(String key, boolean enabled) {
        return Text.translatable(key, (enabled ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled"));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else {
            return this.slowModeHandler.keyPressed(keyCode, scanCode, modifiers) || enchantmentsListWidget.keyPressed(keyCode, scanCode, modifiers) || this.resetLecternModeHandler.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(super.keyReleased(keyCode, scanCode, modifiers)) {
            return true;
        } else {
            return this.slowModeHandler.keyReleased(keyCode, scanCode, modifiers) || this.resetLecternModeHandler.keyReleased(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.slowModeHandler.charTyped(chr, modifiers) || this.resetLecternModeHandler.charTyped(chr, modifiers) || enchantmentsListWidget.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(EnchantmentEntry enchantmentEntry : enchantmentsListWidget.children()) {
            boolean maxPriceFieldSuccess = enchantmentEntry.maxPriceField.mouseClicked(mouseX, mouseY, button);
            boolean levelFieldSuccess = enchantmentEntry.levelField.mouseClicked(mouseX, mouseY, button);
            enchantmentEntry.maxPriceField.setFocused(maxPriceFieldSuccess);
            enchantmentEntry.levelField.setFocused(levelFieldSuccess);
        }

        boolean slowModeFieldSuccess = this.slowModeHandler.mouseClicked(mouseX, mouseY, button);
        boolean resetLecternModeFieldSuccess = this.resetLecternModeHandler.mouseClicked(mouseX, mouseY, button);
        return slowModeFieldSuccess || resetLecternModeFieldSuccess || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean a = this.slowModeHandler.mouseReleased(mouseX, mouseY, button);
        boolean b = this.resetLecternModeHandler.mouseReleased(mouseX, mouseY, button);

        return a || b || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.slowModeHandler.mouseMoved(mouseX, mouseY);
        this.resetLecternModeHandler.mouseMoved(mouseX, mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for(EnchantmentEntry enchantmentEntry : enchantmentsListWidget.children()) {
            if (enchantmentEntry.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void close() {
        this.save();
        super.close();
    }

    public void save() {
        for(EnchantmentEntry enchantmentEntry : enchantmentsListWidget.children()) {
            enchantmentEntry.levelField.checkValueWhenClose();
            enchantmentEntry.maxPriceField.checkValueWhenClose();
        }

        SlowModeHandler.checkValueWhenClose();
        ResetLecternModeHandler.checkValueWhenClose();
        LibrarianTradeFinder.getConfig().save();
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }
}
