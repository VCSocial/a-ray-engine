package dev.vcsocial.arayengine.common;

import dev.vcsocial.arayengine.EntryPoint;
import dev.vcsocial.arayengine.window.Window;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;

import static org.lwjgl.glfw.GLFW.*;

public interface Controllable {
    IntList velocityRelevantKeys = IntArrayList.newListWith(GLFW_KEY_S, GLFW_KEY_W, GLFW_KEY_A, GLFW_KEY_D);

    default double getSpeed() {
        return Window.frameTime * 5;
    }

    default double getRotationSpeed() {
        return Window.frameTime * 3;
    }

    default void initControls(long window) {
        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }

            if (key == GLFW_KEY_TAB && action == GLFW_RELEASE) {
                EntryPoint.LEVEL_MAP.toggleRendering();
            }

            if (action != GLFW_RELEASE &&  velocityRelevantKeys.contains(key)) {
                if (key == GLFW_KEY_W && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
                    moveForward();
                }
                if (key == GLFW_KEY_S && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
                    moveBackward();
                }
                if (key == GLFW_KEY_A && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
                    rotateLeft();
                }
                if (key == GLFW_KEY_D && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
                    rotateRight();
                }
            }
        });
    }

    void moveForward();

    void moveBackward();

    void rotateLeft();

    void rotateRight();
}
