package dev.vcsocial.lazerwizard.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import dev.vcsocial.lazerwizard.common.GlColor;
import dev.vcsocial.lazerwizard.component.KeyboardInputComponent;
import dev.vcsocial.lazerwizard.config.keybindings.KeyAction;
import dev.vcsocial.lazerwizard.core.manager.ShaderTextured;
import dev.vcsocial.lazerwizard.core.util.GlOperationsUtil;
import jakarta.inject.Singleton;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static dev.vcsocial.lazerwizard.core.util.IoUtils.loadTexture;
import static org.lwjgl.opengl.GL33.*;

@Singleton
public class RenderingSystem extends IteratingSystem implements EntitySystemOrListener, AutoCloseable {
    private static final GlColor DEFAULT_BACKGROUND_COLOR = new GlColor(150, 50, 150);
    private static final GlColor UPDATED_BACKGROUND_COLOR = new GlColor(50, 150, 50);

    // TODO possible component?
    static class GlModel {
        public int id;
        public int vertexCount;
        public int texture;

        public GlModel(int id, int vertexCount) {
            this.id = id;
            this.vertexCount = vertexCount;
        }

        public void setTexture(String texture) {
            this.texture = loadTexture(texture);
        }
    }

    float[] vertices = {
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0f, 0.5f, 0f
    };

    int[] indices = {0, 1, 2};


    private final MutableIntList vertexArrayObjectList;
    private final MutableIntList vertexBufferObjectList;

    public RenderingSystem() {
        super(Family.all(KeyboardInputComponent.class).get(), 2);

        vertexArrayObjectList = IntLists.mutable.empty();
        vertexBufferObjectList = IntLists.mutable.empty();
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

    private int createVertexArrayObject() {
        int id = GL33.glGenVertexArrays();
        vertexArrayObjectList.add(id);
        GL33.glBindVertexArray(id);

        return id;
    }

    // TODO
    private void storeData(int attribute, int dimensions, float[] data) {
        int vertexBufferObject = GL33.glGenBuffers();
        vertexBufferObjectList.add(vertexBufferObject);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vertexBufferObject);
        var floatBuffer = toFloatBuffer(data);

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, floatBuffer, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(attribute, dimensions, GL33.GL_FLOAT, false, 0, 0);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
    }

    private void bindIndices(int[] data) {
        int vertexBufferObject = GL33.glGenBuffers();
        vertexBufferObjectList.add(vertexBufferObject);

        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, vertexBufferObject);
        var intBuffer = toIntBuffer(data);

        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL33.GL_STATIC_DRAW);
    }

    private int genVao() {
        int vao = GL33.glGenVertexArrays();
        vertexArrayObjectList.add(vao);
        GL33.glBindVertexArray(vao);
        return vao;
    }


    private GlModel createMesh(float[] positions, float[] uvs, int[] indices) {
        int vao = genVao();
        storeData(0, 3, positions);
        storeData(1, 2, uvs);
        bindIndices(indices);
        GL33.glBindVertexArray(0);

        return new GlModel(vao, indices.length);
    }

    private GlModel createMesh(float[] positions, int[] indices) {
        int vao = genVao();
        storeData(0, 3, positions);
        bindIndices(indices);
        GL33.glBindVertexArray(0);

        return new GlModel(vao, indices.length);
    }

    private GlModel load(float[] vertices) {
        int id = createVertexArrayObject();
        storeData(0, 2, vertices);
        unbind();

        return new GlModel(id, vertices.length / 3);
    }

    private void unbind() {
        GL33.glBindVertexArray(0);
    }

    private void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
    }

    private void renderMesh(GlModel mesh) {
        mesh.setTexture("textures/Dark/texture_01.png");
        var shader = new ShaderTextured();

        shader.start();
        GL33.glBindVertexArray(mesh.id);
        GL33.glEnableVertexAttribArray(0);
        GL33.glEnableVertexAttribArray(1);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, mesh.texture);
        GL33.glDrawElements(GL33.GL_TRIANGLES, mesh.vertexCount, GL33.GL_UNSIGNED_INT, 0);
        GL33.glDisableVertexAttribArray(0);
        GL33.glDisableVertexAttribArray(1);
        GL33.glBindVertexArray(0);
        shader.stop();
    }

    @Override
    public void close() {
        System.out.println("Delete vertices being called");
        vertexArrayObjectList.forEach(GL33::glDeleteVertexArrays);
        vertexArrayObjectList.forEach(GL33::glDeleteBuffers);
    }

    @Override
    public void update(float deltaTime) {
        // NOOP Do nothing TODO
//        super.update(deltaTime);
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
        }
        var mesh = createMesh(vertices, indices);
        renderMesh(mesh);
    }
}
