package dev.vcsocial.arayengine.system.rendering;

import dev.vcsocial.arayengine.core.util.IoUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL33;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;

public class Shader {

    private static final Logger LOGGER = LogManager.getLogger(Shader.class);

    private final int id;
    private final int vertexId;
    private final int fragmentId;

    public Shader(String vertexFilename, String textureFilename) {
        vertexId = IoUtils.loadShader(vertexFilename, GL33.GL_VERTEX_SHADER);
        LOGGER.debug("Vertex shader [vertexId={}] was created", vertexId);

        fragmentId = IoUtils.loadShader(textureFilename, GL33.GL_FRAGMENT_SHADER);
        LOGGER.debug("Fragment shader [fragmentId={}] was created", fragmentId);

        id = GL33.glCreateProgram();
        LOGGER.debug("Shader Program created with [id={}]", id);

        GL33.glAttachShader(id, vertexId);
        GL33.glAttachShader(id, fragmentId);
        GL33.glLinkProgram(id);

        if (GL33.glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
            LOGGER.debug("Unable to link shaders [vertexId={}] [fragment={}] to shader program [id={}], info log: {}",
                    vertexId, fragmentId, id, GL33.glGetProgramInfoLog(id, 512));
            System.exit(-1);
        }
        LOGGER.debug("Successfully linked shaders [vertexId={}] [fragment={}] to shader program [id={}]", vertexId,
                fragmentId, id);

        GL33.glDeleteShader(vertexId);
        GL33.glDeleteShader(fragmentId);
        LOGGER.debug("Deleted shaders [vertexId={}] [fragment={}] since linked in shader program", vertexId,
                fragmentId);
    }

    public void use() {
        GL33.glUseProgram(id);
    }

    public void setBooleanUniform(String name, boolean value) {
        GL33.glUniform1i(GL33.glGetUniformLocation(id, name), value ? 1 : 0);
    }

    public void setIntUniform(String name, int value) {
        GL33.glUniform1i(GL33.glGetUniformLocation(id, name), value);
    }

    public void setFloatUniform(String name, float value) {
        GL33.glUniform1f(GL33.glGetUniformLocation(id, name), value);
    }
}
