package dev.vcsocial.arayengine.entity;

import com.badlogic.ashley.core.Entity;
import dev.vcsocial.arayengine.component.*;

public class Player extends Entity {

    public Player(Builder builder) {
        add(builder.movementComponent);
        add(builder.positionComponent);
        add(builder.cursorInputComponent);
        add(builder.mouseButtonInputComponent);
        add(builder.keyboardInputComponent);
    }

    public static class Builder {

        private MovementComponent movementComponent;
        private PositionComponent positionComponent;
        private CursorInputComponent cursorInputComponent;
        private MouseButtonInputComponent mouseButtonInputComponent;
        private KeyboardInputComponent keyboardInputComponent;


        public Builder withMovement(MovementComponent movement) {
            movementComponent = movement;
            return this;
        }

        public Builder withPosition(PositionComponent position) {
            positionComponent = position;
            return this;
        }

        public Builder withCursorInput(CursorInputComponent cursorInputComponent) {
            this.cursorInputComponent = cursorInputComponent;
            return this;
        }

        public Builder withMouseButtonInput(MouseButtonInputComponent mouseButtonInputComponent) {
            this.mouseButtonInputComponent = mouseButtonInputComponent;
            return this;
        }

        public Builder withKeyboardInput(KeyboardInputComponent keyboardInputComponent) {
            this.keyboardInputComponent = keyboardInputComponent;
            return this;
        }

        public Player build() {
            return new Player(this);
        }
    }
}
