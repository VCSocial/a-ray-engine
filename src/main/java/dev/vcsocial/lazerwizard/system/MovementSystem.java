package dev.vcsocial.lazerwizard.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import dev.vcsocial.lazerwizard.component.KeyboardInputComponent;
import dev.vcsocial.lazerwizard.component.MovementComponent;
import dev.vcsocial.lazerwizard.component.PositionComponent;
import dev.vcsocial.lazerwizard.config.keybindings.KeyAction;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

// TODO decouple concepts and remove assumptions about positioning
// Initial POC attempt for movement
@Singleton
public class MovementSystem extends IteratingSystem implements EntitySystemOrListener {

    public static Logger LOGGER = LogManager.getLogger(MovementSystem.class);

    public MovementSystem() {
        super(Family.all(PositionComponent.class, MovementComponent.class, KeyboardInputComponent.class).get(), 1);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var input = KeyboardInputComponent.COMPONENT_MAPPER.get(entity);
        if (KeyAction.INVALID.equals(input.keyAction)) {
            return;
        }

        var movement = MovementComponent.COMPONENT_MAPPER.get(entity);

        // TODO release a key implementation
        if (KeyAction.FORWARD.equals(input.keyAction)) {
            movement.direction.y -= 1;
        }
        if (KeyAction.BACKWARD.equals(input.keyAction)) {
            movement.direction.y += 1;
        }
        if (KeyAction.STRAFE_LEFT.equals(input.keyAction)) {
            movement.direction.x -= 1;
        }
        if (KeyAction.STRAFE_RIGHT.equals(input.keyAction)) {
            movement.direction.x += 1;
        }

        if (movement.direction.x == 0 && movement.direction.y == 0) {
            return;
        }

        var normalDirection = new Vector3f(movement.direction, 0).normalize();
        var position = PositionComponent.COMPONENT_MAPPER.get(entity);

        position.x += normalDirection.x * movement.velocity * deltaTime;
        position.y += normalDirection.y * movement.velocity * deltaTime;


        movement.direction.x = 0;
        movement.direction.y = 0;
        LOGGER.info("[position.x={}], [position.y={}]", position.x, position.y);
    }
}
