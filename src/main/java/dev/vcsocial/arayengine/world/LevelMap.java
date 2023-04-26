package dev.vcsocial.arayengine.world;

import dev.vcsocial.arayengine.common.GlColor;
import dev.vcsocial.arayengine.common.Renderable;
import dev.vcsocial.arayengine.component.TileType;
import dev.vcsocial.arayengine.manager.window.WindowLifecycleManager;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.joml.Vector2d;
import org.joml.Vector2i;

import static org.lwjgl.opengl.GL33.*;

public class LevelMap implements Renderable {

    private static final ImmutableList<Tile> DEFAULT_TILE_MAP = Lists.immutable.of(
            Tile.wall(GlColor.PURPLE), Tile.wall(GlColor.PURPLE), Tile.wall(GlColor.PURPLE), Tile.wall(GlColor.PURPLE), Tile.wall(GlColor.GREEN), Tile.wall(GlColor.GREEN),
            Tile.wall(GlColor.YELLOW), Tile.floor(), Tile.floor(), Tile.wall(GlColor.BLUE), Tile.floor(), Tile.wall(GlColor.GREEN),
            Tile.wall(GlColor.YELLOW), Tile.floor(), Tile.floor(), Tile.floor(), Tile.floor(), Tile.wall(GlColor.GREEN),
            Tile.wall(GlColor.YELLOW), Tile.floor(), Tile.floor(), Tile.floor(), Tile.floor(), Tile.wall(GlColor.GREEN),
            Tile.wall(GlColor.YELLOW), Tile.floor(), Tile.floor(), Tile.floor(), Tile.floor(), Tile.wall(),
            Tile.wall(GlColor.YELLOW), Tile.wall(GlColor.YELLOW), Tile.wall(), Tile.wall(), Tile.wall(), Tile.wall()
    );

    private final int width;
    private final int height;
    private final ImmutableList<Tile> tileMap;
    private final MutableMap<Tile, MutableList<Vector2i>> tileMutableListMutableMap;
    private boolean toggleRenderEnabled = false;

    public LevelMap(int width, int height) {
        this(width, height, generateTileList(width, height));
    }

    public LevelMap(int width, int height, ImmutableList<Tile> tileMap) {
        this.width = width;
        this.height = height;
        this.tileMap = tileMap;
        this.tileMutableListMutableMap = convertToTypeMap();
    }

    private MutableMap<Tile, MutableList<Vector2i>> convertToTypeMap() {
        MutableMap<Tile, MutableList<Vector2i>> tileTypePosition = Maps.mutable.empty();

        for (int x = 0; x < width ; x++) {
            for (int y = 0; y < height; y++) {
                var tile = getTile(x, y);

                if (tileTypePosition.contains(tile)) {
                    var list = tileTypePosition.get(tile);
                    list.add(new Vector2i(x, y));
                } else {
                    tileTypePosition.put(tile, Lists.mutable.of(new Vector2i(x, y)));
                }
            }
        }
        return tileTypePosition;
    }

    // TODO Buggy
    private static ImmutableList<Tile> generateTileList(int width, int height) {
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
        return Lists.immutable.ofAll(mapper);
    }

    public static LevelMap getDefaultLevelMap() {
        return new LevelMap(6,6, DEFAULT_TILE_MAP);
    }



    public void toggleRendering() {
        toggleRenderEnabled = !toggleRenderEnabled;
    }


    public Tile getTile(int i) {
        return tileMap.get(i);
    }

    // TODO remove null
    public Tile getTile(int x, int y) {
        var i = width * y + x;
        if (i >= tileMap.size() || i < 0 || x >= width || y >= height) {
            return null;
        }
        return getTile(i);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public TileType getTileTypeAt(int x, int y) {
        var tile = getTile(x, y);
        if (tile != null) {
            return tile.getTileType();
        }
        return null;
    }

    public TileType getTileTypeAt(int i) {
        var tile = getTile(i);
        if (tile != null) {
            return tile.getTileType();
        }
        return null;
    }

    public void render() {
        if (toggleRenderEnabled) {
            var playerPosition = new Vector2d();


            // Identify middle of the screen and identify point half way offset using height and width
            var xA = (WindowLifecycleManager.width/ Tile.getTileSize() / 2) - (width / 2);
            var yA = (WindowLifecycleManager.height / Tile.getTileSize() / 2) - (height / 2);

            // Draw each grid item
            for (int i = 0 ; i < tileMap.size(); i++) {

                // Extract x and y coordinates
                int x = i % width;
                int y = i / height;

                // Begin drawing at offset
                int xo = (x + xA) * Tile.getTileSize();
                int yo = (y + yA) * Tile.getTileSize();

//                glBegin(GL_QUADS);
//                glColor4f(0.25f,0.25f,0.25f, 1f);
//                glVertex2i(0, 0);
//                glVertex2i(0, height);
//                glVertex2i(width, height);
//                glVertex2i(width, 0);
//                glEnd();

                glBegin(GL_QUADS);

                // Draw grid item outline
                var color = getTile(x, y).getTileColor();
                if (!TileType.WALL.equals(getTile(x, y).getTileType())) {
                    color = GlColor.BLACK;
                }

                glColor3f(color.getRed(),color.getGreen(),color.getBlue());
                glVertex2i(xo, yo);
                glVertex2i(xo, yo + Tile.getTileSize());
                glVertex2i(xo + Tile.getTileSize(), yo + Tile.getTileSize());
                glVertex2i(xo + Tile.getTileSize(), yo);

                // Draw grid item
                if (TileType.WALL.equals(getTile(x, y).getTileType())) {
                    color = color.shadeBy(0.9f);
                } else {
                    color = GlColor.WHITE;
                }

                glColor3f(color.getRed(),color.getGreen(),color.getBlue());
                glVertex2i(xo + 1, yo + 1);
                glVertex2i(xo + 1, yo + Tile.getTileSize() - 1);
                glVertex2i(xo + Tile.getTileSize() - 1, yo + Tile.getTileSize() - 1);
                glVertex2i(xo - 1 + Tile.getTileSize(), yo + 1);
                glEnd();

                double playerX = (playerPosition.x + xA) * Tile.getTileSize();
                double playerY = (playerPosition.y + yA) * Tile.getTileSize();
                glColor3f(0, 1, 0);
                glPointSize(16);
                glBegin(GL_POINTS);
                glVertex2d(playerX, playerY);
                glEnd();

                // TODO convert to flat top down view
//                double directionX = (playerDirection.x + xA) * Tile.getTileSize();
//                double directionY = (playerDirection.y + yA) * Tile.getTileSize();
//                glLineWidth(30);
//                glBegin(GL_LINES);
//                glVertex2d(playerX, playerY);
//                glVertex2d(playerX + directionX, playerY + directionY);
//                glEnd();
            }
        }
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
