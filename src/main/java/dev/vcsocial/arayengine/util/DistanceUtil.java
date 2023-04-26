package dev.vcsocial.arayengine.util;

import static java.lang.Math.pow;
import static org.joml.Math.sqrt;

public final class DistanceUtil {

    private DistanceUtil() {
    }

    public static float distance(float ax, float ay, float bx, float by, float angle) {
        return (float) sqrt(pow(bx - ax, 2) + pow(by - ay, 2));
//        return (float) (cos(toRadians(angle)) * (bx - ax) - sin(toRadians(angle)) * (by - ay));
    }
}
