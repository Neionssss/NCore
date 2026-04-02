package ncore.a;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Inject(method = "extractPanorama", at = @At(value = "HEAD"), cancellable = true)
    private void var920(GuiGraphicsExtractor graphics, float a, CallbackInfo ci) {
        ci.cancel();
    }
}
