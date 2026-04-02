package ncore.a;

import net.minecraft.client.gui.screens.multiplayer.ServerReconfigScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerReconfigScreen.class)
public abstract class ServerReconfigScreenMixin {

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void var12(CallbackInfo ci) { ci.cancel(); }

}
