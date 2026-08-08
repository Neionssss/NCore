gpackage ncore;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

import static ncore.NCore.mc;

public class RenderUtils {

    public static void roundedFill(GuiGraphicsExtractor context, int x, int y, int width, int height, int color) {
        context.fill(x, y + 1, x + width, y + height - 1, color);
        context.fill(x + 1, y, x + width - 1, y + 1, color);
        context.fill(x + 1, y + height - 1, x + width - 1, y + height, color);
    }

    public static void highlightSlot(GuiGraphicsExtractor context, Slot slot, Color color) {
        context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, color.getRGB());
    }

    private static double interpolate(double now, double last) {
        float delta = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        return last + (now - last) * delta;
    }

    public static Vec3 interpolateEntity(Entity e) {
        return new Vec3(interpolate(e.getX(), e.xo), interpolate(e.getY(), e.yo), interpolate(e.getZ(), e.zo));
    }

    private static AABB interpolateBox(Entity e) {
        var i = interpolateEntity(e);
        return e.getBoundingBox().move(i.x - e.getX(), i.y - e.getY(), i.z - e.getZ());
    }

    public static void renderESP(Entity entity, Color color) {
        Gizmos.cuboid(interpolateBox(entity), GizmoStyle.stroke(color.getRGB())).setAlwaysOnTop();
    }

    public static void drawTracer(Entity e, Color color) {
        Vec3 end = interpolateEntity(e).add(0, e.getEyeHeight(), 0);
        var cam = mc.getCameraEntity();
        Gizmos.line(cam.getEyePosition(mc.getDeltaTracker().getGameTimeDeltaPartialTick(false)).add(cam.getLookAngle().scale(8)), end, color.getRGB()).setAlwaysOnTop();
    }

    public static void renderNameTags(Entity entity, Color color) {
        var interpPos = interpolateEntity(entity);
        var distance = mc.player.distanceToSqr(interpPos);
        var scale = 0.3 + 0.0015 * distance;
        var s = TextUtils.substringAfter(TextUtils.safeName(entity), "]");

        drawText(s, interpPos.add(0, entity.getEyeHeight(), 0), scale, color);
    }

    public static void renderHealth(LivingEntity entity) {
        var interpPos = interpolateEntity(entity);
        var distance = mc.player.distanceToSqr(interpPos);
        var scale = 0.3 + 0.0015 * distance;
        Color color = Color.green;
        var health = (int) entity.getHealth();
        if (health < 15 && health >= 10) color = Color.yellow;
        else if (health < 10) color = Color.red;

        drawText(String.valueOf(health), interpPos.add(0, entity.getEyeHeight(), 0), scale, color);
    }

    public static void drawText(String s, Vec3 vec, double scale, Color color) {
        Gizmos.billboardText(s, vec, TextGizmo.Style.forColorAndCentered(color.getRGB()).withScale((float) scale)).setAlwaysOnTop();
    }

    public static void drawLine(GuiGraphicsExtractor graphics, float x1, float y1, float x2, float y2, float thickness, int color) {
        float dx = x2 - x1;
        float dy = y2 - y1;

        float length = (float) Math.sqrt(dx * dx + dy * dy);
        int steps = Math.max(1, (int) Math.ceil(length));

        int pixelSize = Math.max(1, Math.round(thickness));

        for (int i = 0; i <= steps; i++) {
            float t = i / (float) steps;

            int x = Math.round(x1 + dx * t);
            int y = Math.round(y1 + dy * t);

            graphics.fill(x - pixelSize / 2, y - pixelSize / 2, x - pixelSize / 2 + pixelSize, y - pixelSize / 2 + pixelSize, color);
        }
    } 
}
