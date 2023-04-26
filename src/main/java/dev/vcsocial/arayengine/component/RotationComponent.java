package dev.vcsocial.arayengine.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class RotationComponent implements Component {
    public static final ComponentMapper<RotationComponent> COMPONENT_MAPPER = ComponentMapper.getFor(RotationComponent.class);

    public boolean isRotatingLeft = false;
    public boolean isRotatingRight = false;
}
