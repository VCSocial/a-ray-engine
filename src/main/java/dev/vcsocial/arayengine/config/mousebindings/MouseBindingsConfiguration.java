package dev.vcsocial.arayengine.config.mousebindings;

import jakarta.inject.Singleton;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.lwjgl.glfw.GLFW;

@Singleton
public class MouseBindingsConfiguration {

    private final UnifiedMap<IntIntPair, CursorAction> cursorMappings;
    private final UnifiedMap<IntIntPair, MouseAction> mouseMappings;

    public MouseBindingsConfiguration() {
        cursorMappings = UnifiedMap.newWithKeysValues(
                PrimitiveTuples.pair(0, -1), CursorAction.UP,
                PrimitiveTuples.pair(0, 1), CursorAction.DOWN,
                PrimitiveTuples.pair(-1, 0), CursorAction.LEFT,
                PrimitiveTuples.pair(-1, 0), CursorAction.RIGHT
        );

        mouseMappings = UnifiedMap.newWithKeysValues(
                PrimitiveTuples.pair(GLFW.GLFW_MOUSE_BUTTON_1, GLFW.GLFW_PRESS), MouseAction.FIRE,
                PrimitiveTuples.pair(GLFW.GLFW_MOUSE_BUTTON_2, GLFW.GLFW_PRESS), MouseAction.ZOOM_IN,
                PrimitiveTuples.pair(GLFW.GLFW_MOUSE_BUTTON_3, GLFW.GLFW_PRESS), MouseAction.SCROLL_CLICK
        );
    }

    public void replaceMouseMapping(MouseAction action, int glfwMouseButton, int glfwAction) {
        // Expensive but only done on a remap
        var mappingsByAction = mouseMappings.flipUniqueValues();

        if (mappingsByAction.containsKey(action)) {
            var keys = mappingsByAction.get(action);
            mouseMappings.remove(keys);
            mouseMappings.put(PrimitiveTuples.pair(glfwMouseButton, glfwAction), action);
        }
    }

    public MouseAction getActionForMouseButton(int glfwMouseButton, int glfwAction) {
        return mouseMappings.get(PrimitiveTuples.pair(glfwMouseButton, glfwAction));
    }

    public void invertCursorVertical() {
        var mappingsByAction = cursorMappings.flipUniqueValues();

        var currentUpMapping = mappingsByAction.get(CursorAction.UP);
        if (currentUpMapping != null) {
            var newMapping = PrimitiveTuples.pair(currentUpMapping.getOne() * -1,
                    currentUpMapping.getTwo() * -1);

            cursorMappings.remove(currentUpMapping);
            cursorMappings.put(newMapping, CursorAction.UP);
        }

        var currentDownMapping = mappingsByAction.get(CursorAction.DOWN);
        if (currentDownMapping!= null) {
            var newMapping = PrimitiveTuples.pair(currentDownMapping.getOne() * -1,
                    currentDownMapping.getTwo() * -1);

            cursorMappings.remove(currentDownMapping);
            cursorMappings.put(newMapping, CursorAction.DOWN);
        }
    }

    public void invertCursorHorizontal() {
        var mappingsByAction = cursorMappings.flipUniqueValues();

        var currentLeftMapping = mappingsByAction.get(CursorAction.LEFT);
        if (currentLeftMapping != null) {
            var newMapping = PrimitiveTuples.pair(currentLeftMapping.getOne() * -1,
                    currentLeftMapping.getTwo() * -1);

            cursorMappings.remove(currentLeftMapping);
            cursorMappings.put(newMapping, CursorAction.LEFT);
        }

        var currentRightMapping = mappingsByAction.get(CursorAction.RIGHT);
        if (currentRightMapping!= null) {
            var newMapping = PrimitiveTuples.pair(currentRightMapping.getOne() * -1,
                    currentRightMapping.getTwo() * -1);

            cursorMappings.remove(currentRightMapping);
            cursorMappings.put(newMapping, CursorAction.RIGHT);
        }
    }
}
