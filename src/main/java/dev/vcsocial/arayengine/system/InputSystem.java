package dev.vcsocial.arayengine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import dev.vcsocial.arayengine.component.*;
import dev.vcsocial.arayengine.config.KeyAction;
import dev.vcsocial.arayengine.config.KeyBindingsConfiguration;
import dev.vcsocial.arayengine.core.polling.signal.CursorInputSignal;
import dev.vcsocial.arayengine.core.polling.signal.InputSignal;
import dev.vcsocial.arayengine.core.polling.signal.KeyboardInputSignal;
import dev.vcsocial.arayengine.core.polling.signal.MouseButtonInputSignal;
import jakarta.inject.Singleton;
import org.joml.Vector2i;

@Singleton
public class InputSystem extends IteratingSystem implements Listener<InputSignal>, EntitySystemOrListener {


    @FunctionalInterface
    interface VoidFunction {
        void apply();
    }

    private final KeyBindingsConfiguration keyBindingsConfiguration;
//    private final UnifiedMap<IntIntPair, VoidFunction> cursorDeltaMappings;
//    private final UnifiedMap<IntIntPair, VoidFunction> mouseButtonMappings;
//    private UnifiedMap<IntIntPair, VoidFunction> activeInput = UnifiedMap.newMap(8);

    private Vector2i latestCursorDelta;
    private Vector2i latestMouseButton;
    private KeyAction latestKeyAction;
//    private KeyboardInputSignal keyboardInputSignal;
//    private CursorInputSignal cursorInputSignal;
//    private MouseButtonInputSignal mouseButtonInputSignal;

    public InputSystem(KeyBindingsConfiguration keyBindingsConfiguration) {
        super(Family.one(CursorInputComponent.class, MouseButtonInputComponent.class,
                KeyboardInputComponent.class).get());

        this.keyBindingsConfiguration = keyBindingsConfiguration;
//
//        cursorDeltaMappings = UnifiedMap.newWithKeysValues(
//                PrimitiveTuples.pair(0, -1), () -> System.out.println("I moved my cursor up"),
//                PrimitiveTuples.pair(0, 1), () -> System.out.println("I moved my cursor down"),
//                PrimitiveTuples.pair(-1, 0), () -> System.out.println("I moved my cursor left"),
//                PrimitiveTuples.pair(1, 0), () -> System.out.println("I moved my cursor right")
//        );
//
//        mouseButtonMappings = UnifiedMap.newWithKeysValues(
//                PrimitiveTuples.pair(GLFW.GLFW_MOUSE_BUTTON_LEFT, GLFW.GLFW_PRESS),
//                () -> System.out.println("I pressed the LEFT MOUSE BUTTON"),
//                PrimitiveTuples.pair(GLFW.GLFW_MOUSE_BUTTON_RIGHT, GLFW.GLFW_PRESS),
//                () -> System.out.println("I pressed the RIGHT MOUSE BUTTON"),
//                PrimitiveTuples.pair(GLFW.GLFW_MOUSE_BUTTON_MIDDLE, GLFW.GLFW_PRESS),
//                () -> System.out.println("I pressed the MIDDLE MOUSE BUTTON")
//        );
//
    }

//    private void processCursorInput(Entity entity) {
//        var cursorInput = CursorInputComponent.COMPONENT_MAPPER.get(entity);
//        if (cursorInput != null) {
//            if (cursorInput.deltaX != 0) {
//                var x = cursorInput.deltaX <= -1 ? -1 : 1;
//                var axisX = PrimitiveTuples.pair(x, 0);
//                var mappingX = cursorDeltaMappings.get(axisX);
//                if (mappingX != null) {
//                    mappingX.apply();
//                }
//            }
//            if (cursorInput.deltaY != 0) {
//                var y = cursorInput.deltaY <= -1 ? -1 : 1;
//                var axisY = PrimitiveTuples.pair(0, y);
//                var mappingY = cursorDeltaMappings.get(axisY);
//                if (mappingY != null) {
//                    mappingY.apply();
//                }
//            }
//        }
//    }
//
//    private void processMouseButtonInput(Entity entity) {
//        var mouseButtonInput = MouseButtonInputComponent.COMPONENT_MAPPER.get(entity);
//        if (mouseButtonInput != null) {
//            var intTuple = PrimitiveTuples.pair(mouseButtonInput.glfwMouseButton, mouseButtonInput.glfwMouseAction);
//            var mapping = mouseButtonMappings.get(intTuple);
//            if (mapping != null) {
//                mapping.apply();
//            }
//        }
//    }

//    private void processKeyboardInput(Entity entity) {
//        var keyboardInput = KeyboardInputComponent.COMPONENT_MAPPER.get(entity);
//        if (keyboardInput != null) {
//            var intTuple = PrimitiveTuples.pair(keyboardInput.glfwKey, keyboardInput.glfwAction);
//            if (intTuple.getTwo() == GLFW.GLFW_PRESS) {
//                if (repeatableKeyMappings.containsKey(intTuple) && !activeInput.containsKey(intTuple)) {
//                    activeInput.put(intTuple, repeatableKeyMappings.get(intTuple));
//                }
//            } else if (intTuple.getTwo() == GLFW.GLFW_RELEASE) {
//                activeInput.remove(PrimitiveTuples.pair(intTuple.getOne(), GLFW.GLFW_PRESS));
//            }
//
//            var mapping = unrepeatableKeyMappings.get(intTuple);
//            if (mapping != null) {
//                mapping.apply();
//            }
//        }
//    }

    // TODO Would this cause sync issues?
    @Override
    public void receive(Signal<InputSignal> signal, InputSignal inputSignal) {
        if (inputSignal instanceof CursorInputSignal c) {
            latestCursorDelta = new Vector2i(c.deltaX(), c.deltaY());
        }

        if (inputSignal instanceof MouseButtonInputSignal m) {
            latestMouseButton = new Vector2i(m.glfwMouseButton(), m.glfwAction());
        }

        if (inputSignal instanceof KeyboardInputSignal k) {
            var binding = keyBindingsConfiguration.getActionForKey(k.glfwKey(), k.glfwAction());
            if (binding != null) {
                latestKeyAction = binding;
            }
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var cursorInput = CursorInputComponent.COMPONENT_MAPPER.get(entity);
        var mouseButtonInput = MouseButtonInputComponent.COMPONENT_MAPPER.get(entity);
        var keyboardInput = KeyboardInputComponent.COMPONENT_MAPPER.get(entity);

        if (latestCursorDelta != null) {
            cursorInput.deltaX = latestCursorDelta.x;
            cursorInput.deltaY = latestCursorDelta.y;
        }

        if (latestMouseButton != null) {
            mouseButtonInput.glfwMouseButton = latestMouseButton.x;
            mouseButtonInput.glfwMouseAction = latestMouseButton.y;
        }

        if (latestKeyAction != null) {
            keyboardInput.keyAction = latestKeyAction;
        }
    }
}
