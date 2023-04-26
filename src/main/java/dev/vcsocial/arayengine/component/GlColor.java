package dev.vcsocial.arayengine.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public record GlColor(int r, int g, int b, int alpha) implements Component {
    public static final ComponentMapper<GlColor> COMPONENT_MAPPER = ComponentMapper.getFor(GlColor.class);
}
