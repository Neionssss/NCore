package ncore;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static ncore.NCore.mc;

public class TextUtils {

    public static String safeName(ItemStack stack) {
        Component t = stack.getCustomName();
        return t != null ? t.getString() : "error";
    }

    public static String safeName(Entity entity) {
        Component t = entity.getCustomName();
        return t != null ? t.getString() : "error";
    }

    public static String unformattedText(String string) {
        return Pattern.compile("(?i)§[0-9A-FK-OR]").matcher(string).replaceAll("");
    }

    public static String substringAfter(String str, String separator) {
        int pos = str.indexOf(separator);
        if (pos == -1) return "";
        return str.substring(pos + separator.length());
    }

    public static void drawWrappedText(GuiGraphicsExtractor context, Font font, Component text, int x, int y, int maxWidth) {
        List<FormattedText> lines = font.getSplitter().splitLines(text, maxWidth, Style.EMPTY);
        int lineHeight = font.lineHeight;
        int totalHeight = lines.size() * lineHeight;
        int startY = y - totalHeight / 2;

        for (int i = 0; i < lines.size(); i++) {
            FormattedText line = lines.get(i);
            int lineY = startY + i * lineHeight;

            FormattedCharSequence charSeq = toFormattedCharSequence(line);
            context.centeredText(font, charSeq, x, lineY, Color.white.getRGB());
        }
    }

    private static FormattedCharSequence toFormattedCharSequence(FormattedText text) {
        List<FormattedCharSequence> parts = new ArrayList<>();
        text.visit((FormattedText.StyledContentConsumer<Void>) (style, contents) -> {
            parts.add(FormattedCharSequence.forward(contents, style));
            return Optional.empty();
        }, Style.EMPTY);
        return FormattedCharSequence.composite(parts);
    }

    public static void sendMessage(String str) {
        mc.player.sendSystemMessage(Component.literal(str));
    }

    public static void sendMessage(Component str) {
        mc.player.sendSystemMessage(str);
    }
}
