package dev.vcsocial.arayengine.world;

import dev.vcsocial.arayengine.common.GlColor;
import dev.vcsocial.arayengine.component.TileType;

public class Tile {
    private static final GlColor DEFAULT_COLOR = GlColor.RED;
    private static final int TILE_SIZE = 64;

    private final TileType tileType;
    private final GlColor tileColor;

    private Tile(TileType tileType) {
        this(tileType, DEFAULT_COLOR);
    }

    private Tile(TileType tileType, GlColor color) {
        this.tileType = tileType;
        this.tileColor = color;
    }

    public static int getTileSize() {
        return TILE_SIZE;
    }

    public TileType getTileType() {
        return tileType;
    }

    public GlColor getTileColor() {
        return tileColor;
    }

    public static Tile wall() {
        return new Tile(TileType.WALL, GlColor.RED);
    }

    public static Tile wall(GlColor c) {
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
