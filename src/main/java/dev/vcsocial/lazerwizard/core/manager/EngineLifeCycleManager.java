package dev.vcsocial.lazerwizard.core.manager;

import jakarta.inject.Singleton;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

@Singleton
public class EngineLifeCycleManager implements AutoCloseable {

    private final WindowManager windowManager;
    private final EntityComponentSystemManager entityComponentSystemManager;
    private final GLFWErrorCallback glfwErrorCallback;

    public EngineLifeCycleManager(WindowManager windowManager,
                                  EntityComponentSystemManager entityComponentSystemManager) {
        this.windowManager = windowManager;
        this.entityComponentSystemManager = entityComponentSystemManager;
        GLFW.glfwSetErrorCallback(glfwErrorCallback = GLFWErrorCallback.createPrint(System.err));
    }

    @Override
    public void close() {
        glfwErrorCallback.free();
        GLFW.glfwTerminate();
    }

    public void executeGameLoop() {
        while(!windowManager.shouldWindowClose()) {
            entityComponentSystemManager.update();
            windowManager.update();
        }
    }
}
