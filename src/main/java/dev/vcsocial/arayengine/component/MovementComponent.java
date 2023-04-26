package dev.vcsocial.arayengine.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import org.joml.Vector2f;

public class MovementComponent implements Component {
    public static final ComponentMapper<MovementComponent> COMPONENT_MAPPER = ComponentMapper.getFor(MovementComponent.class);

    public Vector2f direction;
    public float velocity;

    public MovementComponent(Vector2f direction, float velocity) {
        this.direction = direction;
        this.velocity = velocity;
    }
}
