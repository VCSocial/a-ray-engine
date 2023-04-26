package dev.vcsocial.arayengine;

import dev.vcsocial.arayengine.common.GlColorCompatible;
import dev.vcsocial.arayengine.entity.Player;
import dev.vcsocial.arayengine.world.LevelMap;
import dev.vcsocial.arayengine.world.Tile;
import org.eclipse.collections.impl.map.immutable.ImmutableUnifiedMap;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Map;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class EntryPoint {

    /***
     * w and h
     */

    private static final String TITLE = "A Ray Engine";
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 576;
    private static final LevelMap levelMap = LevelMap.getDefaultLevelMap();
    private static final Player player = new Player(275, 275, levelMap);
    private static final ImmutableUnifiedMap<Tile, GlColorCompatible> colorMapping = new ImmutableUnifiedMap<>(Map.of(
            Tile.wall(), new GlColorCompatible(255, 0, 0),
            Tile.floor(), new GlColorCompatible(0, 255, 0),
            Tile.ceiling(), new GlColorCompatible(0, 0, 255),
            Tile.floorAndCeiling(), new GlColorCompatible(0, 255, 255)
    ));

    private static int drawStart = 0;
    private static int drawEnd = 0;

    // The window handle
    private long window;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void calc() {
        for (int x = 0; x < WIDTH; x++) {
            var mapVector = new Vector2i((int) player.getPosition().x, (int) player.getPosition().y);

            var cameraXScalar = 2.0 * x / WIDTH - 1;
            var rayVector = player.getDirection().add(player.getPlane()).mul(cameraXScalar);
            var deltaVector = new Vector2d(1.0, 1.0).div(rayVector).absolute();

            var sideVector = new Vector2d(
                    rayVector.x < 0
                            ? (player.getPosition().x - mapVector.x) * deltaVector.x
                            : (mapVector.x + 1.0 - player.getPosition().x) * deltaVector.x,
                    rayVector.x < 0
                            ? (player.getPosition().y - mapVector.y) * deltaVector.y
                            : (mapVector.y + 1.0 - player.getPosition().y) * deltaVector.x
            );

            var stepVector = new Vector2d(
                    rayVector.x < 0 ? -1 : 1,
                    rayVector.y < 0 ? -1 : 1
            );


            boolean hit = false;
            int side = 0;

            // Execute Digital Differential Algorithm (DDA)
            while (!hit) {

                //jump to next map square, either in x-direction, or in y-direction
                if (sideVector.x < stepVector.y) {
                    sideVector.x += deltaVector.x;
                    mapVector.x += stepVector.x;
                    side = 0;
                } else {
                    sideVector.y += deltaVector.y;
                    mapVector.y += stepVector.y;
                    side = 1;
                }

                //Check if ray has hit a wall
                try {
                    var tile = levelMap.getTile(mapVector.x, mapVector.y);
                    if (tile != null) {
                        hit = true;
                    }
                } catch (IndexOutOfBoundsException e) {
                    // suppress
                    continue;
                }
            }

            double perpendicularWallDistance = side == 0
                    ? sideVector.x - deltaVector.x
                    : sideVector.y - deltaVector.y;

            int lineHeight = (int) (HEIGHT / perpendicularWallDistance);

            drawStart = drawStart < 0
                    ? 0
                    : -lineHeight / 2 + HEIGHT / 2;

            drawEnd = drawEnd >= HEIGHT
                    ? HEIGHT - 1
                    : lineHeight / 2 + HEIGHT / 2;

            var color = colorMapping
                    .getOrDefault(levelMap.getTile(mapVector.x, mapVector.y), new GlColorCompatible(255, 255, 0));

            //give x and y sides different brightness
            if (side == 1) {
                color = new GlColorCompatible(color.getRed() / 2, color.getGreen() / 2, color.getBlue() / 2);
            }


            //draw the pixels of the stripe as a vertical line
            glBegin(GL_LINES);
            glLineWidth(8);
            glColor3f(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat());
            glVertex2f(drawStart, drawStart);
            glVertex2f(drawEnd, drawEnd);
            glEnd();
        }
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable


        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        player.initControls(window);


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

    //        public static void gluOrtho2D(
//        float left,
//        float right,
//        float bottom,
//        float top) {
//            glOrtho(left, right, bottom, top, -1.0, 1.0);
//        }
    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glClearColor(0.3f, 0.3f, 0.3f, 0.0f);
        glOrtho(0, WIDTH, HEIGHT, 0, -1, 1);

        // Set the clear color
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            levelMap.render();
            player.render();

            glfwSwapBuffers(window); // swap the color buffers
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new EntryPoint().run();
    }
}