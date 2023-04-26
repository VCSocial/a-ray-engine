package dev.vcsocial.arayengine.config.keybindings;

import jakarta.inject.Singleton;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.lwjgl.glfw.GLFW;

@Singleton
public class KeyBindingsConfiguration {

    private final UnifiedMap<IntIntPair, KeyAction> keyMappings;

    public KeyBindingsConfiguration() {
        // Note the collection used below can only be instantiated with up to four values
        keyMappings = UnifiedMap.newWithKeysValues(
                PrimitiveTuples.pair(GLFW.GLFW_KEY_W, GLFW.GLFW_PRESS), KeyAction.FORWARD,
                PrimitiveTuples.pair(GLFW.GLFW_KEY_A, GLFW.GLFW_PRESS), KeyAction.STRAFE_LEFT,
                PrimitiveTuples.pair(GLFW.GLFW_KEY_S, GLFW.GLFW_PRESS), KeyAction.BACKWARD,
                PrimitiveTuples.pair(GLFW.GLFW_KEY_D, GLFW.GLFW_PRESS), KeyAction.STRAFE_RIGHT
        );

        keyMappings.put(PrimitiveTuples.pair(GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_RELEASE), KeyAction.QUIT_GAME );
    }

    public void replaceKeyMapping(KeyAction action, int glfwKey, int glfwAction) {
        // Expensive but only done on a remap
        var mappingsByAction = keyMappings.flipUniqueValues();

        if (mappingsByAction.containsKey(action)) {
            var keys = mappingsByAction.get(action);
            keyMappings.remove(keys);
            keyMappings.put(PrimitiveTuples.pair(glfwKey, glfwAction), action);
        }
    }

    public KeyAction getActionForKey(int glfwKey, int glfwAction) {
        return keyMappings.get(PrimitiveTuples.pair(glfwKey, glfwAction));
    }
}
