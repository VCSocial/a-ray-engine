package dev.vcsocial.lazerwizard.component.input;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import dev.vcsocial.lazerwizard.config.mousebindings.MouseAction;

public class MouseButtonInputComponent implements Component {
    public static final ComponentMapper<MouseButtonInputComponent> COMPONENT_MAPPER = ComponentMapper.getFor(MouseButtonInputComponent.class);

    public MouseAction mouseAction;
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
