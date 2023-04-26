package dev.vcsocial.arayengine.common;

import dev.vcsocial.arayengine.util.IoUtils;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Texture {
    private final int id, width, height, comp;


    public Texture(String imagePath) {
        try {

            ByteBuffer imageData = IoUtils.ioResourceToByteBuffer(imagePath, 8 * 1024);

            try (MemoryStack stack = stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer components = stack.mallocInt(1);

                // Decode texture image into a byte buffer
                ByteBuffer decodedImage = stbi_load_from_memory(imageData, w, h, components, 4);

                this.width = w.get(0);
                this.height = h.get(0);
                this.comp = components.get(0);

                // Create a new OpenGL texture
                this.id = glGenTextures();

                // Bind the texture
                glBindTexture(GL_TEXTURE_2D, this.id);

                // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
                glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

                // Upload the texture data
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, decodedImage);

                // Generate Mip Map
                glGenerateMipmap(GL_TEXTURE_2D);
            }
        } catch (IOException e) {
           throw new IllegalStateException("Woops!");
        }
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
