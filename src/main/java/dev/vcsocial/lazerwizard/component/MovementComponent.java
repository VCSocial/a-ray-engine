package dev.vcsocial.lazerwizard.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import org.joml.Vector2f;

public class MovementComponent implements Component {
    public static final ComponentMapper<MovementComponent> COMPONENT_MAPPER = ComponentMapper.getFor(MovementComponent.class);

    public Vector2f direction;
    public float velocity;

    public MovementComponent() {
        this(new Vector2f(0, 0), 0);
    }

    public MovementComponent(float velocity) {
        this(new Vector2f(0, 0), velocity);
    }

    public MovementComponent(Vector2f direction, float velocity) {
        this.direction = direction;
        this.velocity = velocity;
    }
}
