package dev.vcsocial.lazerwizard.config.keybindings;

import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.lwjgl.glfw.GLFW;

@Singleton
public class KeyBindingsConfiguration {

    private static final Logger LOGGER = LogManager.getLogger(KeyBindingsConfiguration.class);

    private final UnifiedMap<IntIntPair, KeyAction> keyMappings;

    public KeyBindingsConfiguration() {
        // Note the collection used below can only be instantiated with up to four values
        keyMappings = UnifiedMap.newWithKeysValues(
                PrimitiveTuples.pair(GLFW.GLFW_KEY_W, GLFW.GLFW_PRESS), KeyActionVertical.FORWARD,
                PrimitiveTuples.pair(GLFW.GLFW_KEY_A, GLFW.GLFW_PRESS), KeyActionHorizontal.STRAFE_LEFT,
                PrimitiveTuples.pair(GLFW.GLFW_KEY_S, GLFW.GLFW_PRESS), KeyActionVertical.BACKWARD,
                PrimitiveTuples.pair(GLFW.GLFW_KEY_D, GLFW.GLFW_PRESS), KeyActionHorizontal.STRAFE_RIGHT
        );

        keyMappings.put(PrimitiveTuples.pair(GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_RELEASE), KeyActionManagement.QUIT_GAME );
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
        LOGGER.trace("Looking for [glfwKey={}], [glfwAction={}]", glfwKey, glfwAction);
        var action = keyMappings.get(PrimitiveTuples.pair(glfwKey, glfwAction));

        // Check for key release of pressable keys
        if (action == null && GLFW.GLFW_RELEASE == glfwAction) {
            action = keyMappings.get(PrimitiveTuples.pair(glfwKey, GLFW.GLFW_PRESS));
            if (action instanceof KeyActionVertical) {
                return KeyActionVertical.INVALID;
            } else if (action instanceof KeyActionHorizontal) {
                return KeyActionHorizontal.INVALID;
            }
        }

        // Continue key action if repeat is found for repeatable key
        if (action == null && GLFW.GLFW_REPEAT == glfwAction) {
            action = keyMappings.get(PrimitiveTuples.pair(glfwKey, GLFW.GLFW_PRESS));
            if (action.isActionRepeatable()) {
                return action;
            }
        }
        return action;
    }
}
