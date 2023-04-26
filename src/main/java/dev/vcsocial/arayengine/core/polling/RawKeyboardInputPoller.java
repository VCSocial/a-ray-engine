package dev.vcsocial.arayengine.core.polling;

import com.badlogic.ashley.signals.Signal;
import dev.vcsocial.arayengine.core.helper.lifecycle.LifeCycleEventBroker;
import dev.vcsocial.arayengine.core.helper.lifecycle.PostWindowInitializationEvent;
import dev.vcsocial.arayengine.core.manager.WindowManager;
import dev.vcsocial.arayengine.core.polling.signal.InputSignal;
import dev.vcsocial.arayengine.core.polling.signal.KeyboardInputSignal;
import dev.vcsocial.arayengine.system.InputSystem;
import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

@Singleton
public class RawKeyboardInputPoller implements RawPoller {

    private static final Logger LOGGER = LogManager.getLogger(RawKeyboardInputPoller.class);

    private final WindowManager windowManager;
    private final LifeCycleEventBroker lifeCycleEventBroker;
    private final Signal<InputSignal> keyboardInputDetected;

    public RawKeyboardInputPoller(WindowManager windowManager, LifeCycleEventBroker eventQueue,
                                  InputSystem inputSystem) {
        this.windowManager = windowManager;
        this.lifeCycleEventBroker = eventQueue;
        keyboardInputDetected = new Signal<>();

        keyboardInputDetected.add(inputSystem);
        lifeCycleEventBroker.registerConsumer(this.getClass(), PostWindowInitializationEvent.class);
    }

    @PostConstruct
    public void initialize() {
        while (!lifeCycleEventBroker.containsEvent(PostWindowInitializationEvent.class)) {
            // wait
            LOGGER.trace("Waiting on window initialization before registering keyboard callbacks");
        }
        lifeCycleEventBroker.acknowledgeEvent(this.getClass(), PostWindowInitializationEvent.class);
        long window = windowManager.getWindow();

        // Handle single key actions
        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            keyboardInputDetected.dispatch(new KeyboardInputSignal(key, action));
            LOGGER.trace("Callback captured [key={}], with [action={}]", key, action);
        });
    }
}
