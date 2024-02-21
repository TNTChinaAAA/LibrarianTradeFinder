package de.greenman999.gui.screens;

import de.greenman999.LibrarianTradeFinder;
import de.greenman999.TradeFinder;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class NumberFieldWidget extends TextFieldWidget {

    public int defaultValue = Integer.MIN_VALUE;

    public int minValue = Integer.MIN_VALUE;

    public int maxValue = Integer.MAX_VALUE;

    public String lastText = "";

    public NumberFieldWidget(TextRenderer textRenderer, int width, int height, Text text) {
        super(textRenderer, width, height, text);
    }

    public NumberFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, (TextFieldWidget)null, text);
    }

    public NumberFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable TextFieldWidget copyFrom, Text text) {
        super(textRenderer, x, y, width, height, copyFrom, text);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!Character.isDigit(chr)) return false;
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(!(keyCode == InputUtil.GLFW_KEY_BACKSPACE || (keyCode >= InputUtil.GLFW_KEY_0 && keyCode <= InputUtil.GLFW_KEY_9) || keyCode == InputUtil.GLFW_KEY_LEFT || keyCode == InputUtil.GLFW_KEY_RIGHT)) return false;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(!(keyCode == InputUtil.GLFW_KEY_BACKSPACE || (keyCode >= InputUtil.GLFW_KEY_0 && keyCode <= InputUtil.GLFW_KEY_9) || keyCode == InputUtil.GLFW_KEY_LEFT || keyCode == InputUtil.GLFW_KEY_RIGHT)) return false;
        return this.isFocused() ? super.keyPressed(keyCode, scanCode, modifiers) : false;
    }

    public void setLastText(String lastText) {
        this.lastText = lastText;
    }

    public String getLastText() {
        return this.lastText;
    }

    public void setTextRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    public int getDefaultValue() {
        return this.defaultValue;
    }

    public int getMinValue() {
        return this.minValue;
    }

    public int getMaxValue() {
        return this.maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setDefaultValue(int defaultValue) {
        if (defaultValue >= this.getMinValue() && defaultValue <= this.getMaxValue()) {
            this.defaultValue = defaultValue;
        }
    }

    public void checkValue() {
        if (this.isFocused() | this.isActive()) {
            return;
        }

        this.checkValueWhenClose();
    }

    public void checkValueWhenClose() {
        if (this.isInvalid(this.getText())) {
            this.setText(this.isInvalid(this.getLastText()) ? String.valueOf(this.getDefaultValue()) : this.getLastText());
            return;
        }

        if (this.getText().startsWith("0") && Integer.parseInt(this.getText()) != 0) this.setText(this.getText().replaceFirst("0", ""));
        this.setLastText(this.getText());
    }

    /*private String getCheckedValue(String str) {
        if (this.isInvalid(str)) {
            return String.valueOf(this.getDefaultValue());
        }

        if (this.getText().startsWith("0") && Integer.parseInt(this.getText()) != 0)
            return str.replaceFirst("0", "");

        return str;
    }*/

    public boolean isInvalid(String str) {
        if (str.equals("") | str.isEmpty()) {
            return true;
        }

        try {
            Integer.parseInt(str);
        } catch (Exception e) {
            return true;
        }

        if (Integer.parseInt(str) == 0) {
            return true;
        }

        if (Integer.parseInt(str) < this.getMinValue() || Integer.parseInt(str) > this.getMaxValue()) {
            return true;
        }

        return false;
    }
}
