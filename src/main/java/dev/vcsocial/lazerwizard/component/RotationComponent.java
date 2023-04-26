package dev.vcsocial.lazerwizard.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class RotationComponent implements Component {
    public static final ComponentMapper<RotationComponent> COMPONENT_MAPPER = ComponentMapper.getFor(RotationComponent.class);

    public boolean isRotatingLeft;
    public boolean isRotatingRight;

    public RotationComponent() {
        this(false, false);

    }

    public RotationComponent(boolean isRotatingLeft, boolean isRotatingRight) {
        this.isRotatingLeft = isRotatingLeft;
        this.isRotatingRight = isRotatingRight;
    }
}
