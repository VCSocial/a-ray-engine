package dev.vcsocial.lazerwizard.core.manager;

public class ShaderTextured extends Shader {

    public ShaderTextured() {
        super("Textured.vs", "Textured.fs");
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
        bindAttribute(1, "textureCoords");
    }

    @Override
    protected void getAllUniformLocations() {
    }
}