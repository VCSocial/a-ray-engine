package dev.vcsocial.lazerwizard.component;

import dev.vcsocial.lazerwizard.system.rendering.Vertex;

public class LineMesh {
    public Vertex vertexBottom;
    public Vertex vertexTop;
    public GlColor color;

    public LineMesh(float x, float bottomY, float topY, GlColor color) {
        vertexBottom = new Vertex(x, bottomY, 0);
        vertexTop = new Vertex(x, topY, 0);
        this.color = color;
    }

}
