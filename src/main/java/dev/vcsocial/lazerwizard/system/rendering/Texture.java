package dev.vcsocial.lazerwizard.system.rendering;

import dev.vcsocial.lazerwizard.core.util.IoUtils;
import org.lwjgl.opengl.GL33;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class Texture {
    private final int id;
    private final int glTextureIndex;

    public Texture(String texturePath, int glTextureIndex) {
        id = IoUtils.loadTexture(texturePath);
        this.glTextureIndex = glTextureIndex;
    }

    public Texture(String texturePath, int glTextureIndex, boolean hasAlpha) {
        id = IoUtils.loadTexture(texturePath, hasAlpha);
        this.glTextureIndex = glTextureIndex;
    }

    public int getId() {
        return id;
    }

    public void activate() {
        GL33.glActiveTexture(glTextureIndex);
        GL33.glBindTexture(GL_TEXTURE_2D, id);
    }
}
