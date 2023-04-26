package dev.vcsocial.arayengine.component.enumeration;

import org.lwjgl.glfw.GLFW;

public enum GlfwMouseButton {
    GLFW_MOUSE_BUTTON_1(GLFW.GLFW_MOUSE_BUTTON_1),
    GLFW_MOUSE_BUTTON_2(GLFW.GLFW_MOUSE_BUTTON_2),
    GLFW_MOUSE_BUTTON_3(GLFW.GLFW_MOUSE_BUTTON_3),
    GLFW_MOUSE_BUTTON_4(GLFW.GLFW_MOUSE_BUTTON_4),
    GLFW_MOUSE_BUTTON_5(GLFW.GLFW_MOUSE_BUTTON_5),
    GLFW_MOUSE_BUTTON_6(GLFW.GLFW_MOUSE_BUTTON_6),
    GLFW_MOUSE_BUTTON_7(GLFW.GLFW_MOUSE_BUTTON_7),
    GLFW_MOUSE_BUTTON_8(GLFW.GLFW_MOUSE_BUTTON_8),
    GLFW_MOUSE_BUTTON_LAST(GLFW.GLFW_MOUSE_BUTTON_8),
    GLFW_MOUSE_BUTTON_LEFT(GLFW.GLFW_MOUSE_BUTTON_1),
    GLFW_MOUSE_BUTTON_RIGHT(GLFW.GLFW_MOUSE_BUTTON_2),
    GLFW_MOUSE_BUTTON_MIDDLE(GLFW.GLFW_MOUSE_BUTTON_3);

    private final int glfwInternalMapping;

    GlfwMouseButton(int glfwInternalMapping) {
        this.glfwInternalMapping = glfwInternalMapping;
    }

    public int getGlfwInternalMapping() {
        return this.glfwInternalMapping;
    }
}
