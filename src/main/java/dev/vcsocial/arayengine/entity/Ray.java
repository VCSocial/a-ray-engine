package dev.vcsocial.arayengine.entity;

import dev.vcsocial.arayengine.common.Renderable;
import dev.vcsocial.arayengine.world.LevelMap;
import org.joml.Vector2d;

public class Ray implements Renderable {

    private final Entity eminator;
    private Vector2d rayDirection;
    private final LevelMap levelMap;

    public Ray(Entity eminator, LevelMap levelMap) {
        this.eminator = eminator;
        this.levelMap = levelMap;
        this.rayDirection = new Vector2d();
    }

    public Ray(Entity eminator, float rayAngle, LevelMap levelMap) {
        this.eminator = eminator;
        this.levelMap = levelMap;
    }

    public Vector2d getRayDirection() {
        return rayDirection;
    }

    public void setRayDirection(Vector2d rayDirection) {
        this.rayDirection = rayDirection;
    }

    public void setRayDirectionX(double x) {
        rayDirection.x = x;
    }

    public void setRayDirectionY(double y) {
        rayDirection.y = y;
    }

    private static int shiftBitsSymmetricBy(float x, int shiftValue) {
        return (((int) x >> shiftValue) << shiftValue);
    }

    public void render() {
    }
}
