package dev.vcsocial.lazerwizard.entity;

import com.badlogic.ashley.core.Entity;
import dev.vcsocial.lazerwizard.component.*;
import dev.vcsocial.lazerwizard.component.input.CursorInputComponent;
import dev.vcsocial.lazerwizard.component.input.KeyboardInputComponent;
import dev.vcsocial.lazerwizard.component.input.MouseButtonInputComponent;

public class Player extends Entity {

    public Player(Builder builder) {
        add(builder.lineMeshGroup);
        add(builder.cameraComponent);
        add(builder.movementComponent);
        add(builder.positionComponent);
        add(builder.cursorInputComponent);
        add(builder.mouseButtonInputComponent);
        add(builder.keyboardInputComponent);
    }

    public static class Builder {

        private CameraComponent cameraComponent;
        private MovementComponent movementComponent;
        private PositionComponent positionComponent;
        private CursorInputComponent cursorInputComponent;
        private MouseButtonInputComponent mouseButtonInputComponent;
        private KeyboardInputComponent keyboardInputComponent;
        private LineMeshGroup lineMeshGroup;

        public Builder withCamera(CameraComponent cameraComponent) {
            this.cameraComponent = cameraComponent;
            return this;
        }

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

        public Builder withLineMeshGroup(LineMeshGroup lineMeshGroup) {
            this.lineMeshGroup = lineMeshGroup;
            return this;
        }

        public Player build() {
            return new Player(this);
        }
    }
}
