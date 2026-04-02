package ncore;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

import static ncore.NCore.mc;

public class NActionButton extends NButton {
    private final String label;
    private final Runnable action;
    private final Color color;

    public NActionButton(int x, int y, int width, int height, String label, Color color, Runnable action) {
        super(x, y, width, height, label);
        this.label = label;
        this.action = action;
        this.color = color;
    }

    @Override
    public void draw(GuiGraphicsExtractor context, double mouseX, double mouseY) {
        Color color2 = isHovered(mouseX, mouseY) ? color.darker() : color;
        context.fill(x, y, x + width, y + height, color2.getRGB());
        int textX = x + width / 2;
        int textY = y + (height - mc.font.lineHeight) / 2;
        context.centeredText(mc.font, label, textX, textY, Color.white.getRGB());
    }

    @Override
    protected void onClick(final MouseButtonEvent event) {
        if (action != null) action.run();
    }
}