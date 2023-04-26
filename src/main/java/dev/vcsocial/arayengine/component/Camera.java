package dev.vcsocial.arayengine.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import org.joml.Vector2f;

public class Camera implements Component {
    public static final ComponentMapper<Camera> COMPONENT_MAPPER = ComponentMapper.getFor(Camera.class);

    public Vector2f direction;
    public Vector2f plane;
}
