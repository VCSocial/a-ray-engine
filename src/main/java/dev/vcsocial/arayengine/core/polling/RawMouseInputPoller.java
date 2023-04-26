package dev.vcsocial.arayengine.core.polling;

import com.badlogic.ashley.signals.Signal;
import dev.vcsocial.arayengine.core.helper.lifecycle.LifeCycleEventBroker;
import dev.vcsocial.arayengine.core.helper.lifecycle.PostWindowInitializationEvent;
import dev.vcsocial.arayengine.core.manager.WindowManager;
import dev.vcsocial.arayengine.core.polling.signal.CursorInputSignal;
import dev.vcsocial.arayengine.core.polling.signal.InputSignal;
import dev.vcsocial.arayengine.core.polling.signal.MouseButtonInputSignal;
import dev.vcsocial.arayengine.system.InputSystem;
import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

@Singleton
public class RawMouseInputPoller implements PollingManager {

    private static final Logger LOGGER = LogManager.getLogger(RawMouseInputPoller.class);

    private final WindowManager windowManager;
    private final LifeCycleEventBroker lifeCycleEventBroker;

    private final Signal<InputSignal> mouseInputDetected;
    private final Vector2d previousPosition;

    private boolean isCursorWithinWindow;

    public RawMouseInputPoller(WindowManager windowManager, LifeCycleEventBroker lifeCycleEventBroker,
                               InputSystem inputSystem) {
        this.windowManager = windowManager;
        this.lifeCycleEventBroker = lifeCycleEventBroker;
        mouseInputDetected = new Signal<>();
        previousPosition = new Vector2d(-1, -1);

        mouseInputDetected.add(inputSystem);
        lifeCycleEventBroker.registerConsumer(this.getClass(), PostWindowInitializationEvent.class);
    }

    @PostConstruct
    public void initialize() {
        while (!lifeCycleEventBroker.containsEvent(PostWindowInitializationEvent.class)) {
            // TODO something better than a busy loop, maybe completable future could be used here
            LOGGER.trace("Waiting on window initialization before registering keyboard callbacks");
        }
        lifeCycleEventBroker.acknowledgeEvent(this.getClass(), PostWindowInitializationEvent.class);
        long window = windowManager.getWindow();

        GLFW.glfwSetCursorEnterCallback(window, (w, entered) -> {
            isCursorWithinWindow = entered;
            LOGGER.trace("[isCursorWithinWindow={}]", isCursorWithinWindow);
        });

        GLFW.glfwSetCursorPosCallback(window, (w, positionX, positionY) -> {
            if (isCursorWithinWindow) {
                LOGGER.trace("Incoming [positionX={}] [positionY={}]", positionX, positionY);
                double deltaX = 0;
                double deltaY = 0;

                if (previousPosition.x > 0 && previousPosition.y > 0) {
                    deltaX = (positionX - previousPosition.x);
                    deltaY = (positionY - previousPosition.y);
                    LOGGER.trace("[deltaX={}] [deltaY={}]", deltaX, deltaY);
                }

                if (deltaX != 0 || deltaY != 0) {
                    mouseInputDetected.dispatch(new CursorInputSignal(deltaX <= -1 ? -1 : 1, deltaY <= -1 ? -1 : 1));
                    LOGGER.trace("Dispatched mouse input detection");
                }

                previousPosition.x = positionX;
                previousPosition.y = positionY;
                LOGGER.trace("Set previousPosition [previousPosition.x={}] [previousPosition.y={}]",
                        previousPosition.x, previousPosition.y);
            }
        });

        GLFW.glfwSetMouseButtonCallback(window, (w, button, action, mods) -> {
            if (isCursorWithinWindow) {
                LOGGER.trace("Incoming [button={}] [action={}]", button, action);
                mouseInputDetected.dispatch(new MouseButtonInputSignal(button, action));
            }
        });
    }
}
