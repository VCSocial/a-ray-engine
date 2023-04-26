package dev.vcsocial.arayengine.core.polling.signal;

public record MouseButtonInputSignal(int glfwMouseButton, int glfwAction) implements InputSignal {
}
