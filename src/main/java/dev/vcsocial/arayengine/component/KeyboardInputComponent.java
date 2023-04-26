package dev.vcsocial.arayengine.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import dev.vcsocial.arayengine.config.KeyAction;

public class KeyboardInputComponent implements Component {
    public static final ComponentMapper<KeyboardInputComponent> COMPONENT_MAPPER = ComponentMapper.getFor(KeyboardInputComponent.class);

    public KeyAction keyAction;

    public KeyboardInputComponent(KeyAction keyAction) {
        this.keyAction = keyAction;
    }
}