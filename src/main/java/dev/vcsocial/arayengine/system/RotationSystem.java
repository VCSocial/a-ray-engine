package dev.vcsocial.arayengine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import dev.vcsocial.arayengine.component.RotationComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RotationSystem extends IteratingSystem implements EntitySystemOrListener {

    private static final Logger LOGGER = LogManager.getLogger(RotationSystem.class);

    public RotationSystem() {
        super(Family.all(RotationComponent.class).get());
    }

    private void onLeft() {
        System.out.println("On left");
    }

    private void onRight() {
        System.out.println("On Right");
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var rotation = RotationComponent.COMPONENT_MAPPER.get(entity);

        if (rotation.isRotatingLeft) {
            onLeft();
        }
        if (rotation.isRotatingRight) {
            onRight();
        }
    }
}
