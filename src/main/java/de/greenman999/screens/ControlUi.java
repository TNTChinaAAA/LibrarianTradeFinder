package de.greenman999.screens;

import de.greenman999.LibrarianTradeFinder;
import de.greenman999.TradeFinder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class ControlUi extends Screen {

    private final Screen parent;
    private EnchantmentsListWidget enchantmentsListWidget;

    public SlowModeHandler slowModeHandler;

    public ResetLecternModeHandler resetLecternModeHandler;

    public ControlUi(Screen parent) {
        super(Text.translatable("tradefinderui.screen.title"));
        this.parent = parent;
        this.slowModeHandler = new SlowModeHandler(this);
        this.resetLecternModeHandler = new ResetLecternModeHandler(this);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawVerticalLine(this.width / 2, 4, this.height - 5, 0xFFC7C0C0);
        super.renderBackground(context, mouseX, mouseY, delta);

        context.fill(this.width / 2 + 6, 5, this.width - 5, 20, 0xAFC7C0C0);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("tradefinderui.options.title"), this.width / 2 + 10, 9, 0xFFFFFF);
        GrayButtonWidget resetLecternMode = this.getGrayButtonWidget(6);
        resetLecternMode.setY(125 + (LibrarianTradeFinder.getConfig().slowMode ? 50 : 0));
        GrayButtonWidget autoTradeMode = this.getGrayButtonWidget(7);
        autoTradeMode.setY(resetLecternMode.getBottom() + 5 + (LibrarianTradeFinder.getConfig().resetLecternMode ? 25 : 0));
        GrayButtonWidget resetAllParams = this.getGrayButtonWidget(8);
        resetAllParams.setY(autoTradeMode.getBottom() + 5);
        super.render(context, mouseX, mouseY, delta);

        this.slowModeHandler.render(context, mouseX, mouseY, delta, this.width, this.height);
        this.resetLecternModeHandler.render(context, mouseX, mouseY, delta, this.width, this.height);
    }

    @Override
    protected void init() {
        this.addDrawableChild(GrayButtonWidget.builder(Text.translatable("tradefinderui.buttons.save"), (buttonWidget) -> {
                    if (this.client != null) {
                        this.client.setScreen(this.parent);
                    }
                    this.save();
                })
                        .dimensions(this.width / 2 + 6, this.height - 25, width / 2 / 2 - 6 - 3, 20)
                        .color(0x4FC7C0C0)
                        .id(0)
                .build());
        this.addDrawableChild(GrayButtonWidget.builder(Text.translatable("tradefinderui.buttons.start").formatted(Formatting.GREEN), (buttonWidget) -> {
                            if((TradeFinder.villager == null || TradeFinder.lecternPos == null) && client != null) {
                                client.inGameHud.getChatHud().addMessage(Text.translatable("commands.tradefinder.start.not-selected").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.RED))));
                                client.setScreen(this.parent);
                            }else {
                                TradeFinder.searchList();
                                if (this.client != null) {
                                    this.client.setScreen(this.parent);
                                }
                                this.save();
                            }
                        })
                .dimensions(this.width / 2 + this.width / 2 / 2 + 3, this.height - 25, width / 2 / 2 - 6, 20)
                .color(0x4FC7C0C0)
                .id(1)
                .build());

        enchantmentsListWidget = new EnchantmentsListWidget(this.client, this.width / 2 - 10, this.height - 30, 25, 20);
        this.addDrawableChild(enchantmentsListWidget);

        this.addDrawableChild(GrayButtonWidget.builder(getButtonText("tradefinderui.options.tp-to-villager", LibrarianTradeFinder.getConfig().tpToVillager), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().tpToVillager = !LibrarianTradeFinder.getConfig().tpToVillager;

                    updateButtonTexts();
                })
                .dimensions(this.width / 2 + 6, 25, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(2)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.tp-to-villager.tooltip")))
                .build());
        this.addDrawableChild(GrayButtonWidget.builder(getButtonText("tradefinderui.options.prevent-axe-break", LibrarianTradeFinder.getConfig().preventAxeBreaking), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().preventAxeBreaking = !LibrarianTradeFinder.getConfig().preventAxeBreaking;

                    updateButtonTexts();
                })
                .dimensions(this.width / 2 + 6 , 50, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(3)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.prevent-axe-break.tooltip")))
                .build());
        this.addDrawableChild(GrayButtonWidget.builder(getButtonText("tradefinderui.options.legit-mode", LibrarianTradeFinder.getConfig().legitMode), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().legitMode = !LibrarianTradeFinder.getConfig().legitMode;
                    if(!LibrarianTradeFinder.getConfig().legitMode) {
                        LibrarianTradeFinder.getConfig().slowMode = false;
                    }

                    updateButtonTexts();
                })
                .dimensions(this.width / 2 + 6, 75, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(4)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.legit-mode.tooltip")))
                .build());
        this.addDrawableChild(GrayButtonWidget.builder(getButtonText("tradefinderui.options.slow-mode", LibrarianTradeFinder.getConfig().slowMode), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().slowMode = !LibrarianTradeFinder.getConfig().slowMode;
                    LibrarianTradeFinder.getConfig().legitMode = LibrarianTradeFinder.getConfig().slowMode || LibrarianTradeFinder.getConfig().legitMode;

                    updateButtonTexts();
                })
                .dimensions(this.width / 2 + 6, 100, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(5)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.slow-mode.tooltip")))
                .build());

        GrayButtonWidget resetLecternMode = GrayButtonWidget.builder(getButtonText("tradefinderui.options.reset-lectern-mode", LibrarianTradeFinder.getConfig().resetLecternMode), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().resetLecternMode = !LibrarianTradeFinder.getConfig().resetLecternMode;

                    updateButtonTexts();
                })
                .dimensions(this.width / 2 + 6, 125 + (LibrarianTradeFinder.getConfig().slowMode ? 50 : 0), this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(6)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.reset-lectern-mode.tooltip"))).
                build();

        this.addDrawableChild(resetLecternMode);

        GrayButtonWidget autoTradeMode = GrayButtonWidget.builder(getButtonText("tradefinderui.options.auto-trade-mode", LibrarianTradeFinder.getConfig().autoTradeMode), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().autoTradeMode = !LibrarianTradeFinder.getConfig().autoTradeMode;

                    updateButtonTexts();
                })
                .dimensions(this.width / 2 + 6, resetLecternMode.getBottom() + 5 + (LibrarianTradeFinder.getConfig().resetLecternMode ? 25 : 0), this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(7)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.auto-trade-mode.tooltip"))).
                build();

        this.addDrawableChild(autoTradeMode);
        this.addDrawableChild(GrayButtonWidget.builder(Text.translatable("tradefinderui.options.reset-all-params-for-bug"), (buttonWidget) -> {
                    TradeFinder.resetAllParams();

                    updateButtonTexts();
                })
                .dimensions(this.width / 2 + 6, autoTradeMode.getBottom() + 5, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(8)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.reset-all-params-for-bug.tooltip"))).
                build());

        /*
        TextWidget textWidget = new TextWidget(this.width / 2 + 6, 150, this.width / 2 - 10, 20, Text.of("鸡巴"), this.client.textRenderer);
        textWidget.setAlpha(20);
        this.addDrawableChild(GrayButtonWidget.builder(Text.translatable("tradefinderui.options.set-delay-for-reset-lectern-mode"), buttonWidget -> {

                })
                .dimensions(this.width / 2 + 6, 150, this.client.textRenderer.getWidth("tradefinderui.options.reset-lectern-mode"), 20)
                .color(0x4FC7C0C0)
                .id(7)
                .build());
        EditBoxWidget editBoxWidget = new EditBoxWidget(this.client.textRenderer, this.width / 2 + 6, 175, this.width / 2 - 10, 20, Text.translatable("tradefinderui.options.set-delay-for-reset-lectern-mode"), Text.of("20"));
        this.addDrawableChild(editBoxWidget);
        */

        super.init();
    }

    private void updateButtonTexts() {
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

        SlowModeHandler.placeDelay.checkValueWhenClose();
        SlowModeHandler.interactDelay.checkValueWhenClose();
        ResetLecternModeHandler.delay.checkValueWhenClose();
        LibrarianTradeFinder.getConfig().save();
    }
}
