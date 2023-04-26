package dev.vcsocial.arayengine.entity;

import dev.vcsocial.arayengine.component.KeyboardInputComponent;

public class Keyboard extends AbstractEntity{
    private final KeyboardInputComponent keyboardInputComponent;

    public Keyboard(KeyboardInputComponent keyboardInputComponent) {
        this.keyboardInputComponent = keyboardInputComponent;
    }

    public KeyboardInputComponent getKeyboardInput() {
        return keyboardInputComponent;
    }
}
