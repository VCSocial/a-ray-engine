package dev.vcsocial.arayengine.util;

import dev.vcsocial.arayengine.common.GlColor;
import org.lwjgl.opengl.GL11;

public final class GlOperationsUtil {

    private GlOperationsUtil() {
    }

    public static void glClearColor(GlColor glColor) {
        GL11.glClearColor(glColor.getRed(), glColor.getGreen(), glColor.getBlue(), glColor.getAlpha());
    }
}
