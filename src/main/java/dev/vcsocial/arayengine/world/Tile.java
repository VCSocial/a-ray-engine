package dev.vcsocial.arayengine.world;

import dev.vcsocial.arayengine.common.GlColor;

public class Tile {
    private static final int tileSize = 64;
    private final TileType tileType;
    private final GlColor tileColor;

    private Tile(TileType tileType) {
        this(tileType, new GlColor(255, 0 ,0));
    }

    private Tile(TileType tileType, GlColor color) {
        this.tileType = tileType;
        this.tileColor = color;
    }

    public static int getTileSize() {
        return tileSize;
    }

    public TileType getTileType() {
        return tileType;
    }

    public GlColor getTileColor() {
        return tileColor;
    }

    public static Tile wall() {
        return new Tile(TileType.WALL);
    }

    public static Tile wallColored(GlColor c) {
        return new Tile(TileType.WALL, c);
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
