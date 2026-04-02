package ncore.a;

import ncore.NActionButton;
import ncore.NButton;
import ncore.NSliderButton;
import ncore.ButtonOption;
import ncore.NCore;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.*;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackRepository;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {

    protected PauseScreenMixin() {
        super(Component.empty());
    }

    private Screen var55() {
        try {
            var clazz = Class.forName("net.caffeinemc.mods.sodium.client.gui.VideoSettingsScreen");
            var m = clazz.getDeclaredMethod("createScreen", Screen.class);
            return (Screen) m.invoke(null, this);
        } catch (Throwable t) {
            return new VideoSettingsScreen(this, minecraft, minecraft.options);
        }
    }

    private boolean var52() {
        try {
            Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private Screen var57(Screen parent) {
        try {
            var clazz = Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");
            var ctor = clazz.getConstructor(Screen.class);
            return (Screen) ctor.newInstance(parent);

        } catch (Throwable t) {
            return null;
        }
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void var012(CallbackInfo ci) {
        ci.cancel();
        NButton.clearAll();
        int startXLeft = width / 2 - 155;
        int startXRight = width / 2 + 5;
        int y = height / 6;
        var settings = minecraft.options;

        List<ButtonOption> options = new ArrayList<>();
        options.add(new ButtonOption("options.skinCustomisation", () -> minecraft.setScreen(new SkinCustomizationScreen(this, settings))));
        options.add(new ButtonOption("options.sounds", () -> minecraft.setScreen(new SoundOptionsScreen(this, settings))));
        options.add(new ButtonOption("options.video", () -> minecraft.setScreen(var55())));
        options.add(new ButtonOption("options.controls", () -> minecraft.setScreen(new ControlsScreen(this, settings))));
        options.add(new ButtonOption("options.language", () -> minecraft.setScreen(new LanguageSelectScreen(this, settings, minecraft.getLanguageManager()))));
        options.add(new ButtonOption("options.chat.title", () -> minecraft.setScreen(new ChatOptionsScreen(this, settings))));
        options.add(new ButtonOption("options.resourcepack", () -> minecraft.setScreen(new PackSelectionScreen(minecraft.getResourcePackRepository(), this::var56, minecraft.getResourcePackDirectory(), Component.translatable("resourcePack.title")))));
        options.add(new ButtonOption("options.accessibility.title", () -> minecraft.setScreen(new AccessibilityOptionsScreen(this, settings))));

        if (var52()) options.add(new ButtonOption("Mods", () -> minecraft.setScreen(var57(this))));

        for (int i = 0; i < options.size(); i++) {
            ButtonOption opt = options.get(i);
            int x = (i % 2 == 0) ? startXLeft : startXRight;
            int btnY = y + (i / 2) * 24;
            var label = Component.translatable(opt.translationKey()).getString();
            NButton.addButton(new NActionButton(x, btnY, 150, 20, label, Color.cyan.darker(), opt.action()));
        }

        var fovSet = settings.fov();

        int sliderWidth = 160;
        int sliderHeight = 20;
        int fovX = width / 2 - sliderWidth / 2;
        int spaceAbove = height / 6 - 40;
        int fovY = spaceAbove >= sliderHeight ? 40 : (int) (height / 1.5);
        NButton.addButton(new NSliderButton(fovX, fovY, sliderWidth, sliderHeight, "FOV", 30, 110, fovSet.get(), val -> fovSet.set(val.intValue())));
    }

    private void var56(PackRepository resourcePackManager) {
        minecraft.options.updateResourcePacks(resourcePackManager);
        minecraft.setScreen(this);
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void var051(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        ci.cancel();

        graphics.fill(0,0, width / 32, height, Color.cyan.darker().getRGB());
        int x1 = (int) (width * 0.003);
        int cy = (int) (height * 0.9);
        int size = Math.min(width, height) / 20;
        int colorExit = 0xFFFF4444;
        int colorPlay = 0xFF44FF44;

        for (int i = 0; i < size; i++) {
            int left = x1 + i / 3;
            int top = x1 + i / 2;
            int bottom = size - i / 2;
            graphics.fill(left, top, x1 + i, bottom, colorPlay);
        }

        int segments = 720;
        float cx = x1 + size / 2f;
        for (int i = 0; i < segments * 3 / 4; i++) {
            double angle1 = Math.toRadians(i * 360.0 / segments);
            double angle2 = Math.toRadians((i + 1) * 360.0 / segments);

            int xStart = (int) (cx + Math.cos(angle1) * size / 1.75);
            int yStart = (int) (cy + Math.sin(angle1) * size / 1.75);
            int xEnd = (int) (cx + Math.cos(angle2) * size / 3);
            int yEnd = (int) (cy + Math.sin(angle2) * size / 3);

            graphics.fill(xStart, yStart, xEnd, yEnd, 0xFFFFFFFF);
        }

        int yExit = (int) (height * 0.95);
        for (int i = 0; i < size; i++) {
            graphics.fill(i, yExit + i, x1 + i + 1, yExit + i + 1, colorExit); // \ диагональ \
            graphics.fill(size - i, yExit + i, x1 + (size - i) + 1, yExit + i + 1, colorExit); //  / диагональ /
        }

        NButton.getButtons().forEach(btn -> btn.draw(graphics, mouseX, mouseY));
    }
    @Override
    public boolean mouseClicked(final @NonNull MouseButtonEvent event, final boolean doubleClick) {
        for (NButton btn : NButton.getButtons()) btn.mouseClicked(event);

        int minDimension = Math.min(width, height);
        int buttonSize = minDimension / 20;
        double buttonX = width * 0.003;

        if (isHovered(event, buttonX, height * 0.01, buttonSize)) minecraft.setScreen(null);

        var conn = minecraft.getConnection().getConnection();
        if (isHovered(event, buttonX, height * 0.95, buttonSize)) conn.disconnect(Component.literal("Left the game..."));

        if (isHovered(event, buttonX, height * 0.9, buttonSize)) {
            conn.disconnect(Component.literal("Reconnecting..."));
            NCore.connect();
        }

        return false;
    }

    private boolean isHovered(final @NonNull MouseButtonEvent event, double x, double y, int size) {
        double clickX = event.x();
        double clickY = event.y();

        return clickX >= x && clickX <= x + size && clickY >= y && clickY <= y + size;
    }

    @Override
    public boolean mouseDragged(final MouseButtonEvent event, final double dx, final double dy) {
        NButton.getButtons().forEach(btn -> btn.mouseDragged(event));
        return false;
    }

}
