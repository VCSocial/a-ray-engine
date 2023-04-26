package dev.vcsocial.arayengine.manager.window;

import dev.vcsocial.arayengine.core.polling.PollingManager;
import dev.vcsocial.arayengine.diagnostics.FpsCounter;
import dev.vcsocial.arayengine.manager.window.exception.GlfwInitializationException;
import dev.vcsocial.arayengine.manager.window.exception.GlfwWindowCreationException;
import io.avaje.inject.PostConstruct;
import io.avaje.inject.PreDestroy;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowLifecycleManager {

    private static final Logger LOGGER = LogManager.getLogger(WindowLifecycleManager.class);

    private static final String DEFAULT_TITLE = "A Ray Engine";
    private static final int DEFAULT_WIDTH = 1024;
    private static final int DEFAULT_HEIGHT = 576;

    // TODO Encapsulate?
    public static int width = DEFAULT_WIDTH;
    public static int height = DEFAULT_HEIGHT;
    public static String title = DEFAULT_TITLE;

    public static long window;

    private ImmutableList<Consumer<Long>> windowConsumers;
    private ImmutableList<Runnable> renderRunners;
    private List<PollingManager> pollingManagerList;

    public WindowLifecycleManager(ImmutableList<Consumer<Long>> windowConsumers, ImmutableList<Runnable> renderRunners) {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_TITLE, windowConsumers, renderRunners);
    }

    @Inject
    public WindowLifecycleManager(List<PollingManager> pollingManagerList) {
        WindowLifecycleManager.width = DEFAULT_WIDTH;
        WindowLifecycleManager.height = DEFAULT_HEIGHT;
        WindowLifecycleManager.title = DEFAULT_TITLE;

        this.pollingManagerList = pollingManagerList;
        this.windowConsumers = Lists.immutable.empty();
        this.renderRunners = Lists.immutable.empty();
    }

    public WindowLifecycleManager(int width, int height, String title, ImmutableList<Consumer<Long>> windowConsumers,
                                  ImmutableList<Runnable> renderRunners) {
        WindowLifecycleManager.width = width;
        WindowLifecycleManager.height = height;
        WindowLifecycleManager.title = title;
        this.windowConsumers = windowConsumers;
        this.renderRunners = renderRunners;
    }

    @PostConstruct
    void init() {
        LOGGER.info("A test log a the beginning");

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new GlfwInitializationException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);


        // Create the window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            throw new GlfwWindowCreationException("Failed to create the GLFW window");
        }
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
//        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        windowConsumers.forEach(windowConsumer -> windowConsumer.accept(window));
//        pollingManagerList.forEach(p -> p.initialize(window));


        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    @PreDestroy
    void terminate() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void loop() {
        LOGGER.info("A test log in the loop");
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glClearColor(0.3f, 0.3f, 0.3f, 0.0f);
        glOrtho(0, width, height, 0, -1, 1);

        // Set the clear color
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            renderRunners.forEach(Runnable::run);
//            pollingManagerList.forEach(p -> p.deltaPoll(window));
            FpsCounter.updateFrameTime();
//            engine.update(1);

            glfwSwapBuffers(window); // swap the color buffers
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public long getGlfwWindow() {
        return window;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public void run() {
        init();
        loop();
    }
}
