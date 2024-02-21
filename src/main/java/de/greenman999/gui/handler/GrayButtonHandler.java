package de.greenman999.gui.handler;

import de.greenman999.LibrarianTradeFinder;
import de.greenman999.TradeFinder;
import de.greenman999.gui.screens.ControlUi;
import de.greenman999.gui.screens.EnchantmentsListWidget;
import de.greenman999.gui.screens.GrayButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

public class GrayButtonHandler {


    public void render(ControlUi ui, DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(ui.width / 2 + 6, 5, ui.width - 5, 20, 0xAFC7C0C0);
        context.drawTextWithShadow(ui.getTextRenderer(), Text.translatable("tradefinderui.options.title"), ui.width / 2 + 10, 9, 0xFFFFFF);
        GrayButtonWidget resetLecternMode = this.getGrayButtonWidget(ui,6);
        resetLecternMode.setY(125 + (LibrarianTradeFinder.getConfig().slowMode ? 50 : 0));
        GrayButtonWidget autoTradeMode = this.getGrayButtonWidget(ui,7);
        autoTradeMode.setY(resetLecternMode.getBottom() + 5 + (LibrarianTradeFinder.getConfig().resetLecternMode ? 25 : 0));
        GrayButtonWidget resetAllParams = this.getGrayButtonWidget(ui,8);
        resetAllParams.setY(autoTradeMode.getBottom() + 5);
    }

    public void init(@NotNull ControlUi ui) {
        MinecraftClient client = MinecraftClient.getInstance();

        ui.addDrawableChild(GrayButtonWidget.builder(Text.translatable("tradefinderui.buttons.save"), (buttonWidget) -> {
                    if (client != null) {
                        client.setScreen(ui.parent);
                    }

                    ui.save();
                })
                .dimensions(ui.width / 2 + 6, ui.height - 25, ui.width / 2 / 2 - 6 - 3, 20)
                .color(0x4FC7C0C0)
                .id(0)
                .build());
        ui.addDrawableChild(GrayButtonWidget.builder(Text.translatable("tradefinderui.buttons.start").formatted(Formatting.GREEN), (buttonWidget) -> {
                    if((TradeFinder.villager == null || TradeFinder.lecternPos == null) && client != null) {
                        client.inGameHud.getChatHud().addMessage(Text.translatable("commands.tradefinder.start.not-selected").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.RED))));
                        client.setScreen(ui.parent);
                    }else {
                        TradeFinder.searchList();
                        if (client != null) {
                            client.setScreen(ui.parent);
                        }

                        ui.save();
                    }
                })
                .dimensions(ui.width / 2 + ui.width / 2 / 2 + 3, ui.height - 25, ui.width / 2 / 2 - 6, 20)
                .color(0x4FC7C0C0)
                .id(1)
                .build());

        ui.enchantmentsListWidget = new EnchantmentsListWidget(client, ui.width / 2 - 10, ui.height - 30, 25, 20);
        ui.addDrawableChild(ui.enchantmentsListWidget);

        ui.addDrawableChild(GrayButtonWidget.builder(ui.getButtonText("tradefinderui.options.tp-to-villager", LibrarianTradeFinder.getConfig().tpToVillager), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().tpToVillager = !LibrarianTradeFinder.getConfig().tpToVillager;

                    ui.updateButtonTexts();
                })
                .dimensions(ui.width / 2 + 6, 25, ui.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(2)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.tp-to-villager.tooltip")))
                .build());
        ui.addDrawableChild(GrayButtonWidget.builder(ui.getButtonText("tradefinderui.options.prevent-axe-break", LibrarianTradeFinder.getConfig().preventAxeBreaking), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().preventAxeBreaking = !LibrarianTradeFinder.getConfig().preventAxeBreaking;

                    ui.updateButtonTexts();
                })
                .dimensions(ui.width / 2 + 6 , 50, ui.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(3)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.prevent-axe-break.tooltip")))
                .build());
        ui.addDrawableChild(GrayButtonWidget.builder(ui.getButtonText("tradefinderui.options.legit-mode", LibrarianTradeFinder.getConfig().legitMode), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().legitMode = !LibrarianTradeFinder.getConfig().legitMode;
                    if(!LibrarianTradeFinder.getConfig().legitMode) {
                        LibrarianTradeFinder.getConfig().slowMode = false;
                    }

                    ui.updateButtonTexts();
                })
                .dimensions(ui.width / 2 + 6, 75, ui.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(4)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.legit-mode.tooltip")))
                .build());
        ui.addDrawableChild(GrayButtonWidget.builder(ui.getButtonText("tradefinderui.options.slow-mode", LibrarianTradeFinder.getConfig().slowMode), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().slowMode = !LibrarianTradeFinder.getConfig().slowMode;
                    LibrarianTradeFinder.getConfig().legitMode = LibrarianTradeFinder.getConfig().slowMode || LibrarianTradeFinder.getConfig().legitMode;

                    ui.updateButtonTexts();
                })
                .dimensions(ui.width / 2 + 6, 100, ui.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(5)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.slow-mode.tooltip")))
                .build());

        GrayButtonWidget resetLecternMode = GrayButtonWidget.builder(ui.getButtonText("tradefinderui.options.reset-lectern-mode", LibrarianTradeFinder.getConfig().resetLecternMode), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().resetLecternMode = !LibrarianTradeFinder.getConfig().resetLecternMode;

                    ui.updateButtonTexts();
                })
                .dimensions(ui.width / 2 + 6, 125 + (LibrarianTradeFinder.getConfig().slowMode ? 50 : 0), ui.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(6)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.reset-lectern-mode.tooltip"))).
                build();

        ui.addDrawableChild(resetLecternMode);

        GrayButtonWidget autoTradeMode = GrayButtonWidget.builder(ui.getButtonText("tradefinderui.options.auto-trade-mode", LibrarianTradeFinder.getConfig().autoTradeMode), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().autoTradeMode = !LibrarianTradeFinder.getConfig().autoTradeMode;

                    ui.updateButtonTexts();
                })
                .dimensions(ui.width / 2 + 6, resetLecternMode.getBottom() + 5 + (LibrarianTradeFinder.getConfig().resetLecternMode ? 25 : 0), ui.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(7)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.auto-trade-mode.tooltip"))).
                build();

        ui.addDrawableChild(autoTradeMode);
        ui.addDrawableChild(GrayButtonWidget.builder(Text.translatable("tradefinderui.options.reset-all-params-for-bug"), (buttonWidget) -> {
                    TradeFinder.resetAllParams();

                    ui.updateButtonTexts();
                })
                .dimensions(ui.width / 2 + 6, autoTradeMode.getBottom() + 5, ui.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(8)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.reset-all-params-for-bug.tooltip"))).
                build());
    }

    public GrayButtonWidget getGrayButtonWidget(ControlUi ui, int id) {
        return ui.getGrayButtonWidget(id);
    }
}
