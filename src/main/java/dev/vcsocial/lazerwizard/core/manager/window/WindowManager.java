package dev.vcsocial.lazerwizard.core.manager.window;

import dev.vcsocial.lazerwizard.common.GlColor;
import dev.vcsocial.lazerwizard.core.helper.lifecycle.LifeCycleEventBroker;
import dev.vcsocial.lazerwizard.core.helper.lifecycle.PostWindowInitializationEvent;
import dev.vcsocial.lazerwizard.core.manager.FrameManager;
import dev.vcsocial.lazerwizard.core.manager.window.exception.GlfwInitializationException;
import dev.vcsocial.lazerwizard.core.manager.window.exception.GlfwWindowCreationException;
import dev.vcsocial.lazerwizard.core.util.GlOperationsUtil;
import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

@Singleton
public class WindowManager implements AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger(WindowManager.class);
    private static final String DEFAULT_TITLE = "Lazer Wizard Engine";
    private static final int DEFAULT_WIDTH = 1024;
    private static final int DEFAULT_HEIGHT = 576;
    private static final GlColor DEFAULT_BACKGROUND_COLOR = new GlColor(125, 50, 150);

    private final FrameManager frameManager;
    private final LifeCycleEventBroker lifeCycleEventBroker;
    private final String title;
    private int width;
    private int height;
    public long window;

    public WindowManager(FrameManager frameManager, LifeCycleEventBroker lifeCycleEventBroker) { // List<PollingManager> inputPollerList) {
        this.frameManager = frameManager;
        this.lifeCycleEventBroker = lifeCycleEventBroker;
        title = DEFAULT_TITLE;
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
    }

    public boolean shouldWindowClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public long getWindow() {
        return window;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @PostConstruct
    public void initialize() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new GlfwInitializationException("Unable to initialize Glfw");
        }
        LOGGER.trace("Successfully initialized GLFW");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL33.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL33.GL_FALSE);

        // Set version
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

        // Create window
//        window = GLFW.glfwCreateWindow(width, height, title, glfwGetPrimaryMonitor(), MemoryUtil.NULL);
        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new GlfwWindowCreationException("Unable to create window through GLFW");
        }
        LOGGER.trace("Successfully created the window");

        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            // TODO Use this for resizes?
        });

        // Hide the cursor when it enters the screen
        GLFW.glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);


        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            GLFW.glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode videoMode = GLFW.glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            GLFW.glfwSetWindowPos(
                    window,
                    (videoMode.width() - pWidth.get(0)) / 2,
                    (videoMode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Set vsync to on and display the window
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);

        GL.createCapabilities();
        LOGGER.trace("OpenGL capabilities have been created");

        GlOperationsUtil.glClearColor(DEFAULT_BACKGROUND_COLOR);
        GL33.glEnable(GL33.GL_DEPTH_TEST | GL33.GL_STENCIL_TEST);

        // Set back to be culled based on counterclockwise indexing
        GL33.glEnable(GL33.GL_CULL_FACE);
        GL33.glCullFace(GL33.GL_BACK);
        GL33.glFrontFace(GL33.GL_CCW);

        lifeCycleEventBroker.registerEvent(new PostWindowInitializationEvent());
    }

    @Override
    public void close() {
        GLFW.glfwDestroyWindow(window);
    }

    public void update() {
        GLFW.glfwSetWindowTitle(window,
                title + " \t | \t [Frame Time ms=" + frameManager.getFrameTimeMs() + "]");

    }

    public void setWindowShouldClose() {
        GLFW.glfwSetWindowShouldClose(window, true);
    }
}
