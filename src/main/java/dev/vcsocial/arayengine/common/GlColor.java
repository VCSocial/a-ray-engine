package dev.vcsocial.arayengine.common;

public class GlColor {
    public static final GlColor RED = new GlColor(255, 0, 0);
    public static final GlColor GREEN = new GlColor(0, 255, 0);
    public static final GlColor BLUE = new GlColor(0, 0, 255);
    public static final GlColor YELLOW = new GlColor(255, 255, 0);
    public static final GlColor PURPLE = new GlColor(255, 0, 255);
    public static final GlColor WHITE = new GlColor(255, 255, 255);
    public static final GlColor BLACK = new GlColor(0, 0, 0);

    private float r;
    private float g;
    private float b;

    public GlColor(int r, int g, int b) {
        this.r = r / 255.0f;
        this.g = g / 255.0f;
        this.b = b / 255.0f;
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

    public void shadeBy(float shadeRatio) {
        r *=  shadeRatio;
        g *= shadeRatio;
        b *= shadeRatio;
    }
}
