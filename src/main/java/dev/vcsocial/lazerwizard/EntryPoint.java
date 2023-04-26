package dev.vcsocial.lazerwizard;

import dev.vcsocial.lazerwizard.core.manager.EngineLifeCycleManager;
import io.avaje.inject.BeanScope;

/**
 * Engine entry point used to start up DI services
 *
 * @since 0.1.0
 * @author vcsocial
 */
public class EntryPoint {

    public static void main(String[] args) {
        // TODO what is an appropriate size? The default is 64KiBi this sets it to 1MiBi
        System.setProperty("org.lwjgl.system.stackSize", "1024");

        BeanScope.newBuilder().build()
                .get(EngineLifeCycleManager.class)
                .executeGameLoop();
    }
}