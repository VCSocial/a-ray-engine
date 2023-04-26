package dev.vcsocial.lazerwizard.config.keybindings;

// TODO define repeatability here instead of in mapping so single map can be used
public enum KeyActionVertical implements KeyAction {
    INVALID(false),
    FORWARD(true),
    BACKWARD(true);

    private final boolean isRepeatable;

    KeyActionVertical(boolean isRepeatable) {
        this.isRepeatable = isRepeatable;
    }

    public boolean isActionRepeatable() {
        return isRepeatable;
    }
}
