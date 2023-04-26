package dev.vcsocial.arayengine.world;

import dev.vcsocial.arayengine.common.Renderable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.FixedSizeList;
import org.eclipse.collections.api.list.MutableList;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2i;

public class LevelMap implements Renderable {
    public static int globalWidth = 0;
    public static int globalHeight = 0;
    public static LevelMap myself;

    private static final FixedSizeList<Tile> DEFAULT_TILE_MAP = Lists.fixedSize.of(
            Tile.wall(), Tile.wall(), Tile.wall(), Tile.wall(), Tile.wall(), Tile.wall(),
            Tile.wall(), Tile.floor(), Tile.wall(), Tile.floor(), Tile.floor(), Tile.wall(),
            Tile.wall(), Tile.floor(), Tile.wall(), Tile.floor(), Tile.floor(), Tile.wall(),
            Tile.wall(), Tile.floor(), Tile.wall(), Tile.wall(), Tile.floor(), Tile.wall(),
            Tile.wall(), Tile.floor(), Tile.floor(), Tile.floor(), Tile.floor(), Tile.wall(),
            Tile.wall(), Tile.wall(), Tile.wall(), Tile.wall(), Tile.wall(), Tile.wall()
    );

    private final int width;
    private final int height;
    private final FixedSizeList<Tile> tileMap;
    private boolean ranAlready = true;

    public LevelMap(int width, int height) {
        this(width, height, generateTileList(width, height));
    }

    public LevelMap(int width, int height, FixedSizeList<Tile> tileMap) {
        this.width = width;
        this.height = height;
        this.tileMap = tileMap;

        globalWidth = width;
        globalHeight = height;
        myself = this;
    }


    public static LevelMap getDefaultLevelMap() {
        return new LevelMap(6,6, DEFAULT_TILE_MAP);
    }

    // Buggy
    private static FixedSizeList<Tile> generateTileList(int width, int height) {
        MutableList<Tile> mapper = Lists.mutable.empty();
        for (int x = 0; x < (width * height); x++) {
            System.out.println("Indexing [x=" + x + "]");
            if (x < width // Close top
                    || x % height == 0 // Close left
                    || (((x / width) + 1) * width ) % x == 1 // Closes Right causes exception on 0 but not issue due to short-cicuiting
                    || x >= (width * height) - width) { // Close bottom
                mapper.add(Tile.wall());
            } else {
                mapper.add(Tile.floor());
            }
        }
        return Lists.fixedSize.ofAll(mapper);
    }

    public Tile getTile(int i) {
        return tileMap.get(i);
    }

    public Tile getTile(int x, int y) {
        return tileMap.get(width * y + x);
    }

    public void render() {
        // width * x + y
        // solve for y =

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!ranAlready) {
                    System.out.println("Indexing: [x=" + x + "], [y=" + y + "]");
                }

                if (TileType.WALL.equals(getTile(x, y).getTileType())) {
                    glColor3f(1,1,1);
                    if (!ranAlready) {
                        System.out.println("Detected WALL at: [x=" + x + "], [y=" + y + "]");
                    }
                } else {
                    glColor3f(0,0,0);
                    if (!ranAlready) {
                        System.out.println("Detected FLOOR at: [x=" + x + "], [y=" + y + "]");
                    }
                }

                int xo = x * Tile.getTileSize();
                int yo = y * Tile.getTileSize();
                glBegin(GL_QUADS);
                glVertex2i(xo + 1, yo + 1);
                glVertex2i(xo + 1, yo + Tile.getTileSize() - 1);
                glVertex2i(xo + Tile.getTileSize() - 1, yo + Tile.getTileSize() - 1);
                glVertex2i(xo - 1 + Tile.getTileSize(), yo + 1);
                glEnd();
            }
        }
        ranAlready = true;
//
//        for (int i = 0; i < tileMap.size(); i++) {
//            if (TileType.WALL.equals(tileMap.get(i).getTileType())) {
//                glColor3f(1,1,1);
//            } else {
//                glColor3f(0,0,0);
//            }
//
//            int xo =
//
//
//
//        }
    }

    public static LevelMap getFixedClosedMap(int width, int height) {
        return new LevelMap(width, height);
    }

//        var map = new Tile[width][height];
//
//        for (int i = 0; i < width ; i++) {
//            for (int j = 0; j < height; j++) {
//                map[i][j] = (i == 0 || i == width - 1 || j == 0 || j == height - 1)
//                        ? Tile.floor()
//                        : Tile.ceiling();
//
//            }
//        }

    // 4x3

    // 2D
    //(0 1 2 3)
    // 1 1 1 1
    //(4 5 6 7)
    // 1 0 0 0
    //(8 9 10 11)
    // 1 0 0  0

    // address 1,1
    // x,y (5)
    // width - x + height - y
    // (4 - 1) + (3 - 1) >>> 3 + 2 = 5
    // address 0,2
    // (4 - 0) + (3 - 2) >>> 4 + 1;
    // NOPE

    // (width * x) + y
    // (4 * 1) + 1 == 5
    // (4 * 0) + 2 == 2
    // 2,3
    // (4 * 2) + 3 = 8 + 3 = 11


    // x % height, x == 4, 4 % 4
    // x == 7, x % (width + height) == 0, 7 % (4 + 3) > 7 % 7 == 0, GOOD :D

    // 1D (width 4, height 3), total size: 9 (good), x < width (x < 3 GOOD), x % height (3
    // 0 1 2 3 4 5 6 7 8 9 10 11
    // 1 1 1 1 0 0 0 0 0 0 0  0

    // x >= (width * height) - width, 8 >= (4 * 3) - 3, 8 >= 12 - 3, 8 >= 8
}
