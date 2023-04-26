package dev.vcsocial.arayengine.system.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import dev.vcsocial.arayengine.common.GlColor;
import dev.vcsocial.arayengine.component.KeyboardInputComponent;
import dev.vcsocial.arayengine.config.keybindings.KeyAction;
import dev.vcsocial.arayengine.core.util.GlOperationsUtil;
import dev.vcsocial.arayengine.core.util.IoUtils;
import dev.vcsocial.arayengine.system.EntitySystemOrListener;
import jakarta.inject.Singleton;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.tuple.Triple;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.tuple.Tuples;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;

import static org.joml.Math.sin;
import static org.lwjgl.opengl.GL33.*;

@Singleton
public class RenderingOpenGlSystem extends IteratingSystem implements EntitySystemOrListener, AutoCloseable {
    private static final GlColor DEFAULT_BACKGROUND_COLOR = new GlColor(150, 50, 150);
    private static final GlColor UPDATED_BACKGROUND_COLOR = new GlColor(50, 150, 50);
    private static final int FLOAT_SIZE = 4;

    /**
     * Some ECS-ify thoughts have a system that calculates vertices (the raycasting system)
     * Pass it to the render system and use that for mesh generation
     */

    int texture1 = -1;
    int texture2 = -1;

    private Texture texture;

    float[] vertices = {
            // positions          // colors           // texture coords
            0.5f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f,   1.0f, 1.0f,   // top right
            0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f,   1.0f, 0.0f,   // bottom right
            -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, 1.0f,   0.0f, 0.0f,   // bottom left
            -0.5f,  0.5f, 0.0f,   1.0f, 1.0f, 0.0f,   0.0f, 1.0f    // top left
    };

    int[] indices = {
            0, 1, 3, // first triangle
            1, 2, 3  // second triangle
    };

    private Shader shader;
    private boolean init = false;
    private int programId = -1;

    private final MutableIntList vertexArrayObjectList;
    private final MutableIntList vertexBufferObjectList;
    private final MutableIntList elementBufferObjectList;

    public RenderingOpenGlSystem() {
        super(Family.all(KeyboardInputComponent.class).get(), 2);

        vertexArrayObjectList = IntLists.mutable.empty();
        vertexBufferObjectList = IntLists.mutable.empty();
        elementBufferObjectList = IntLists.mutable.empty();
    }

    public int[] generateIndiciesFromVertices(float[] vertices, int width) {
        int stride = 3;

        int numberVertices = (vertices.length / width) * stride; // TODO expect 6 for hard coded test
        Map<Triple, Integer> positions = Maps.mutable.ofInitialCapacity(numberVertices);

        // TODO we can find the triples right but not the resulting indices
        for (int i = 0; i < vertices.length; i += width) {
            float x = Float.MIN_VALUE;
            float y = Float.MIN_VALUE;
            float z = Float.MIN_VALUE;
            for (int j = i; j < i + stride; j++) {
                if (i + stride - j == 3) {
                    x = vertices[j];
                } else if (i + stride - j == 2) {
                    y = vertices[j];
                } else if (i + stride - j == 1) {
                    z = vertices[j];
                }
            }

            positions.put(Tuples.triple(x, y, z), i % width);
        }



//        var val = positions.values();
//        int[] indicesGen = new int[val.size()];
//        for (int i = 0; i < val.size(); i++) {
//            indicesGen[i] = val.
//        }

        return new int[] {};
    }

    private FloatBuffer toFloatBuffer(float[] data) {
        var floatBuffer = BufferUtils.createFloatBuffer(data.length);
        floatBuffer.put(data).flip();
        return floatBuffer;
    }

    private IntBuffer toIntBuffer(int[] data) {
        var intBuffer = BufferUtils.createIntBuffer(data.length);
        intBuffer.put(data).flip();
        return intBuffer;
    }

    private void createUniform() {
        float currentTime = (float) GLFW.glfwGetTime();
        float greenValue = (sin(currentTime) / 2.0f) + 0.5f;
        int vertexColorLocation = GL33.glGetUniformLocation(programId, "ourColor");
        glUniform4f(vertexColorLocation, 0.0f, greenValue, 0.0f, 1.0f);
    }

    private void renderInitialization() {
        // Shader loadings and compilation
        shader = new Shader("Textured.vs", "Textured.fs");

//        GL33.glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);

        int vertexArrayObject = glGenVertexArrays();
        int vertexBufferObject = glGenBuffers();
        int elementBufferObject = glGenBuffers();

        vertexArrayObjectList.add(vertexArrayObject);
        vertexBufferObjectList.add(vertexBufferObject);
        elementBufferObjectList.add(elementBufferObject);

        // Setup vao
        GL33.glBindVertexArray(vertexArrayObject);

        // Setup vbo
        GL33.glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
        try (var stack = MemoryStack.stackPush()) {
            var floatBuffer = stack.mallocFloat(vertices.length);
            floatBuffer.put(vertices).flip();
            GL33.glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        }

        GL33.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBufferObject);
        try (var stack = MemoryStack.stackPush()) {
            var intBuffer = stack.mallocInt(indices.length);
            intBuffer.put(indices).flip();
            GL33.glBufferData(GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL_STATIC_DRAW);
        }

        int stride = FLOAT_SIZE * 8;
        GL33.glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        GL33.glEnableVertexAttribArray(0);

        GL33.glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * FLOAT_SIZE);
        GL33.glEnableVertexAttribArray(1);

        GL33.glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6 * FLOAT_SIZE);
        GL33.glEnableVertexAttribArray(2);

        GL33.glBindBuffer(GL_ARRAY_BUFFER, 0);
        GL33.glBindVertexArray(0);

        texture = new Texture("Dark/texture_01.png", GL_TEXTURE0);
        texture2 = IoUtils.loadTexture("Dark/awesomeface.png", true);


        shader.use();
        shader.setIntUniform("texture1", 0);
        shader.setIntUniform("texture2", 1);
    }

    private void inRenderLoop() {
//        GL33.glUseProgram(programId);
//        createUniform();

        texture.activate();

        GL33.glActiveTexture(GL_TEXTURE1);
        GL33.glBindTexture(GL_TEXTURE_2D, texture2);

        GL33.glBindVertexArray(vertexArrayObjectList.get(0));
//        GL33.glDrawArrays(GL_TRIANGLES, 0, 3);
        GL33.glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }


    private void unbind() {
        GL33.glBindVertexArray(0);
    }

    private void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
    }

    @Override
    public void close() {
        System.out.println("Delete vertices being called");
        vertexArrayObjectList.forEach(GL33::glDeleteVertexArrays);
        vertexBufferObjectList.forEach(GL33::glDeleteBuffers);

    }

    @Override
    public void update(float deltaTime) {
        clear();
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var keyboardInput = KeyboardInputComponent.COMPONENT_MAPPER.get(entity);

        // LazerWizardLizard? TODO

        if (KeyAction.FORWARD.equals(keyboardInput.keyAction)) {
            GlOperationsUtil.glClearColor(UPDATED_BACKGROUND_COLOR);
        } else if (KeyAction.BACKWARD.equals(keyboardInput.keyAction)) {
            GlOperationsUtil.glClearColor(DEFAULT_BACKGROUND_COLOR);
        } else if (KeyAction.STRAFE_LEFT.equals(keyboardInput.keyAction)) {
//            render(load(vertexArray));
            GL33.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else if (KeyAction.STRAFE_RIGHT.equals(keyboardInput.keyAction)) {
            GL33.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }

        if (!init) {
            renderInitialization();
            System.out.println("Should only be one");
            init = true;
            generateIndiciesFromVertices(vertices, 8);
        }
        inRenderLoop();
    }
}
