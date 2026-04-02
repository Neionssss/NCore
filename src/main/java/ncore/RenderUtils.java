package ncore;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
import java.awt.*;

import static ncore.NCore.mc;
import static ncore.RenderContext.getMatrices;
import static net.minecraft.util.Mth.floor;

public class RenderUtils {

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

    private static void vertex(float x, float y, float z, Color color, MultiBufferSource.BufferSource bufferSource) {
        var buffer = bufferSource.getBuffer(RenderTypes.LINES);
        buffer.addVertex(getMatrices(), x,y,z).setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f).setNormal(0f,0f,0f).setLineWidth(4.0f);
    }

    public static void renderESP(Entity entity, Color color) {
        var cameraPos = mc.getEntityRenderDispatcher().camera.position();
        var box = interpolateBox(entity).move(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        var buffer = mc.renderBuffers().bufferSource();

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        addBoxLines(buffer, box, color);
        buffer.endBatch();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private static final int[][] EDGES = {
            {0,1},{1,2},{2,3},{3,0}, // низ
            {4,5},{5,6},{6,7},{7,4}, // верх
            {0,4},{1,5},{2,6},{3,7}  // вертикали
    };

    private static void addBoxLines(MultiBufferSource.BufferSource vc, AABB box, Color color) {
        float[] x = {(float) box.minX, (float) box.maxX};
        float[] y = {(float) box.minY, (float) box.maxY};
        float[] z = {(float) box.minZ, (float) box.maxZ};
        Vec3[] v = {
                new Vec3(x[0], y[0], z[0]),
                new Vec3(x[1], y[0], z[0]),
                new Vec3(x[1], y[0], z[1]),
                new Vec3(x[0], y[0], z[1]),
                new Vec3(x[0], y[1], z[0]),
                new Vec3(x[1], y[1], z[0]),
                new Vec3(x[1], y[1], z[1]),
                new Vec3(x[0], y[1], z[1])
        };

        for (int[] e : EDGES) {
            Vec3 a = v[e[0]], b = v[e[1]];
            vertex((float) a.x, (float) a.y, (float) a.z, color, vc);
            vertex((float) b.x, (float) b.y, (float) b.z, color, vc);
        }
    }

    public static void drawTracer(Entity e, Color color) {
        var headPos = mc.player.getLookAngle();
        var cameraPos = mc.getEntityRenderDispatcher().camera.position();
        var bufferSource = mc.renderBuffers().bufferSource();
        var pos2 = interpolateEntity(e).add(0, e.getEyeHeight(), 0);
        vertex((float) headPos.x, (float) headPos.y, (float) headPos.z, color, bufferSource);
        vertex((float) (pos2.x - cameraPos.x), (float) (pos2.y - cameraPos.y), (float) (pos2.z - cameraPos.z), color, bufferSource);
        bufferSource.endBatch();
    }

    public static void renderNameTags(Entity entity, Color color) {
        var pose = getMatrices();
        var camera = mc.getEntityRenderDispatcher().camera;
        var camPos = camera.position();
        var interpPos = interpolateEntity(entity);
        var distance = camPos.distanceTo(interpPos);
        float scale = (float) Math.max(0.004f * distance, 0.020f);
        var s = TextUtils.substringAfter(TextUtils.safeName(entity), "]");

        pose.pushMatrix();
        pose.translate((float) (interpPos.x - camPos.x), (float) (interpPos.y + entity.getEyeHeight() - camPos.y), (float) (interpPos.z - camPos.z));
        pose.rotate(camera.rotation());
        pose.scale(scale, -scale, scale);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        drawCenteredTextWithShadow(s, 0, 0, color);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        pose.popMatrix();
    }

    public static void drawCenteredTextWithShadow(String text, float x, float y, Color color) {
        var consumers = mc.renderBuffers().bufferSource();
        mc.font.drawInBatch(text, x - mc.font.width(text) / 2f, y, color.getRGB(),true, getMatrices(), consumers, Font.DisplayMode.NORMAL, 0x00000000, 15728880);
        consumers.endBatch();
    }
}