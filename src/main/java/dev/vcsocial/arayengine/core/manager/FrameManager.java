package dev.vcsocial.arayengine.core.manager;

import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Singleton
public class FrameManager {

    private static final Logger LOGGER = LogManager.getLogger(FrameManager.class);
    private static final float MILLISECONDS_IN_SECOND = 1000f;

    private double previousTime = GLFW.glfwGetTime();
    private int frameCounter = 0;
    private double frameTimeMs = 0;

    // TODO there is an issue with this approach
    // http://www.opengl-tutorial.org/miscellaneous/an-fps-counter/
    // See resource for more details https://gafferongames.com/post/fix_your_timestep/
    //        time = GLFW.glfwGetTime() * MILISECONDS_IN_SECOND;
    //        return (float) ((time - previousTime) / MILISECONDS_IN_SECOND);
    public float getDeltaTime() {
        LOGGER.trace("Updating timing");
        double currentTime = GLFW.glfwGetTime();
        frameCounter++;
        double deltaTime = currentTime - previousTime;
        if (deltaTime >= 1) {
            frameTimeMs = MILLISECONDS_IN_SECOND / frameCounter;
            frameCounter = 0;
            previousTime += 1;
        }
        return (float) deltaTime;
    }

    public double getFrameTimeMs() {
        return frameTimeMs;
    }
}
