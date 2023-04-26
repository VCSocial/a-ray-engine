package dev.vcsocial.lazerwizard.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import dev.vcsocial.lazerwizard.component.CameraComponent;
import dev.vcsocial.lazerwizard.component.MovementComponent;
import dev.vcsocial.lazerwizard.component.PositionComponent;
import dev.vcsocial.lazerwizard.component.TileType;
import dev.vcsocial.lazerwizard.component.input.CursorInputComponent;
import dev.vcsocial.lazerwizard.component.input.KeyboardInputComponent;
import dev.vcsocial.lazerwizard.config.keybindings.KeyActionHorizontal;
import dev.vcsocial.lazerwizard.config.keybindings.KeyActionVertical;
import dev.vcsocial.lazerwizard.config.mousebindings.CursorAction;
import dev.vcsocial.lazerwizard.core.manager.tile.TileMapManager;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;

import static org.joml.Math.cos;
import static org.joml.Math.sin;

// TODO decouple concepts and remove assumptions about positioning
// Initial POC attempt for movement
@Singleton
public class MovementSystem extends IteratingSystem implements EntitySystemOrListener {

    private static final Logger LOGGER = LogManager.getLogger(MovementSystem.class);

    private final TileMapManager tileMapManager;

    public MovementSystem(TileMapManager tileMapManager) {
        super(Family.all(CameraComponent.class, PositionComponent.class, MovementComponent.class,
                KeyboardInputComponent.class).get(), 1);

        this.tileMapManager = tileMapManager;
    }
    /*
//    public void rotateLeft() {
//        double oldDirectionX = direction.x;
//        direction.x = direction.x * cos(getRotationSpeed()) - direction.y * sin(getRotationSpeed());
//        direction.y = oldDirectionX * sin(getRotationSpeed()) + direction.y * cos(getRotationSpeed());
//
//        double oldPlaneX = plane.x;
//        plane.x = plane.x * cos(getRotationSpeed()) - plane.y * sin(getRotationSpeed());
//        plane.y = oldPlaneX * sin(getRotationSpeed()) + plane.y * cos(getRotationSpeed());
//    }
//
//    public void rotateRight() {
//        double oldDirectionX = direction.x;
//        direction.x = direction.x * cos(-getRotationSpeed()) - direction.y * sin(-getRotationSpeed());
//        direction.y = oldDirectionX * sin(-getRotationSpeed()) + direction.y * cos(-getRotationSpeed());
//
//        double oldPlaneX = plane.x;
//        plane.x = plane.x * cos(-getRotationSpeed()) - plane.y * sin(-getRotationSpeed());
//        plane.y = oldPlaneX * sin(-getRotationSpeed()) + plane.y * cos(-getRotationSpeed());
//    }
     */

    private void rotateLeft(Vector2f direction, Vector2f plane, float deltaTime) {
        float speed = 2f;
        float rotSpeed = deltaTime * speed;

        float tempDirectionX = direction.x;
        direction.x =  direction.x * cos(rotSpeed) - direction.y * sin(rotSpeed);
        direction.y = tempDirectionX * sin(rotSpeed) + direction.y * cos(rotSpeed);

        float tempPlaneX = plane.x;
        plane.x = plane.x * cos(rotSpeed) - plane.y * sin(rotSpeed);
        plane.y = tempPlaneX * sin(rotSpeed) + plane.y * cos(rotSpeed);
    }

    private void rotateRight(Vector2f direction, Vector2f plane, float deltaTime) {
        float speed = 2f;
        float rotSpeed = deltaTime * speed;

        float tempDirectionX = direction.x;
        direction.x =  direction.x * cos(-rotSpeed) - direction.y * sin(-rotSpeed);
        direction.y = tempDirectionX * sin(-rotSpeed) + direction.y * cos(-rotSpeed);

        float tempPlaneX = plane.x;
        plane.x = plane.x * cos(-rotSpeed) - plane.y * sin(-rotSpeed);
        plane.y = tempPlaneX * sin(-rotSpeed) + plane.y * cos(-rotSpeed);
    }


    private Vector2f forward(PositionComponent position, Vector2f direction, float velocity, float deltaTime) {
        var movement = new Vector2f(
                position.x + direction.x * velocity * deltaTime,
                position.y + direction.y * velocity * deltaTime
        );

        movement.x = !TileType.WALL.equals(tileMapManager.getTileTypeAt((int) movement.x, (int) position.y))
                ? direction.x * velocity
                : position.x;

        movement.y = !TileType.WALL.equals(tileMapManager.getTileTypeAt((int) position.x, (int) movement.y))
                ? direction.y * velocity
                : position.y;

        return movement;
    }

    private Vector2f backward(PositionComponent position, Vector2f direction, float velocity, float deltaTime) {
        var movement = new Vector2f(
                position.x - direction.x * velocity * deltaTime,
                position.y - direction.y * velocity * deltaTime
        );

        movement.x = !TileType.WALL.equals(tileMapManager.getTileTypeAt((int) movement.x, (int) position.y))
                ? direction.x * velocity
                : position.x;

        movement.y = !TileType.WALL.equals(tileMapManager.getTileTypeAt((int) position.x, (int) movement.y))
                ? direction.y * velocity
                : position.y;

        return movement;
    }

    private Vector2f left(PositionComponent position, Vector2f plane, float velocity, float deltaTime) {
        var movement = new Vector2f(
                position.x - plane.x * velocity * deltaTime,
                position.y - plane.y * velocity * deltaTime
        );

        movement.x = !TileType.WALL.equals(tileMapManager.getTileTypeAt((int) movement.x, (int) position.y))
                ? plane.x * velocity
                : position.x;

        movement.y = !TileType.WALL.equals(tileMapManager.getTileTypeAt((int) position.x, (int) movement.y))
                ? plane.y * velocity
                : position.y;

        return movement;
    }

    private Vector2f right(PositionComponent position, Vector2f plane, float velocity, float deltaTime) {
        var movement = new Vector2f(
                position.x + plane.x * velocity * deltaTime,
                position.y + plane.y * velocity * deltaTime
        );

        movement.x = !TileType.WALL.equals(tileMapManager.getTileTypeAt((int) movement.x, (int) position.y))
                ? plane.x * velocity
                : position.x;

        movement.y = !TileType.WALL.equals(tileMapManager.getTileTypeAt((int) position.x, (int) movement.y))
                ? plane.y * velocity
                : position.y;

        return movement;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var keyboardInputComponent = KeyboardInputComponent.COMPONENT_MAPPER.get(entity);
        var movement = MovementComponent.COMPONENT_MAPPER.get(entity);
        var direction = CameraComponent.COMPONENT_MAPPER.get(entity).direction;
        var plane = CameraComponent.COMPONENT_MAPPER.get(entity).plane;
        var position = PositionComponent.COMPONENT_MAPPER.get(entity);
        var cursorInputComponent = CursorInputComponent.COMPONENT_MAPPER.get(entity);

        if (CursorAction.LEFT.equals(cursorInputComponent.cursorAction)) {
            rotateLeft(direction, plane, movement.velocity);
            LOGGER.debug("Rotating Left");
        } else if (CursorAction.RIGHT.equals(cursorInputComponent.cursorAction)) {
            rotateRight(direction, plane, movement.velocity);
            LOGGER.debug("Rotating Right");
        }

        if (KeyActionVertical.FORWARD.equals(keyboardInputComponent.keyActionVertical)) {
            var m = forward(position, direction, movement.velocity, deltaTime);
            position.x += m.x;
            position.y += m.y;
        } else if (KeyActionVertical.BACKWARD.equals(keyboardInputComponent.keyActionVertical)) {
            var m = backward(position, direction, movement.velocity, deltaTime);
            position.x -= m.x;
            position.y -= m.y;
        }

        if (KeyActionHorizontal.STRAFE_LEFT.equals(keyboardInputComponent.keyActionHorizontal)) {
            var m = left(position, plane, movement.velocity, deltaTime);
            position.x -= m.x;
            position.y -= m.y;
        } else if (KeyActionHorizontal.STRAFE_RIGHT.equals(keyboardInputComponent.keyActionHorizontal)) {
            var m = right(position, plane, movement.velocity, deltaTime);
            position.x += m.x;
            position.y += m.y;
        }


        movement.direction.x = 0;
        movement.direction.y = 0;
    }
}
