package dev.vcsocial.lazerwizard.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import dev.vcsocial.lazerwizard.component.input.KeyboardInputComponent;
import dev.vcsocial.lazerwizard.config.keybindings.KeyActionManagement;
import dev.vcsocial.lazerwizard.core.manager.window.WindowManager;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Singleton
public class MetaControlSystem extends IteratingSystem implements EntitySystemOrListener {

    private static final Logger LOGGER = LogManager.getLogger(MetaControlSystem.class);

    private final WindowManager windowManager;

    public MetaControlSystem(WindowManager windowManager) {
        super(Family.all(KeyboardInputComponent.class).get(), 1);
        this.windowManager = windowManager;
    }

    private void quit() {
        LOGGER.debug("Found quit input, attempting to quit!");
        GLFW.glfwSetWindowShouldClose(windowManager.getWindow(), true);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var keyInput = KeyboardInputComponent.COMPONENT_MAPPER.get(entity);

        LOGGER.trace("[keyInput={}]", keyInput);
        if (KeyActionManagement.QUIT_GAME.equals(keyInput.keyActionManagement)) {
            quit();
        }
    }
}
