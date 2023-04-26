package dev.vcsocial.arayengine.core.util;

import org.joml.Vector2f;

import static java.lang.Math.pow;
import static org.joml.Math.sqrt;

public final class DistanceUtil {

    private DistanceUtil() {
    }

    public static float distance(float ax, float ay, float bx, float by, float angle) {
        return (float) sqrt(pow(bx - ax, 2) + pow(by - ay, 2));
    }

    public static float distance(Vector2f a, Vector2f b, float angle) {
        return (float) sqrt(pow(b.x - a.x, 2) + pow(b.y - a.y, 2));
    }

//        return (float) (cos(toRadians(angle)) * (bx - ax) - sin(toRadians(angle)) * (by - ay));
}
