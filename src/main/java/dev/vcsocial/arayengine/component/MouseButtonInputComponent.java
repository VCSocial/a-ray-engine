package dev.vcsocial.arayengine.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class MouseButtonInputComponent implements Component {
    public static final ComponentMapper<MouseButtonInputComponent> COMPONENT_MAPPER = ComponentMapper.getFor(MouseButtonInputComponent.class);

    public int glfwMouseButton;
    public int glfwMouseAction;
    public int glfwMouseModifier;

    public MouseButtonInputComponent() {
        this(-1, -1, -1);
    }

    public MouseButtonInputComponent(int glfwMouseButton, int glfwMouseAction, int glfwMouseModifier) {
        this.glfwMouseButton = glfwMouseButton;
        this.glfwMouseAction = glfwMouseAction;
        this.glfwMouseModifier = glfwMouseModifier;
    }
}
