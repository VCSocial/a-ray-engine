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
    private static final GlColor DEFAULT_BACKGROUND_COLOR = new GlColor(125, 50, 175);

    private final FrameManager frameManager;
    private final LifeCycleEventBroker lifeCycleEventBroker;
    private final String title;
    private int width;
    private int height;
    public long window;

    public WindowManager(FrameManager frameManager, LifeCycleEventBroker lifeCycleEventBroker) {
        this.frameManager = frameManager;
        this.lifeCycleEventBroker = lifeCycleEventBroker;
        title = DEFAULT_TITLE;
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
    }

    private void initializeGl() {
        GL.createCapabilities();
        LOGGER.debug("OpenGL capabilities have been created");

        GlOperationsUtil.glClearColor(DEFAULT_BACKGROUND_COLOR);
        GL33.glEnable(GL33.GL_DEPTH_TEST | GL33.GL_STENCIL_TEST);

        // Set back to be culled based on counterclockwise indexing
        GL33.glEnable(GL33.GL_CULL_FACE);
        GL33.glCullFace(GL33.GL_BACK);
        GL33.glFrontFace(GL33.GL_CCW);
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
        LOGGER.debug("Successfully initialized GLFW");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL33.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL33.GL_FALSE);

        // Set version
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

        // Get primary monitor
        long primaryMonitor = glfwGetPrimaryMonitor();

        // Create window
        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
//        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new GlfwWindowCreationException("Unable to create window through GLFW");
        }
        LOGGER.debug("Successfully created the window");

        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            // TODO Use this for resizes?
        });

        // Hide the cursor when it enters the screen
//        GLFW.glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        GLFW.glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);


        // Get the thread stack and push a new frame
        // the stack frame is popped automatically
        try (MemoryStack stack = stackPush()) {
            IntBuffer ptrToWidth = stack.mallocInt(1);
            IntBuffer ptrToHeight = stack.mallocInt(1);

            // Get the window size passed to glfwCreateWindow
            GLFW.glfwGetWindowSize(window, ptrToWidth, ptrToHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode videoMode = GLFW.glfwGetVideoMode(primaryMonitor);

            // Center the window
            GLFW.glfwSetWindowPos(
                    window,
                    (videoMode.width() - ptrToWidth.get(0)) / 2,
                    (videoMode.height() - ptrToHeight.get(0)) / 2
            );
        }

        // Set vsync to on and display the window
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);

        initializeGl();
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
        LOGGER.debug("Set window to should close, attempting to quit!");
    }
}
