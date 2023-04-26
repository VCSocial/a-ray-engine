package dev.vcsocial.arayengine.core.util;

import dev.vcsocial.arayengine.common.TextureManager;
import dev.vcsocial.arayengine.system.RenderingSystem;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.memSlice;

public final class IoUtils {

    private static Map<String, Integer> textureCache = UnifiedMap.newMap();

    private IoUtils() {
    }

    public static int loadTexture(String texture) {
        String resourceName = "PNG/Dark/" + texture;

        if (textureCache.containsKey(texture)) {
            return textureCache.get(resourceName);
        }

        try (var stack = MemoryStack.stackPush()) {
            var url = RenderingSystem.class.getClassLoader().getResource(resourceName);
            var file = new File(url.toURI());
            var filePath = file.getAbsolutePath();

            // Generate texture handle
            int textureId = GL33.glGenTextures();
            textureCache.put(resourceName, textureId);
            GL33.glBindTexture(GL_TEXTURE_2D, textureId);

            // Filtering options
            GL33.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            GL33.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            GL33.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            GL33.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            ByteBuffer data = STBImage.stbi_load(filePath, w, h, channels, 0);
            if (data == null) {
                throw new IOException("Can't load" + resourceName);
            }

            GL33.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w.get(), h.get(), 0, GL_RGB, GL_UNSIGNED_BYTE, data);
            GL33.glGenerateMipmap(GL_TEXTURE_2D);
            STBImage.stbi_image_free(data);

            return textureId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int loadShader(String file, int type) {
        var shaderSource = new StringBuilder();
        URI uri;
        try {
            uri = RenderingSystem.class.getClassLoader().getResource("shaders/" + file).toURI();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to construct shader's path");
        }
        var fileFile = new File(uri);

        try (var bufferedReader = new BufferedReader(new FileReader(fileFile))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("double frick :/");
        }

        int id = GL33.glCreateShader(type);
        GL33.glShaderSource(id, shaderSource);
        GL33.glCompileShader(id);

        if (GL33.glGetShaderi(id, GL33.GL_COMPILE_STATUS) == GL_FALSE) {
            System.out.println(GL33.glGetShaderInfoLog(id, 512));
            System.exit(-1);
        }
        return id;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param resource   the resource to read
     * @param bufferSize the initial buffer size
     * @return the resource data
     * @throws IOException if an IO error occurs
     */
    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = createByteBuffer((int) fc.size() + 1);
                while (fc.read(buffer) != -1) {
                    ;
                }
            }
        } else {
            try (
                    InputStream source = TextureManager.class.getClassLoader().getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                buffer = createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                    }
                }
            }
        }

        buffer.flip();
        return memSlice(buffer);
    }
}
