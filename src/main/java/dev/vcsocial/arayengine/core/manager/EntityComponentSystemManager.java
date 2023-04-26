package dev.vcsocial.arayengine.core.manager;

import com.badlogic.ashley.core.*;
import dev.vcsocial.arayengine.component.*;
import dev.vcsocial.arayengine.config.KeyAction;
import dev.vcsocial.arayengine.entity.Player;
import dev.vcsocial.arayengine.system.EntitySystemOrListener;
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

        var p = engine.createEntity();

        var player = new Player();
        player.add(new KeyboardInputComponent(KeyAction.INVALID));
        player.add(new MouseButtonInputComponent(-1, -1, -1));
        player.add(new CursorInputComponent(0,0));
        player.add(new PositionComponent(1,1));
        player.add(new MovementComponent(new Vector2f(0, 0), 1));

        engine.addEntity(player);
//
//
//        engine.addEntity(new Player(
//                new Camera(
//                        new Vector2d(1, 1),
//                        new Vector2d(-1, 0),
//                        new Vector2d(0, 0.66)
//                ),
//                new CursorInputComponent(0,0),
//                new MouseButtonInputComponent(-1, -1, -1),
//                new KeyboardInputComponent(KeyAction.INVALID),
//                new PositionComponent(1,1),
//                new MovementComponent(new Vector2f(0, 0), 1),
//                new RotationComponent()
//        ));
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
