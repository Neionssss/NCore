package ncore.mixins;

import ncore.NCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.events.GameLoadTimesEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Redirect(method = "onGameLoadFinished", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/telemetry/events/GameLoadTimesEvent;send(Lnet/minecraft/client/telemetry/TelemetryEventSender;)V"))
    private void var021(GameLoadTimesEvent instance, TelemetryEventSender eventSender) {}

    @Redirect(method = "onGameLoadFinished", at = @At(value = "INVOKE", target = "Ljava/lang/Runnable;run()V"))
    private void var0223(Runnable instance) {
        NCore.connect();
    }

    @Redirect(method = "clearClientLevel", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/Minecraft;setScreenAndShow(Lnet/minecraft/client/gui/screens/Screen;)V"))
    private void ss(Minecraft instance, Screen screen) {}

    @Inject(method = "handleKeybinds", at = @At("HEAD"), cancellable = true)
    private void fixIssue(CallbackInfo ci) {
        if (player == null) ci.cancel();
    }

}
