package dev.vcsocial.lazerwizard.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import org.joml.Vector2f;

public class CameraComponent implements Component {
    public static final ComponentMapper<CameraComponent> COMPONENT_MAPPER = ComponentMapper.getFor(CameraComponent.class);

    public static CameraComponent NORTH_FACING = new CameraComponent(new Vector2f(-1, 0), new Vector2f(0, 0.66f));
    public static CameraComponent SOUTH_FACING = new CameraComponent(new Vector2f(1, 0), new Vector2f(0, -0.66f));
    public static CameraComponent WEST_FACING = new CameraComponent(new Vector2f(0, -1), new Vector2f(0.66f, 0));
    public static CameraComponent EAST_FACING = new CameraComponent(new Vector2f(0, 1), new Vector2f(-0.66f, 0));

    public Vector2f direction;
    public Vector2f plane;

    public CameraComponent() {
        direction = new Vector2f(1, 0);
        plane = new Vector2f(0, -0.66f);
    }

    private CameraComponent(Vector2f direction, Vector2f plane) {
        this.direction = direction;
        this.plane = plane;
    }
}
