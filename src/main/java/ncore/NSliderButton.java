package ncore;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;
import java.util.function.Consumer;

import static ncore.NCore.mc;

public class NSliderButton extends NButton {
    private final int min, max;
    private double value;
    private final Consumer<Integer> onChange;
    private boolean dragging = false;

    public NSliderButton(int x, int y, int width, int height, String label, int min, int max, int startValue, Consumer<Integer> onChange) {
        super(x, y, width, height, label);
        this.min = min;
        this.max = max;
        this.value = Math.clamp(startValue, min, max);
        this.onChange = onChange;
    }

    @Override
    public void draw(GuiGraphicsExtractor context, double mouseX, double mouseY) {
        roundedFill(context, x, y, width, height, isHovered(mouseX, mouseY) ? 0xFF555555 : 0xFF333333);

        int sliderX = (int) (x + (value - min) / (max - min) * width);
        context.fill(sliderX - 2, y, sliderX + 2, y + height, 0xFFAAAAAA);

        String text = label + ": " + (int) value;
        int textX = x + width / 2;
        int textY = y + (height - mc.font.lineHeight) / 2;
        context.centeredText(mc.font, text, textX, textY, Color.white.getRGB());
    }

    @Override
    protected void onClick(final MouseButtonEvent event) {
        dragging = true;
        updateValue(event.x());
    }

    @Override
    public void mouseDragged(final MouseButtonEvent event) {
        if (dragging) updateValue(event.x());
    }

    @Override
    public void mouseReleased(final MouseButtonEvent event) {
        if (dragging) dragging = false;
    }

    private void updateValue(double mouseX) {
        double percent = (mouseX - x) / width;
        value = (int) Math.clamp(min + percent * (max - min), min, max);
        if (onChange != null) onChange.accept((int) value);
    }
}
