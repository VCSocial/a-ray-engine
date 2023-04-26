package dev.vcsocial.arayengine.common;

import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public interface Controllable {
    static final IntList velocityRelevantKeys = IntArrayList.newListWith(GLFW_KEY_S, GLFW_KEY_W, GLFW_KEY_A, GLFW_KEY_D);


    default float getSpeed() {
        return 5;
    }

    default void initControls(long window) {
        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }

            if (action != GLFW_RELEASE &&  velocityRelevantKeys.contains(key)) {
                var velocity = new Vector2f(0, 0);
                if (key == GLFW_KEY_W && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
//                    velocity = new Vector2f(velocity.x, velocity.y - 1);
                    updateX();
                }
                if (key == GLFW_KEY_S && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
//                    velocity = new Vector2f(velocity.x, velocity.y + 1);
                    updateY();
                }
                if (key == GLFW_KEY_A && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
//                    velocity = new Vector2f(velocity.x - 1, velocity.y);
                    updateAngle(-0.1f);
                }
                if (key == GLFW_KEY_D && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
//                    velocity = new Vector2f(velocity.x + 1, velocity.y);
                    updateAngle(0.1f);
                }

//                velocity = velocity.normalize().mul(getSpeed());
//                updateX(velocity.x);
//                updateY(velocity.y);
//                updateX();
//                updateY();
            }
        });
    }

    void updateX();

    void updateY();

    void updateAngle(float updateIncrement);
}
