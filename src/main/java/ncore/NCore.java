package ncore;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.world.scores.DisplaySlot;

import static ncore.TextUtils.unformattedText;

public class NCore implements ClientModInitializer {

    public static final Minecraft mc = Minecraft.getInstance();

    public static String getGame() {
        if (mc.level == null || mc.player == null) return null;
        var objective = mc.level.getScoreboard().getDisplayObjective(DisplaySlot.SIDEBAR);
        if (objective == null) return null;
        return unformattedText(objective.getDisplayName().getString()).toUpperCase();
    }

    public static void connect() {
        var data = new ServerData("Hypixel", "mc.hypixel.net", ServerData.Type.OTHER);
        ConnectScreen.startConnecting(null, mc, ServerAddress.parseString("mc.hypixel.net"), data, false, null);
    }

    @Override
    public void onInitializeClient() {}
}
