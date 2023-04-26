package dev.vcsocial.lazerwizard.core.manager;

import com.badlogic.ashley.core.*;
import dev.vcsocial.lazerwizard.component.*;
import dev.vcsocial.lazerwizard.config.keybindings.KeyAction;
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
                .withCursorInput(new CursorInputComponent(0,0))
                .withMouseButtonInput(new MouseButtonInputComponent(-1, -1, -1))
                .withKeyboardInput(new KeyboardInputComponent(KeyAction.INVALID))
                .withPosition(new PositionComponent(1,1))
                .withMovement(new MovementComponent(new Vector2f(0, 0), 1))
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
