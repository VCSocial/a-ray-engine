package dev.vcsocial.arayengine.core.polling.signal;

public record KeyboardInputSignal(int glfwKey, int glfwAction) implements InputSignal {
}
