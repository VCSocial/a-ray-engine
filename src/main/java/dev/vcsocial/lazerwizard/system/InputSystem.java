package dev.vcsocial.lazerwizard.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import dev.vcsocial.lazerwizard.component.*;
import dev.vcsocial.lazerwizard.config.keybindings.KeyAction;
import dev.vcsocial.lazerwizard.config.keybindings.KeyBindingsConfiguration;
import dev.vcsocial.lazerwizard.config.mousebindings.CursorAction;
import dev.vcsocial.lazerwizard.config.mousebindings.MouseAction;
import dev.vcsocial.lazerwizard.config.mousebindings.MouseBindingsConfiguration;
import dev.vcsocial.lazerwizard.core.polling.signal.CursorInputSignal;
import dev.vcsocial.lazerwizard.core.polling.signal.InputSignal;
import dev.vcsocial.lazerwizard.core.polling.signal.KeyboardInputSignal;
import dev.vcsocial.lazerwizard.core.polling.signal.MouseButtonInputSignal;
import jakarta.inject.Singleton;
import org.joml.Vector2i;

@Singleton
public class InputSystem extends IteratingSystem implements Listener<InputSignal>, EntitySystemOrListener {

    private final MouseBindingsConfiguration mouseBindingsConfiguration;
    private final KeyBindingsConfiguration keyBindingsConfiguration;

    private Vector2i latestCursorDelta;
    private Vector2i latestMouseButton;
    private CursorAction latestCursorAction;
    private MouseAction latestMouseAction;
    private KeyAction latestKeyAction;

    public InputSystem(MouseBindingsConfiguration mouseBindingsConfiguration,
                       KeyBindingsConfiguration keyBindingsConfiguration) {
        super(Family.one(CursorInputComponent.class, MouseButtonInputComponent.class,
                KeyboardInputComponent.class).get());

        this.mouseBindingsConfiguration = mouseBindingsConfiguration;
        this.keyBindingsConfiguration = keyBindingsConfiguration;
    }

    // TODO Would this cause sync issues?
    @Override
    public void receive(Signal<InputSignal> signal, InputSignal inputSignal) {
        if (inputSignal instanceof CursorInputSignal c) {
            latestCursorDelta = new Vector2i(c.deltaX(), c.deltaY());
            latestCursorAction = mouseBindingsConfiguration.getActionForCursorDelta(c.deltaX(), c.deltaY());
        }

        if (inputSignal instanceof MouseButtonInputSignal m) {
            latestMouseButton = new Vector2i(m.glfwMouseButton(), m.glfwAction());
            latestMouseAction = mouseBindingsConfiguration.getActionForMouseButton(m.glfwMouseButton(), m.glfwAction());
        }

        if (inputSignal instanceof KeyboardInputSignal k) {
            var binding = keyBindingsConfiguration.getActionForKey(k.glfwKey(), k.glfwAction());
            latestKeyAction = binding != null
                    ? binding
                    : KeyAction.INVALID;
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
            cursorInput.cursorAction = latestCursorAction;
        }

        if (latestMouseButton != null) {
            mouseButtonInput.glfwMouseButton = latestMouseButton.x;
            mouseButtonInput.glfwMouseAction = latestMouseButton.y;
            mouseButtonInput.mouseAction = latestMouseAction;
        }

        if (latestKeyAction != null) {
            keyboardInput.keyAction = latestKeyAction;
        }
    }
}
