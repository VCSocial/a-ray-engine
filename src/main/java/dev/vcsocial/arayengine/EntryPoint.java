package dev.vcsocial.arayengine;

import dev.vcsocial.arayengine.core.manager.EngineLifeCycleManager;
import io.avaje.inject.BeanScope;

public class EntryPoint {

    public static void main(String[] args) {
        BeanScope.newBuilder().build()
                .get(EngineLifeCycleManager.class)
                .executeGameLoop();
    }
}