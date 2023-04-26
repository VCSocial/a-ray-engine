package dev.vcsocial.arayengine.world;

public class Tile {
    private static final int tileSize = 64;
    private final TileType tileType;

    private Tile(TileType tileType) {
        this.tileType = tileType;
    }

    public static int getTileSize() {
        return tileSize;
    }

    public TileType getTileType() {
        return tileType;
    }

    public static Tile wall() {
        return new Tile(TileType.WALL);
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
