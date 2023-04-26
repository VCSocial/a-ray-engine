package dev.vcsocial.lazerwizard.config.keybindings;

// TODO define repeatability here instead of in mapping so single map can be used
public enum KeyActionHorizontal implements KeyAction {
    INVALID(false),
    STRAFE_LEFT(true),
    STRAFE_RIGHT(true);

    private final boolean isRepeatable;

    KeyActionHorizontal(boolean isRepeatable) {
        this.isRepeatable = isRepeatable;
    }

    public boolean isActionRepeatable() {
        return isRepeatable;
    }
}
