package ncore.a;

import ncore.NActionButton;
import ncore.NCore;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.awt.Color;

import static ncore.TextUtils.drawWrappedText;

@Mixin(DisconnectedScreen.class)
public abstract class DisconnectedScreenMixin extends Screen {

    @Shadow
    @Final
    private DisconnectionDetails details;
    private NActionButton nActionButton;
    private NActionButton nExitButton;
    private long lClicked = 0L;
    private int lastWidth = -1;
    private int lastHeight = -1;

    protected DisconnectedScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void var000(CallbackInfo ci) {
        ci.cancel();
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        int baseY = height / 2;
        if (width != lastWidth || height != lastHeight) {
            nActionButton = new NActionButton(width / 2 - 100, baseY, 200, 50, "Reconnect", Color.orange, NCore::connect);
            nExitButton = new NActionButton(width / 2 - 100, baseY + 60, 200, 50, "Exit", Color.red, minecraft::stop);
            lastWidth = width;
            lastHeight = height;
        }

        drawWrappedText(context, font, details.reason(), width / 2, baseY - 30, width - 50);
        nActionButton.draw(context, mouseX, mouseY);
        nExitButton.draw(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(final @NonNull MouseButtonEvent event, final boolean doubleClick) {
        if (System.currentTimeMillis() - lClicked > 1000) {
            nActionButton.mouseClicked(event);
            nExitButton.mouseClicked(event);
            lClicked = System.currentTimeMillis();
        }
        return false;
    }

}
