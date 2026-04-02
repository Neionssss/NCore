package ncore;

import org.joml.Matrix4fStack;

public final class RenderContext {

    private static Matrix4fStack matrices;

    public static void setMatrices(Matrix4fStack stack) {
        matrices = stack;
    }

    public static Matrix4fStack getMatrices() {
        return matrices;
    }
}