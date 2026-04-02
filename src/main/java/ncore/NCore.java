package ncore;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

public class NCore implements ClientModInitializer {

    public static final Minecraft mc = Minecraft.getInstance();

    public static void connect() {
        var data = new ServerData("Hypixel", "mc.hypixel.net", ServerData.Type.OTHER);
        ConnectScreen.startConnecting(null, mc, ServerAddress.parseString("mc.hypixel.net"), data, false, null);
    }

    @Override
    public void onInitializeClient() {}
}