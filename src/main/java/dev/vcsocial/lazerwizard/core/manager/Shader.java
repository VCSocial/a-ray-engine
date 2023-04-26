package dev.vcsocial.lazerwizard.core.manager;

import dev.vcsocial.lazerwizard.core.util.IoUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;

import java.nio.FloatBuffer;

public abstract class Shader {
    private int programId;
    private int vertexId;
    private int fragmentId;

    private FloatBuffer matrix = BufferUtils.createFloatBuffer(16);

    public Shader(String vertex, String fragment) {
        vertexId = IoUtils.loadShader(vertex, GL33.GL_VERTEX_SHADER);
        fragmentId = IoUtils.loadShader(fragment, GL33.GL_FRAGMENT_SHADER);
        programId = GL33.glCreateProgram();

        GL33.glAttachShader(programId, vertexId);
        GL33.glAttachShader(programId, fragmentId);
        bindAttributes();

        GL33.glLinkProgram(programId);
        GL33.glValidateProgram(programId);
        getAllUniformLocations();
    }

    public void start() {
        GL33.glUseProgram(programId);
    }

    public void stop() {
        GL33.glUseProgram(0);
    }

    protected void bindAttribute(int attribute, String variableName) {
        GL33.glBindAttribLocation(programId, attribute, variableName);
    }

    protected int getUniformLocation(String uniformName) {
        return GL33.glGetUniformLocation(programId, uniformName);
    }

    protected void loadFloat(int location, float value) {
        GL33.glUniform1f(location, value);
    }

    protected void loadVector(int location, Vector3f vector) {
        GL33.glUniform3f(location, vector.x, vector.y, vector.z);
    }

    protected void loadBoolean(int location, boolean bool) {
        float toVector = bool ? 1: 0;
        GL33.glUniform1f(location, toVector);
    }

    protected void loadMatrix(int location, Matrix4f matrix4f) {
        matrix4f.set(matrix);
        matrix.flip();
        GL33.glUniformMatrix4fv(location, false, matrix);
    }

    protected abstract void bindAttributes();

    protected abstract void getAllUniformLocations();
}
