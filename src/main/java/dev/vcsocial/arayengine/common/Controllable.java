package dev.vcsocial.arayengine.common;

import dev.vcsocial.arayengine.diagnostics.FpsCounter;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;

import static org.lwjgl.glfw.GLFW.*;

public interface Controllable {
    IntList velocityRelevantKeys = IntArrayList.newListWith(GLFW_KEY_S, GLFW_KEY_W, GLFW_KEY_A, GLFW_KEY_D);

    default double getSpeed() {
        return FpsCounter.getFrameTime() * 5;
    }

    default double getRotationSpeed() {
        return FpsCounter.getFrameTime() * 3;
    }

    default void initControls(long window) {
        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }

            if (key == GLFW_KEY_TAB && action == GLFW_RELEASE) {
//                EntryPoint.LEVEL_MAP.toggleRendering();
            }

            if (key == GLFW_KEY_F && action == GLFW_RELEASE) {
                FpsCounter.toggleFpsCounter();
            }

            if (action != GLFW_RELEASE &&  velocityRelevantKeys.contains(key)) {
                boolean isW, isS, isA, isD;

//                if (key == GLFW_KEY_W && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
//                    moveForward();
//                }
//                if (key == GLFW_KEY_S && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
//                    moveBackward();
//                }
//                if (key == GLFW_KEY_A && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
//                    moveLeft();
//                }
//                if (key == GLFW_KEY_D && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
//                    moveRight();
//                }
            }
        });

    }

    void moveForward();

    void moveBackward();

    void moveLeft();

    void moveRight();

    void rotateLeft();

    void rotateRight();
}
