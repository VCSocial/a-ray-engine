package dev.vcsocial.arayengine.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import org.joml.Vector2d;

public record Camera(Vector2d position, Vector2d direction, Vector2d plane) implements Component {
    public static final ComponentMapper<Camera> COMPONENT_MAPPER = ComponentMapper.getFor(Camera.class);
}
