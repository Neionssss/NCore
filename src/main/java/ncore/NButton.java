package ncore;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static ncore.NCore.mc;

public abstract class NButton {
    protected int x, y, width, height;
    protected String label;

    private static final Set<NButton> buttons = ConcurrentHashMap.newKeySet();

    public NButton(int x, int y, int width, int height, String label) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
    }

    public static Set<NButton> getButtons() {
        return buttons;
    }

    public static void clearAll() {
        buttons.clear();
    }

    public static void addButton(NButton btn) {
        buttons.add(btn);
    }

    public abstract void draw(GuiGraphicsExtractor context, double mouseX, double mouseY);

    protected abstract void onClick(final MouseButtonEvent event);

    public void mouseDragged(final MouseButtonEvent event) {}

    public void mouseReleased(final MouseButtonEvent event) {}

    public void mouseClicked(final MouseButtonEvent event) {
        if (event.button() == 0 && isHovered(event.x(), event.y())) {
            onClick(event);
            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    public boolean isHovered(double mX, double mY) {
        return mX >= x && mX <= x + width && mY >= y && mY <= y + height;
    }
}