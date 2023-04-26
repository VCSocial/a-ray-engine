package dev.vcsocial.lazerwizard.system.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import dev.vcsocial.lazerwizard.component.LineMeshGroup;
import dev.vcsocial.lazerwizard.core.manager.window.WindowManager;
import dev.vcsocial.lazerwizard.system.EntitySystemOrListener;
import jakarta.inject.Singleton;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL33.*;


@Singleton
public class RenderingRaySystem extends IteratingSystem implements EntitySystemOrListener, AutoCloseable {
    private static final int FLOAT_SIZE = 4;

    /**
     * Some ECS-ify thoughts have a system that calculates vertices (the raycasting system)
     * Pass it to the render system and use that for mesh generation
     */

    private final WindowManager windowManager;
    private final MutableIntList vertexArrayObjectList;
    private final MutableIntList vertexBufferObjectList;

    float firstVertices[] = null;

    private Shader shader = null;

    public RenderingRaySystem(WindowManager windowManager) {
        super(Family.all(LineMeshGroup.class).get(), 3);

        this.windowManager = windowManager;
        vertexArrayObjectList = IntLists.mutable.empty();
        vertexBufferObjectList = IntLists.mutable.empty();
    }

    private void render(float[] vertices) {
        if (firstVertices == null) {
            firstVertices = vertices;
        }

        // Shader loadings and compilation
        if (shader == null) {
            shader = new Shader("Textured.vs", "Textured.fs");
        }

        int vertexArrayObject = glGenVertexArrays();
        int vertexBufferObject = glGenBuffers();

        vertexArrayObjectList.clear();
        vertexBufferObjectList.clear();

        vertexArrayObjectList.add(vertexArrayObject);
        vertexBufferObjectList.add(vertexBufferObject);

        // Setup vao
        GL33.glBindVertexArray(vertexArrayObject);

        // Setup vbo
        GL33.glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
        try (var stack = MemoryStack.stackPush()) {
            var floatBuffer = stack.mallocFloat(vertices.length);
            floatBuffer.put(vertices).flip();
            GL33.glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_DYNAMIC_DRAW);
        }

        int stride = FLOAT_SIZE * 7;
        GL33.glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        GL33.glEnableVertexAttribArray(0);

        GL33.glVertexAttribPointer(1, 4, GL_FLOAT, false, stride, 3 * FLOAT_SIZE);
        GL33.glEnableVertexAttribArray(1);

        GL33.glBindBuffer(GL_ARRAY_BUFFER, 0);
        GL33.glBindVertexArray(0);

        shader.use();
        GL33.glBindVertexArray(vertexArrayObjectList.get(0));
        GL33.glDrawArrays(GL_LINES, 0, vertices.length);
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

        glFlush();
        GLFW.glfwSwapBuffers(windowManager.getWindow());
        GLFW.glfwPollEvents();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var lineMeshGroup = LineMeshGroup.COMPONENT_MAPPER.get(entity);

        var list = new ArrayList<Float>();

        lineMeshGroup.lineMeshList.forEach(mesh -> {
            list.add(mesh.vertexBottom.x());
            list.add(mesh.vertexBottom.y());
            list.add(mesh.vertexBottom.z());

            list.add(mesh.color.r);
            list.add(mesh.color.g);
            list.add(mesh.color.b);
            list.add(mesh.color.alpha);

            list.add(mesh.vertexTop.x());
            list.add(mesh.vertexTop.y());
            list.add(mesh.vertexTop.z());

            list.add(mesh.color.r);
            list.add(mesh.color.g);
            list.add(mesh.color.b);
            list.add(mesh.color.alpha);
        });

        float[] vertices = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            vertices[i] = list.get(i);
        }

        render(vertices);
    }
}