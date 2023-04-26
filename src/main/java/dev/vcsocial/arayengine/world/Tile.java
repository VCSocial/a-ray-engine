package dev.vcsocial.arayengine.world;

import dev.vcsocial.arayengine.common.GlColorCompatible;

import java.awt.*;

public class Tile {
    private static final int tileSize = 64;
    private final TileType tileType;
    private final GlColorCompatible tileColor;

    private Tile(TileType tileType) {
        this(tileType, new GlColorCompatible(255, 0 ,0));
    }

    private Tile(TileType tileType, GlColorCompatible color) {
        this.tileType = tileType;
        this.tileColor = color;
    }

    public static int getTileSize() {
        return tileSize;
    }

    public TileType getTileType() {
        return tileType;
    }

    public GlColorCompatible getTileColor() {
        return tileColor;
    }

    public static Tile wall() {
        return new Tile(TileType.WALL);
    }

    public static Tile wallColored(Color c) {
        return new Tile(TileType.WALL, new GlColorCompatible(c.getRed(), c.getGreen(), c.getBlue()));
    }

    public static Tile floor() {
        return new Tile(TileType.FLOOR);
    }

    public static Tile ceiling() {
        return new Tile(TileType.CEILING);
    }

    public static Tile floorAndCeiling() {
        return new Tile(TileType.FLOOR_AND_CEILING);
    }
}
