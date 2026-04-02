package ncore.a;

import io.netty.channel.ChannelFuture;
import ncore.NActionButton;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static ncore.TextUtils.drawWrappedText;

@Mixin(ConnectScreen.class)
public abstract class ConnectScreenMixin extends Screen {

    @Shadow
    private Component status;
    @Shadow
    private @Nullable ChannelFuture channelFuture;
    @Shadow
    private volatile @Nullable Connection connection;
    NActionButton cancel;
    private int lastWidth = -1;
    private int lastHeight = -1;


    protected ConnectScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void init(CallbackInfo ci) {
        ci.cancel();
    }

    private void disconnect() {
        if (channelFuture != null) {
            channelFuture.cancel(true);
            channelFuture = null;
        }

        if (connection != null) connection.disconnect(Component.empty());
        minecraft.setScreen(new DisconnectedScreen(null, Component.empty(), Component.literal("Cancelled")));
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        if (width != lastWidth || height != lastHeight) {
            lastHeight = height;
            lastWidth = width;
            cancel = new NActionButton(width / 2 - 75, height / 2, 150, 20, "Cancel", Color.BLUE, this::disconnect);
        }
        drawWrappedText(context, font, status, width / 2, height / 2 - 30, width - 50);
        cancel.draw(context,mouseX,mouseY);
    }

    @Override
    public boolean mouseClicked(final @NonNull MouseButtonEvent event, final boolean doubleClick) {
        cancel.mouseClicked(event);
        return false;
    }

}
