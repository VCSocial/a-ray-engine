package dev.vcsocial.lazerwizard.core.polling.signal;

public record KeyboardInputSignal(int glfwKey, int glfwAction) implements InputSignal {
}
