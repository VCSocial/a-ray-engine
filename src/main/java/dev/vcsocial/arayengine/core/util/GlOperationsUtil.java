package dev.vcsocial.arayengine.core.util;

import dev.vcsocial.arayengine.common.GlColor;
import org.lwjgl.opengl.GL33;

/**
 * Utilities providing a bridge between custom objects and OpenGl
 *
 * @since 0.1.0
 * @author vcsocial
 */
public final class GlOperationsUtil {

    private GlOperationsUtil() {
    }

    /**
     * Calls glClearColor with the supplied color
     *
     * @param glColor color fed to glClearColor
     */
    public static void glClearColor(GlColor glColor) {
        GL33.glClearColor(glColor.getRed(), glColor.getGreen(), glColor.getBlue(), glColor.getAlpha());
    }
}
