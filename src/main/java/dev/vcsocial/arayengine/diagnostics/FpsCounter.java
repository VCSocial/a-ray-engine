package dev.vcsocial.arayengine.diagnostics;

import org.lwjgl.glfw.GLFW;

public final class FpsCounter {
    private static double time = GLFW.glfwGetTime();
    private static double frameTime = 0;
    private static boolean isLoggingEnabled;

    public static void updateFrameTime() {
        double oldTime = time;
        time = org.lwjgl.glfw.GLFW.glfwGetTime() * 1000;
        frameTime = (time - oldTime) / 1000.0;
        if (isLoggingEnabled) {
            System.out.println("[FPS=" + 1.0 / frameTime + "]");
        }
    }

    public static double getFrameTime() {
        return frameTime;
    }

    public static void toggleFpsCounter() {
        isLoggingEnabled = !isLoggingEnabled;
    }
}
