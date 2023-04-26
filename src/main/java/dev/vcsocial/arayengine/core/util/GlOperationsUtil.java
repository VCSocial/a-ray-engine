package dev.vcsocial.arayengine.core.util;

import dev.vcsocial.arayengine.common.GlColor;
import org.lwjgl.opengl.GL33;

public final class GlOperationsUtil {

    private GlOperationsUtil() {
    }

    public static void glClearColor(GlColor glColor) {
        GL33.glClearColor(glColor.getRed(), glColor.getGreen(), glColor.getBlue(), glColor.getAlpha());
    }
}
