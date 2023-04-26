package dev.vcsocial.arayengine.entity;

import dev.vcsocial.arayengine.component.GlColor;
import dev.vcsocial.arayengine.component.TileType;

import javax.swing.text.Position;


public class Tile {
    public final TileType tileType;
    public final GlColor glColor;
    private final Position position;

    public Tile(TileType tileType, GlColor glColor, Position position) {
        this.tileType = tileType;
        this.glColor = glColor;
        this.position = position;
    }

    public TileType getTileType() {
        return tileType;
    }

    public GlColor getGlColor() {
        return glColor;
    }

    public Position getPosition() {
        return position;
    }
}
