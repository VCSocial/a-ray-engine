package dev.vcsocial.arayengine.config.mousebindings;

public enum MouseAction {
    INVALID(false, false),
    FIRE(true, false),
    ZOOM_IN(false, true),
    SCROLL_UP(false, false),
    SCROLL_DOWN(false, false),
    SCROLL_CLICK(false, false);

    private final boolean isRepeatable;
    private final boolean isToggleable;

    MouseAction(boolean isRepeatable, boolean isToggleable) {
        this.isRepeatable = isRepeatable;
        this.isToggleable = isToggleable;
    }

    public boolean isActionRepeatable() {
        return isRepeatable;
    }

    public boolean isActionToggleable() {
        return isToggleable;
    }
}
