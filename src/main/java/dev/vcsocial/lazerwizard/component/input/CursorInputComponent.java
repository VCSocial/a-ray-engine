package dev.vcsocial.lazerwizard.component.input;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import dev.vcsocial.lazerwizard.config.mousebindings.CursorAction;

public class CursorInputComponent implements Component {
    public static final ComponentMapper<CursorInputComponent> COMPONENT_MAPPER = ComponentMapper.getFor(CursorInputComponent.class);

    public CursorAction cursorAction = CursorAction.STILL;
}
