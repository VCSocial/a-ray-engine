package dev.vcsocial.lazerwizard.core.manager;

import com.badlogic.ashley.core.*;
import dev.vcsocial.lazerwizard.component.CameraComponent;
import dev.vcsocial.lazerwizard.component.LineMeshGroup;
import dev.vcsocial.lazerwizard.component.MovementComponent;
import dev.vcsocial.lazerwizard.component.PositionComponent;
import dev.vcsocial.lazerwizard.component.input.CursorInputComponent;
import dev.vcsocial.lazerwizard.component.input.KeyboardInputComponent;
import dev.vcsocial.lazerwizard.component.input.MouseButtonInputComponent;
import dev.vcsocial.lazerwizard.entity.Player;
import dev.vcsocial.lazerwizard.system.EntitySystemOrListener;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;

import java.util.List;

@Singleton
public class EntityComponentSystemManager {

    private static final Logger LOGGER = LogManager.getLogger(EntityComponentSystemManager.class);

    private final FrameManager frameManager;
    private final Engine engine;

    public EntityComponentSystemManager(FrameManager frameManager,
                                        List<EntitySystemOrListener> entitySystemOrListenerList) {
        this.frameManager = frameManager;
        engine = new PooledEngine();

        entitySystemOrListenerList.forEach(r -> {
            if (r instanceof EntitySystem es) {
                engine.addSystem(es);
            }
            if (r instanceof EntityListener el) {
                engine.addEntityListener(el);
            }
        });

        var player = new Player.Builder()
                .withCamera(CameraComponent.SOUTH_FACING)
                .withCursorInput(new CursorInputComponent())
                .withMouseButtonInput(new MouseButtonInputComponent())
                .withKeyboardInput(new KeyboardInputComponent())
                .withPosition(new PositionComponent(2,2))
                .withMovement(new MovementComponent(new Vector2f(0, 0), 0.03f))
                .withLineMeshGroup(new LineMeshGroup())
                .build();

        engine.addEntity(player);
    }

    public void update() {
        engine.update(frameManager.getDeltaTime());
    }

    public void createEntityWithComponent(Component component) {
        var entity = engine.createEntity();
        entity.add(component);
        engine.addEntity(entity);
        LOGGER.debug("Entity [entity={}] submitted with [components={}]", entity, entity.getComponents());
    }
}
