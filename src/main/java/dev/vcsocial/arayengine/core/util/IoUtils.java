package dev.vcsocial.arayengine.core.util;

import dev.vcsocial.arayengine.system.RenderingSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Map;

import static org.lwjgl.opengl.GL33.*;

/**
 * Utility class for loading in textures and shaders
 *
 * @since 1.0.0
 * @author vcsocial
 */
public final class IoUtils {

    private final static Logger LOGGER = LogManager.getLogger(IoUtils.class);
    private final static String SHADER_ROOT = "shaders/";
    private final static String TEXTURE_ROOT = "textures/";
    private final static Map<String, Integer> textureCache = UnifiedMap.newMap();

    private IoUtils() {
    }

    public static int loadTexture(String texture) {
        return loadTexture(texture, GL_RGB);
    }

    public static int loadTexture(String texture, boolean hasAlpha) {
        return loadTexture(texture, hasAlpha ? GL_RGBA : GL_RGB);
    }

    public static int loadTexture(String texturePath, int internalFormat) {
        STBImage.stbi_set_flip_vertically_on_load(true);
        texturePath = TEXTURE_ROOT + texturePath;

        if (textureCache.containsKey(texturePath)) {
            LOGGER.debug("Cache contained [texturePath={}]", texturePath);
            return textureCache.get(texturePath);
        }
        LOGGER.debug("Continuing to load texture [texturePath={}]", texturePath);

        try (var stack = MemoryStack.stackPush()) {
            var url = RenderingSystem.class.getClassLoader().getResource(texturePath);
            var file = new File(url.toURI());
            var filePath = file.getAbsolutePath();
            LOGGER.debug("Resolved texture to be at [filePath={}]", filePath);

            // Generate texture handle
            int textureId = GL33.glGenTextures();
            textureCache.put(texturePath, textureId);
            LOGGER.debug("Texture generated with [textureId={}] and stored in cache with key [texturePath={}]",
                    textureId, texturePath);

            // Bind the texture
            GL33.glBindTexture(GL_TEXTURE_2D, textureId);
            LOGGER.debug("Bound [textureId={}]", textureId);

            // Filtering options
            GL33.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            GL33.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            GL33.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            GL33.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            ByteBuffer data = STBImage.stbi_load(filePath, width, height, channels, 0);
            if (data == null) {
                throw new IOException("Can't load" + texturePath);
            }

            GL33.glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            GL33.glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width.get(), height.get(), 0, internalFormat,
                    GL_UNSIGNED_BYTE, data);
            LOGGER.debug("glTexImage2D set with [internalFormat={}]", internalFormat);

            GL33.glGenerateMipmap(GL_TEXTURE_2D);
            STBImage.stbi_image_free(data);
            LOGGER.debug("STBImage was freed");

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
            uri = RenderingSystem.class.getClassLoader().getResource(SHADER_ROOT + file).toURI();
            LOGGER.debug("Shader was resolve at [uri={}]", uri);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to construct shader's path");
        }
        var fileFile = new File(uri);

        try (var bufferedReader = new BufferedReader(new FileReader(fileFile))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                shaderSource.append(line).append("\n");
                LOGGER.trace("{}", line);
            }
        } catch (IOException e) {
            LOGGER.error("Unable to read in shader source");
            System.exit(-1);
        }

        int id = GL33.glCreateShader(type);
        LOGGER.debug("Shader created with [id={}]", id);

        GL33.glShaderSource(id, shaderSource);
        GL33.glCompileShader(id);

        if (GL33.glGetShaderi(id, GL33.GL_COMPILE_STATUS) == GL_FALSE) {
            LOGGER.error("Shader unable to be compiled, [info={}]", GL33.glGetShaderInfoLog(id, 512));
            System.exit(-1);
        }

        LOGGER.debug("Successfully loaded and compiled shader with [id={}]", id);
        return id;
    }
}
