package dev.vcsocial.arayengine;

import dev.vcsocial.arayengine.core.manager.EngineLifeCycleManager;
import io.avaje.inject.BeanScope;

/**
 * Engine entry point used to start up DI services
 *
 * @since 0.1.0
 * @author vcsocial
 */
public class EntryPoint {

    public static void main(String[] args) {
        BeanScope.newBuilder().build()
                .get(EngineLifeCycleManager.class)
                .executeGameLoop();
    }
}