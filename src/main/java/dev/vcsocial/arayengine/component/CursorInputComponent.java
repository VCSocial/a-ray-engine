package dev.vcsocial.arayengine.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class CursorInputComponent implements Component {
    public static final ComponentMapper<CursorInputComponent> COMPONENT_MAPPER = ComponentMapper.getFor(CursorInputComponent.class);

    public int deltaX;
    public int deltaY;

    public CursorInputComponent(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }
}
