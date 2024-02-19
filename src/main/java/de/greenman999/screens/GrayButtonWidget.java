package de.greenman999.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class GrayButtonWidget extends ButtonWidget {
    int color;
    int id;

    protected GrayButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier, int color, int id) {
        super(x, y, width, height, message, onPress, narrationSupplier);
        this.color = color;
        this.id = id;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.setFocused(false);
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        RenderSystem.enableDepthTest();
        matrices.translate(0, 0, 200);
        context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), color);
        int j = this.active ? 16777215 : 10526880;
        context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 7) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        matrices.pop();
        RenderSystem.disableDepthTest();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == InputUtil.GLFW_KEY_ENTER || keyCode == InputUtil.GLFW_KEY_SPACE || keyCode == InputUtil.GLFW_KEY_UP || keyCode == InputUtil.GLFW_KEY_DOWN || keyCode == InputUtil.GLFW_KEY_LEFT || keyCode == InputUtil.GLFW_KEY_RIGHT) {
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public static GrayButtonWidget.Builder builder(Text message, PressAction onPress) {
        return new GrayButtonWidget.Builder(message, onPress);
    }

    public int getId() {
        return id;
    }

    public int getBottom() {
        return this.getY() + this.getHeight();
    }

    public static class Builder extends ButtonWidget.Builder {
        private final Text message;
        private final PressAction onPress;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private NarrationSupplier narrationSupplier;
        private int color;
        private int id;

        public Builder(Text message, PressAction onPress) {
            super(message, onPress);
            this.narrationSupplier = ButtonWidget.DEFAULT_NARRATION_SUPPLIER;
            this.message = message;
            this.onPress = onPress;
        }

        public GrayButtonWidget.Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public GrayButtonWidget.Builder width(int width) {
            this.width = width;
            return this;
        }

        public GrayButtonWidget.Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public GrayButtonWidget.Builder dimensions(int x, int y, int width, int height) {
            return this.position(x, y).size(width, height);
        }

        public GrayButtonWidget.Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public GrayButtonWidget.Builder narrationSupplier(NarrationSupplier narrationSupplier) {
            this.narrationSupplier = narrationSupplier;
            return this;
        }

        public GrayButtonWidget.Builder color(int color) {
            this.color = color;
            return this;
        }

        public GrayButtonWidget.Builder id(int id) {
            this.id = id;
            return this;
        }

        public GrayButtonWidget build() {
            GrayButtonWidget buttonWidget = new GrayButtonWidget(this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier, this.color, this.id);
            buttonWidget.setTooltip(this.tooltip);
            return buttonWidget;
        }
    }
}