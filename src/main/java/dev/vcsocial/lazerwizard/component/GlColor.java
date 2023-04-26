package dev.vcsocial.lazerwizard.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import java.util.Objects;

public class GlColor implements Component {
    private static final float OPAQUE = 1f;

    public static final ComponentMapper<GlColor> COMPONENT_MAPPER = ComponentMapper.getFor(GlColor.class);

    // TODO lazy loading
    public static final GlColor RED = new GlColor(255, 0, 0);
    public static final GlColor GREEN = new GlColor(0, 255, 0);
    public static final GlColor BLUE = new GlColor(0, 0, 255);
    public static final GlColor YELLOW = new GlColor(255, 255, 0);
    public static final GlColor PURPLE = new GlColor(255, 0, 255);
    public static final GlColor WHITE = new GlColor(255, 255, 255);
    public static final GlColor BLACK = new GlColor(0, 0 ,0);

    public float r;
    public float g;
    public float b;
    public float alpha;

    public GlColor(int r, int g, int b) {
        this(r / 255f, g / 255f, b / 255f, OPAQUE);
    }

    public GlColor(int r, int g, int b, float alpha) {
        this(r / 255f, g / 255f, b / 255f, alpha);
    }

    public GlColor(float r, float g, float b, float alpha) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.alpha = alpha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlColor glColor = (GlColor) o;
        return Float.compare(glColor.r, r) == 0
                && Float.compare(glColor.g, g) == 0
                && Float.compare(glColor.b, b) == 0
                && Float.compare(glColor.alpha, alpha) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b, alpha);
    }
}
