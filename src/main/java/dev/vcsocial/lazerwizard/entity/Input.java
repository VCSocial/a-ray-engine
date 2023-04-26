package dev.vcsocial.lazerwizard.entity;

import dev.vcsocial.lazerwizard.component.CursorInputComponent;
import dev.vcsocial.lazerwizard.component.KeyboardInputComponent;
import dev.vcsocial.lazerwizard.component.MouseButtonInputComponent;

public class Input extends AbstractEntity {
    private final KeyboardInputComponent keyboardInputComponent;
    private final MouseButtonInputComponent mouseButtonInputComponent;
    private final CursorInputComponent cursorInputComponent;

    public Input(KeyboardInputComponent keyboardInputComponent, MouseButtonInputComponent mouseButtonInputComponent, CursorInputComponent cursorInputComponent) {
        this.keyboardInputComponent = keyboardInputComponent;
        this.mouseButtonInputComponent = mouseButtonInputComponent;
        this.cursorInputComponent = cursorInputComponent;
    }

    public KeyboardInputComponent getKeyboardInput() {
        return keyboardInputComponent;
    }

    public MouseButtonInputComponent getMouseButtonInput() {
        return mouseButtonInputComponent;
    }

    public CursorInputComponent getCursorInput() {
        return cursorInputComponent;
    }
}
