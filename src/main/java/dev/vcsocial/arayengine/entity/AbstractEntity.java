package dev.vcsocial.arayengine.entity;

import com.badlogic.ashley.core.Entity;

import java.util.UUID;

public abstract class AbstractEntity extends Entity {
    private final UUID uuid;

    public AbstractEntity() {
        uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }
}
