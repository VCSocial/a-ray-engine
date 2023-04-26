package dev.vcsocial.arayengine.common;

import dev.vcsocial.arayengine.EntryPoint;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class MouseInput {

    private final Vector2d previousPosition;
    private final Vector2d currentPosition;
    private final Vector2f displayVector;

    private boolean withinWindow;
    private boolean pressedMouse1;
    private boolean pressedMouse2;

    public MouseInput() {
        previousPosition = new Vector2d(-1, -1);
        currentPosition = new Vector2d(0, 0);
        displayVector = new Vector2f();
    }

    public void init(long window) {
        GLFW.glfwSetCursorPosCallback(window, (w, positionX, positionY) -> {
            currentPosition.x = positionX;
            currentPosition.y = positionY;
            System.out.println("[currentPosition.x=%s] [currentPosition.y=%s]".formatted(currentPosition.x, currentPosition.y));
        });

        GLFW.glfwSetCursorEnterCallback(window, (w, entered) -> {
            withinWindow = entered;
            System.out.println("Entered");
        });

        GLFW.glfwSetMouseButtonCallback(window, (w, button, action, mods) -> {
           pressedMouse1 = GLFW.GLFW_MOUSE_BUTTON_1 == button
                   && GLFW.GLFW_PRESS == action;
           if (pressedMouse1) {
               System.out.println("Pressed mouse 1");
           }

            pressedMouse2 = GLFW.GLFW_MOUSE_BUTTON_2 == button
                    && GLFW.GLFW_PRESS == action;
            if (pressedMouse2) {
                System.out.println("Pressed mouse 2");
            }

        });
    }

    public void input() {
        displayVector.x = 0;
        displayVector.y = 0;

        if (withinWindow && previousPosition.x > 0 && previousPosition.y > 0) {
            displayVector.x = (float) (currentPosition.x - previousPosition.x);
            displayVector.y = (float) (currentPosition.y - previousPosition.y);
        }

        // TODO dirty hack movement is also not smooth
        if (currentPosition.x < (previousPosition.x - 10)) {
            EntryPoint.PLAYER.rotateLeft();
        } else if (currentPosition.x > (previousPosition.x + 10)) {
            EntryPoint.PLAYER.rotateRight();
        }

        previousPosition.x = currentPosition.x;
        previousPosition.y = currentPosition.y;
    }

    public Vector2f getDisplayVector() {
        return displayVector;
    }

    public boolean isPressedMouse1() {
        return pressedMouse1;
    }

    public boolean isPressedMouse2() {
        return pressedMouse2;
    }
}
