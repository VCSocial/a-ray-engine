package dev.vcsocial.arayengine.common;

import java.awt.*;

public class GlColorCompatible extends Color {

    public GlColorCompatible(int r, int g, int b) {
        super(r, g, b);
    }

    public float getRedAsFloat() {
        return getRed() / 255f;
    }

    public float getGreenAsFloat() {
        return getGreen() / 255f;
    }

    public float getBlueAsFloat() {
        return getBlue() / 255f;
    }
}
