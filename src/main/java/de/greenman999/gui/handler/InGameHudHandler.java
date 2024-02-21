package de.greenman999.gui.handler;

import de.greenman999.TradeFinder;
import de.greenman999.TradeState;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.List;

public class InGameHudHandler {

    private static Text lastText = null;

    public static void render(WorldRenderContext context) {
        if (TradeFinder.state == TradeState.IDLE) {
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        Text a = null;
        Text checkText = Text.translatable("librarian-trade-finder.actionbar.status.check", TradeFinder.tries).formatted(Formatting.GRAY);
        Text waitText = Text.translatable("librarian-trade-finder.actionbar.status.wait", TradeFinder.tries).formatted(Formatting.GRAY);
        Text breakText = Text.translatable("librarian-trade-finder.actionbar.status.break", TradeFinder.tries).formatted(Formatting.GRAY);
        Text placeText = Text.translatable("librarian-trade-finder.actionbar.status.place", TradeFinder.tries).formatted(Formatting.GRAY);

        switch (TradeFinder.state) {
            case CHECK, AIM -> a = checkText;
            case WAITING_FOR_PACKET -> a = waitText;
            case BREAK -> a = breakText;
            case PLACE -> a = placeText;
        }

        removeLastTextFromInGameHud();

        if (isInGameHudNull()) {
            mc.inGameHud.setOverlayMessage(a, false);
        } else {
            mc.inGameHud.setOverlayMessage(mc.inGameHud.overlayMessage.copy().append("\n").append(a).formatted(Formatting.GRAY), false);
        }

        lastText = a;
    }

    public static boolean isInGameHudNull() {
        MinecraftClient mc = MinecraftClient.getInstance();
        Text overlayMessage = mc.inGameHud.overlayMessage;

        if (overlayMessage == null) {
            return true;
        } else if (overlayMessage.getLiteralString() == null) {
            return true;
        } else if (overlayMessage.getLiteralString().equals("")) {
            return true;
        }

        return false;
    }

    public static void removeLastTextFromInGameHud() {
        Text overlayMessage = MinecraftClient.getInstance().inGameHud.overlayMessage;

        if (overlayMessage != null) {

            List<Text> siblings = overlayMessage.getSiblings();

            if (siblings != null) {
                try {
                    siblings.remove(lastText);
                } catch (UnsupportedOperationException e) {
                    overlayMessage = overlayMessage.copy();
                    overlayMessage.getSiblings().remove(lastText);
                    MinecraftClient.getInstance().inGameHud.overlayMessage = overlayMessage;
                }
            }
        }
    }
}
