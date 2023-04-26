package dev.vcsocial.arayengine.config;

// TODO define repeatability here instead of in mapping so single map can be used
public enum KeyAction {
    INVALID(false),
    FORWARD(true),
    BACKWARD(true),
    STRAFE_LEFT(true),
    STRAFE_RIGHT(true),
    QUIT_GAME(false);

    private final boolean isRepeatable;

    KeyAction(boolean isRepeatable) {
        this.isRepeatable = isRepeatable;
    }

    public boolean isActionRepeatable() {
        return isRepeatable;
    }
}
