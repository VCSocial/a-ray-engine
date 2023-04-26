package dev.vcsocial.arayengine.common;

import java.util.Objects;

public class GlColor {

    private static final float OPAQUE = 1f;
    private static final float TRANSPARENT = 1f;

    public static final GlColor RED = new GlColor(255, 0, 0);
    public static final GlColor GREEN = new GlColor(0, 255, 0);
    public static final GlColor BLUE = new GlColor(0, 0, 255);
    public static final GlColor YELLOW = new GlColor(255, 255, 0);
    public static final GlColor PURPLE = new GlColor(255, 0, 255);
    public static final GlColor WHITE = new GlColor(255, 255, 255);
    public static final GlColor BLACK = new GlColor(0, 0 ,0);

    private final float r;
    private final float g;
    private final float b;
    private final float alpha;

    public GlColor(int r, int g, int b) {
        this(r, g, b, OPAQUE);
    }

    public GlColor(int r, int g, int b, float alpha) {
        this.r = r / 255.0f;
        this.g = g / 255.0f;
        this.b = b / 255.0f;
        this.alpha = alpha;
    }

    public float getRed() {
        return r;
    }

    public float getGreen() {
        return g;
    }

    public float getBlue() {
        return b;
    }

    public float getAlpha() {
        return alpha;
    }

    public GlColor shadeBy(float shadeRatio) {
        return new GlColor((int) (r * shadeRatio * 255) , (int) (g * shadeRatio * 255), (int) (b * shadeRatio * 255));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlColor color = (GlColor) o;
        return Float.compare(color.r, r) == 0 && Float.compare(color.g, g) == 0 && Float.compare(color.b, b) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }
}
