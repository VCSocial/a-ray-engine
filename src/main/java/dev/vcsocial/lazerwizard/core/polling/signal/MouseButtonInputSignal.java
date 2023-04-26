package dev.vcsocial.lazerwizard.core.polling.signal;

public record MouseButtonInputSignal(int glfwMouseButton, int glfwAction) implements InputSignal {
}
