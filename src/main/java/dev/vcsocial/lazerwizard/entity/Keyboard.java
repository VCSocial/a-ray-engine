package dev.vcsocial.lazerwizard.entity;

import dev.vcsocial.lazerwizard.component.input.KeyboardInputComponent;

public class Keyboard extends AbstractEntity{
    private final KeyboardInputComponent keyboardInputComponent;

    public Keyboard(KeyboardInputComponent keyboardInputComponent) {
        this.keyboardInputComponent = keyboardInputComponent;
    }

    public KeyboardInputComponent getKeyboardInput() {
        return keyboardInputComponent;
    }
}
