package dev.vcsocial.lazerwizard.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import dev.vcsocial.lazerwizard.component.input.CursorInputComponent;
import dev.vcsocial.lazerwizard.component.input.KeyboardInputComponent;
import dev.vcsocial.lazerwizard.component.input.MouseButtonInputComponent;
import dev.vcsocial.lazerwizard.config.keybindings.KeyActionManagement;
import dev.vcsocial.lazerwizard.config.keybindings.KeyActionVertical;
import dev.vcsocial.lazerwizard.config.keybindings.KeyActionHorizontal;
import dev.vcsocial.lazerwizard.config.keybindings.KeyBindingsConfiguration;
import dev.vcsocial.lazerwizard.config.mousebindings.CursorAction;
import dev.vcsocial.lazerwizard.config.mousebindings.MouseAction;
import dev.vcsocial.lazerwizard.config.mousebindings.MouseBindingsConfiguration;
import dev.vcsocial.lazerwizard.core.polling.signal.CursorInputSignal;
import dev.vcsocial.lazerwizard.core.polling.signal.InputSignal;
import dev.vcsocial.lazerwizard.core.polling.signal.KeyboardInputSignal;
import dev.vcsocial.lazerwizard.core.polling.signal.MouseButtonInputSignal;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2i;

@Singleton
public class InputSystem extends IteratingSystem implements Listener<InputSignal>, EntitySystemOrListener {

    private static final Logger LOGGER = LogManager.getLogger(InputSystem.class);

    private final MouseBindingsConfiguration mouseBindingsConfiguration;
    private final KeyBindingsConfiguration keyBindingsConfiguration;

    private Vector2i latestCursorDelta;
    private Vector2i latestMouseButton;
    private CursorAction latestCursorAction;
    private MouseAction latestMouseAction;
    private KeyActionManagement latestKeyActionManagement;
    private KeyActionVertical latestKeyActionVertical;
    private KeyActionHorizontal latestKeyActionHorizontal;

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
            LOGGER.debug("Set [latestCursorAction={}], [deltaX={}], [deltaY={}]", latestCursorAction,
                    latestCursorDelta.x, latestCursorDelta.y);
        }

        if (inputSignal instanceof MouseButtonInputSignal m) {
            latestMouseButton = new Vector2i(m.glfwMouseButton(), m.glfwAction());
            latestMouseAction = mouseBindingsConfiguration.getActionForMouseButton(m.glfwMouseButton(), m.glfwAction());
        }

        if (inputSignal instanceof KeyboardInputSignal k) {
            var binding = keyBindingsConfiguration.getActionForKey(k.glfwKey(), k.glfwAction());
            if (binding instanceof KeyActionManagement m) {
                latestKeyActionManagement = m;
            } else if (binding instanceof KeyActionVertical v) {
                latestKeyActionVertical = v;
            } else if (binding instanceof KeyActionHorizontal h) {
                latestKeyActionHorizontal = h;
            }
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var cursorInput = CursorInputComponent.COMPONENT_MAPPER.get(entity);
        var mouseButtonInput = MouseButtonInputComponent.COMPONENT_MAPPER.get(entity);
        var keyboardInput = KeyboardInputComponent.COMPONENT_MAPPER.get(entity);

        if (latestCursorAction != null) {
            cursorInput.cursorAction = latestCursorAction;
            latestCursorAction = CursorAction.STILL;
            LOGGER.trace("Set [cursorAction={}]", cursorInput.cursorAction);
        }

        if (latestMouseButton != null) {
            mouseButtonInput.glfwMouseButton = latestMouseButton.x;
            mouseButtonInput.glfwMouseAction = latestMouseButton.y;
            mouseButtonInput.mouseAction = latestMouseAction;
            LOGGER.trace("Set [mouseAction={}]", mouseButtonInput.mouseAction);
        }

        if (latestKeyActionManagement != null) {
            keyboardInput.keyActionManagement = latestKeyActionManagement;
            LOGGER.trace("Set [keyActionManagement={}]", keyboardInput.keyActionManagement);
        }

        if (latestKeyActionVertical != null) {
            keyboardInput.keyActionVertical = latestKeyActionVertical;
            LOGGER.trace("Set [keyActionVertical={}]", keyboardInput.keyActionVertical);
        }

        if (latestKeyActionHorizontal != null) {
            keyboardInput.keyActionHorizontal = latestKeyActionHorizontal;
            LOGGER.trace("Set [keyActionHorizontal={}]", keyboardInput.keyActionHorizontal);
        }
    }
}
