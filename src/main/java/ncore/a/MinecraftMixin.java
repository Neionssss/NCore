package ncore.a;

import ncore.NCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.events.GameLoadTimesEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Redirect(method = "onGameLoadFinished", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/telemetry/events/GameLoadTimesEvent;send(Lnet/minecraft/client/telemetry/TelemetryEventSender;)V"))
    private void var021(GameLoadTimesEvent instance, TelemetryEventSender eventSender) {}

    @Redirect(method = "onGameLoadFinished", at = @At(value = "INVOKE", target = "Ljava/lang/Runnable;run()V"))
    private void var0223(Runnable instance) {
        NCore.connect();
    }
}
