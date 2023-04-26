package dev.vcsocial.lazerwizard.component.input;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import dev.vcsocial.lazerwizard.config.keybindings.KeyActionHorizontal;
import dev.vcsocial.lazerwizard.config.keybindings.KeyActionManagement;
import dev.vcsocial.lazerwizard.config.keybindings.KeyActionVertical;

public class KeyboardInputComponent implements Component {
    public static final ComponentMapper<KeyboardInputComponent> COMPONENT_MAPPER = ComponentMapper.getFor(KeyboardInputComponent.class);

    public KeyActionManagement keyActionManagement;

    public KeyActionVertical keyActionVertical;
    public KeyActionHorizontal keyActionHorizontal;

    public KeyboardInputComponent() {
        keyActionManagement = KeyActionManagement.INVALID;
        keyActionVertical = KeyActionVertical.INVALID;
        keyActionHorizontal = KeyActionHorizontal.INVALID;
    }
}
