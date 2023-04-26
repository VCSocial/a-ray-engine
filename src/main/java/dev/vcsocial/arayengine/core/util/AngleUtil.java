package dev.vcsocial.arayengine.core.util;

public final class AngleUtil {

    private AngleUtil() {
    }

    public static float addLimitToCirlce(float originalAngle, float angleIncrement)  {
        originalAngle += angleIncrement;

        if (originalAngle > 2 * Math.PI) {
            originalAngle -= 2 * Math.PI;
        } else if (originalAngle < 0) {
            originalAngle += 2 * Math.PI;
        }
        return originalAngle;
    }
}
