package ncore.mixins;

import net.minecraft.SharedConstants;
import net.minecraft.client.InputType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.mojang.text2speech.Narrator.LOGGER;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    private @Nullable Screen screen;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void ju(Screen screen, CallbackInfo ci) {
        ci.cancel();
        if (SharedConstants.IS_RUNNING_IN_IDE && Thread.currentThread() != minecraft.getRunningThread()) {
            LOGGER.error("setScreen called from non-game thread");
        }

        if (this.screen != null) this.screen.removed();
        else minecraft.setLastInputType(InputType.NONE);

        if (screen == null) {
            if (minecraft.player.isDeadOrDying()) {
                if (minecraft.player.shouldShowDeathScreen()) {
                    screen = new DeathScreen(null, minecraft.level.getLevelData().isHardcore(), minecraft.player);
                } else minecraft.player.respawn();
            }
        }

        this.screen = screen;
        if (this.screen != null) this.screen.added();

        if (screen != null) {
            minecraft.mouseHandler.releaseMouse();
            KeyMapping.releaseAll();
            screen.init(minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        } else {
            minecraft.textInputManager().stopTextInput();
            if (minecraft.level != null) KeyMapping.restoreToggleStatesOnScreenClosed();

            minecraft.getSoundManager().resume();
            minecraft.mouseHandler.grabMouse();
        }

        minecraft.updateTitle();
    }

}
