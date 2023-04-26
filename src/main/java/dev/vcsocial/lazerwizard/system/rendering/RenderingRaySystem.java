package dev.vcsocial.lazerwizard.system.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import dev.vcsocial.lazerwizard.component.LineMeshGroup;
import dev.vcsocial.lazerwizard.core.manager.window.WindowManager;
import dev.vcsocial.lazerwizard.system.EntitySystemOrListener;
import jakarta.inject.Singleton;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.impl.factory.primitive.FloatLists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.util.Map;

import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL33.*;


@Singleton
public class RenderingRaySystem extends IteratingSystem implements EntitySystemOrListener, AutoCloseable {
    private static final int FLOAT_SIZE = 4;
    private static final Map<String, Texture> lazyTextures = Maps.mutable.empty();

    /**
     * Some ECS-ify thoughts have a system that calculates vertices (the raycasting system)
     * Pass it to the render system and use that for mesh generation
     */

    private final WindowManager windowManager;
    private final MutableIntList vertexArrayObjectList;
    private final MutableIntList vertexBufferObjectList;

    float firstVertices[] = null;

    private Shader skyShader = null;
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
        GL33.glDrawArrays(GL_TRIANGLES, 0, vertices.length);
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

    private void renderSky() {
//        float[] vertices = {
//                -1, -1, 0, 0, 0,
//                1, 1, 0, 1, 1,
//                -1, 1, 0, 0, 1,
//                -1, -1, 0, 0, 0,
//                1, -1, 0, 1, 0,
//                1, 1, 0, 1, 1
//        };

        float[] vertices = {
                1,  1, 0, 1, 0,  // top right
                1, -1, 0, 1, 1, // bottom right
                -1, -1, 0, 0, 1, // bottom left
                -1,  1, 0,  0, 0 // top left
        };
        int[] indices = {  // note that we start from 0!
                0, 1, 3,   // first triangle
                1, 2, 3    // second triangle
        };

        // Shader loadings and compilation
        if (skyShader == null) {
            skyShader = new Shader("sky/Sky.vs", "sky/Sky.fs");
        }

        int vertexArrayObject = glGenVertexArrays();
        int vertexBufferObject = glGenBuffers();
        int elementBufferObject = glGenBuffers();

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

        int stride = FLOAT_SIZE * 5;
        GL33.glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        GL33.glEnableVertexAttribArray(0);

        GL33.glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * FLOAT_SIZE);
        GL33.glEnableVertexAttribArray(1);

        GL33.glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6 * FLOAT_SIZE);
        GL33.glEnableVertexAttribArray(2);

        GL33.glBindBuffer(GL_ARRAY_BUFFER, 0);
        GL33.glBindVertexArray(0);

        var skyPath = "sky/guillermo-ferla-kEEl9csCutg-unsplash.jpg";
        if (lazyTextures.get(skyPath) == null) {
            lazyTextures.put(skyPath, new Texture(skyPath, GL_TEXTURE0));
        }
        skyShader.use();
        skyShader.setIntUniform("texture0", 0);

        lazyTextures.get(skyPath).activate();
        GL33.glBindVertexArray(vertexArrayObject);
        GL33.glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var lineMeshGroup = LineMeshGroup.COMPONENT_MAPPER.get(entity);
        var list = FloatLists.mutable.empty();

        for (int i = 0; i < lineMeshGroup.lineMeshList.size(); i++) {
//        for (int i = 0; i < lineMeshGroup.lineMeshList.size(); i += 2) {
            if (i + 1 < lineMeshGroup.lineMeshList.size()) {
                var a = lineMeshGroup.lineMeshList.get(i);
                var b = lineMeshGroup.lineMeshList.get(i + 1);

                // First triangle
                // first point
                list.add(a.vertexBottom.x());
                list.add(a.vertexBottom.y());
                list.add(a.vertexBottom.z());

                list.add(a.color.r);
                list.add(a.color.g);
                list.add(a.color.b);
                list.add(a.color.alpha);

                // second point
                list.add(b.vertexTop.x());
                list.add(b.vertexTop.y());
                list.add(b.vertexTop.z());

                list.add(a.color.r);
                list.add(a.color.g);
                list.add(a.color.b);
                list.add(a.color.alpha);
//                list.add(b.color.r);
//                list.add(b.color.g);
//                list.add(b.color.b);
//                list.add(b.color.alpha);

                // third point
                list.add(a.vertexTop.x());
                list.add(a.vertexTop.y());
                list.add(a.vertexTop.z());

                list.add(a.color.r);
                list.add(a.color.g);
                list.add(a.color.b);
                list.add(a.color.alpha);

                // Second triangle
                // first point
                list.add(a.vertexBottom.x());
                list.add(a.vertexBottom.y());
                list.add(a.vertexBottom.z());

                list.add(a.color.r);
                list.add(a.color.g);
                list.add(a.color.b);
                list.add(a.color.alpha);

                // second point
                list.add(b.vertexBottom.x());
                list.add(b.vertexBottom.y());
                list.add(b.vertexBottom.z());

                list.add(a.color.r);
                list.add(a.color.g);
                list.add(a.color.b);
                list.add(a.color.alpha);
//                list.add(b.color.r);
//                list.add(b.color.g);
//                list.add(b.color.b);
//                list.add(b.color.alpha);

                // third point
                list.add(b.vertexTop.x());
                list.add(b.vertexTop.y());
                list.add(b.vertexTop.z());

                list.add(a.color.r);
                list.add(a.color.g);
                list.add(a.color.b);
                list.add(a.color.alpha);
//                list.add(b.color.r);
//                list.add(b.color.g);
//                list.add(b.color.b);
//                list.add(b.color.alpha);
            }
        }
        renderSky();
        render(list.toArray());
    }
}