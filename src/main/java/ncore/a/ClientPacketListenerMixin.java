package ncore.a;

import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ncore.NCore.mc;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Shadow
    private @Nullable LevelLoadTracker levelLoadTracker;

    @Inject(method = "startWaitingForNewLevel", at = @At(value = "HEAD"), cancellable = true)
    public void var1(LocalPlayer player, ClientLevel level, LevelLoadingScreen.Reason reason, CallbackInfo ci) {
        ci.cancel();
        if (levelLoadTracker == null) levelLoadTracker = new LevelLoadTracker();
        levelLoadTracker.startClientLoad(player, level, mc.levelRenderer);
        mc.setScreenAndShow(null);
    }
}