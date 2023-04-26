package dev.vcsocial.lazerwizard.config.keybindings;

public interface KeyAction {
    default boolean isActionRepeatable() {
        return false;
    }
}
