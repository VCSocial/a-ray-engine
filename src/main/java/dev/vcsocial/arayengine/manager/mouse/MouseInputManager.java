package dev.vcsocial.arayengine.manager.mouse;

import jakarta.inject.Singleton;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

@Singleton
public class MouseInputManager {

    private final Vector2d previousPosition;
    private final Vector2d currentPosition;
    private final Vector2f displayVector;

    private static boolean withinWindow;
    private static final Runnable placeHolder = new Runnable() {
        @Override
        public void run() {
            System.out.println("triggered place holder");
        }
    };

    private static UnifiedMap<Integer, Runnable> mouseButtonCallbacks = UnifiedMap.newWithKeysValues(
            GLFW.GLFW_MOUSE_BUTTON_LEFT, placeHolder,
            GLFW.GLFW_MOUSE_BUTTON_RIGHT, placeHolder
    );

    private static UnifiedMap<Integer, UnifiedMap<Integer, Runnable>> mouseActionCallbacks = UnifiedMap.newWithKeysValues(
      GLFW.GLFW_PRESS, mouseButtonCallbacks
    );

    private boolean pressedMouse1;
    private boolean pressedMouse2;

    public MouseInputManager() {
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
            var buttonCallbacks = mouseActionCallbacks.get(action);
            if (buttonCallbacks != null) {
                var callback = mouseButtonCallbacks.get(button);
                if (callback != null) {
                    callback.run();
                }
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
//            EntryPoint.PLAYER.rotateLeft();
        } else if (currentPosition.x > (previousPosition.x + 10)) {
//            EntryPoint.PLAYER.rotateRight();
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
