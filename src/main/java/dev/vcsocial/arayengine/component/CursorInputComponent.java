package dev.vcsocial.arayengine.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import dev.vcsocial.arayengine.config.mousebindings.CursorAction;

public class CursorInputComponent implements Component {
    public static final ComponentMapper<CursorInputComponent> COMPONENT_MAPPER = ComponentMapper.getFor(CursorInputComponent.class);

    public CursorAction cursorAction;
    public int deltaX;
    public int deltaY;

    public CursorInputComponent() {
        this(0, 0);
    }

    public CursorInputComponent(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }
}
