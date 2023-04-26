package dev.vcsocial.arayengine.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class PositionComponent implements Component {
    public static final ComponentMapper<PositionComponent> COMPONENT_MAPPER = ComponentMapper.getFor(PositionComponent.class);

    public float x;
    public float y;

    public PositionComponent() {
        this(0, 0);
    }

    public PositionComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
