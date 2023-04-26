package dev.vcsocial.arayengine.entity;

import dev.vcsocial.arayengine.common.Controllable;
import dev.vcsocial.arayengine.common.GlColor;
import dev.vcsocial.arayengine.common.Renderable;
import dev.vcsocial.arayengine.window.Window;
import dev.vcsocial.arayengine.world.LevelMap;
import dev.vcsocial.arayengine.world.TileType;
import org.joml.Vector2d;
import org.joml.Vector2i;

import static org.joml.Math.*;
import static org.lwjgl.opengl.GL11.*;

public class Player implements Renderable, Controllable, Entity {

//    static record RenderColumnInfo(int x, int bottomY, int topY, GlColor color) implements Comparable<RenderColumnInfo> {
//        @Override
//        public int compareTo(RenderColumnInfo o) {
//            if (x > o.x) {
//                return 1;
//            } else if (x < o.x) {
//                return -1;
//            }
//            return 0;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            RenderColumnInfo that = (RenderColumnInfo) o;
//            return x == that.x && bottomY == that.bottomY && topY == that.topY && color.equals(that.color);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(x, bottomY, topY, color);
//        }
//    }

    private final LevelMap levelMap;

    private Vector2d position;
    private Vector2d direction;
    private Vector2d plane;
    private boolean toggleRenderEnabled = false;

    public Player(int coordinateX, int coordinateY, LevelMap levelMap) {
        this.levelMap = levelMap;
        position = new Vector2d(coordinateX, coordinateY);
        direction = new Vector2d(-1, 0);
        plane = new Vector2d(0, 0.66);
    }

    public Vector2d getPosition() {
        return position;
    }

    // https://lodev.org/cgtutor/raycasting.html#The_Basic_Idea_
    // w is screenWidth
    // h is screenHeight
    public void cast() {
//        MutableMap<GlColor, Pair<RenderColumnInfo, RenderColumnInfo>> renderMap = Maps.mutable.empty();
        // Map<GlColor, Map.Entry<RenderInfo, RenderInfo>> renderMap = new HashMap<>();

        for (int x = 0; x < Window.width ; x++) {
            double cameraX = 2 * x / ((double) Window.width) - 1;
            var rayDirection = new Vector2d(
                    direction.x + plane.x * cameraX,
                    direction.y + plane.y * cameraX);

            int mapX = (int) position.x;
            int mapY = (int) position.y;

            double deltaDistanceX = Math.abs(1.0 / rayDirection.x);
            double deltaDistanceY = Math.abs(1.0 / rayDirection.y);

            double sideDistX;
            double sideDistY;

            int stepX;
            int stepY;

            if (rayDirection.x < 0) {
                stepX = -1;
                sideDistX = (position.x - mapX) * deltaDistanceX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1.0 - position.x) * deltaDistanceX;
            }

            if (rayDirection.y < 0) {
                stepY = -1;
                sideDistY = (position.y - mapY) * deltaDistanceY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1.0 - position.y) * deltaDistanceY;
            }

            int hit = 0;
            int side = 0;

            while (hit == 0) {
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistanceX;
                    mapX += stepX;
                    side = 0;
                } else {
                    sideDistY += deltaDistanceY;
                    mapY += stepY;
                    side = 1;
                }

                if (TileType.WALL.equals(levelMap.getTileTypeAt(mapX, mapY))) {
                    hit = 1;
                }
            }

            double perpWallDist = side == 0
                    ? sideDistX - deltaDistanceX
                    : sideDistY - deltaDistanceY;

            // height of the walls
            int lineHeight = (int) (Window.height / perpWallDist);

            // calc top and bottom of a wall
            int drawStart = -lineHeight / 2 + Window.height / 2;
            if (drawStart < 0) {
                drawStart = 0;
            }

            int drawEnd = lineHeight / 2 + Window.height / 2;
            if (drawStart >= Window.height) {
                drawEnd = Window.height - 1;
            }

            GlColor color = levelMap.getTile(mapX, mapY).getTileColor();
            if (side == 1) {
                color = color.shadeBy(0.5f);
            }
            renderVerticalLine(x, drawStart, drawEnd, color);
        }
//
//            var rec = captureVerticalLine(x, drawStart, drawEnd, color);
//
//            if (!renderMap.containsKey(rec.color)) {
//                var str = """
//                    [x=%s]
//                    [r=%s], [g=%s], [b=%s]
//                    [col X=%s] [col Bottom Y=%s] [col Top Y=%s]
//                    """.formatted(x, color.getRed(), color.getGreen(), color.getBlue(),
//                        rec.x, rec.bottomY, rec.topY);
//                System.out.println(str);
//                renderMap.put(rec.color, Tuples.pair(rec, rec));
//            } else {
//                var storedRecPair = renderMap.get(rec.color);
//                if (storedRecPair.getOne().x > rec.x) {
//                    renderMap.replace(rec.color, Tuples.pair(rec, storedRecPair.getTwo()));
//                } else if (storedRecPair.getTwo().x < rec.x) {
//                    renderMap.replace(rec.color, Tuples.pair(storedRecPair.getOne(), rec));
//                }
//            }
//        }

        // Quad based rendering ALMOST works :/
        // Wall is pushed back effect
//        var r = new TreeSortedMap<>(renderMap.flipUniqueValues());
//
//        r.keySet().forEach(recPair -> {
//            var c = recPair.getOne().color;
//            var beginColumn = recPair.getOne();
//            var endColumn = recPair.getTwo();
//            var str = """
//                    [r=%s], [g=%s], [b=%s]
//                    [recOne X=%s] [recOne Bottom Y=%s] [recOne Top Y=%s]
//                    [recTwo X=%s] [recTwo Bottom Y=%s] [recTwo Top Y=%s]
//                    """.formatted(c.getRed(), c.getGreen(), c.getBlue(),
//                    beginColumn.x, beginColumn.bottomY, beginColumn.topY,
//                    endColumn.x, endColumn.bottomY, endColumn.topY);
//
//            System.out.println(str);
//
//            glBegin(GL_QUADS);
//            glColor3f(c.getRed(), c.getGreen(), c.getBlue());
//
//
//            glVertex2f(beginColumn.x, beginColumn.bottomY);
//            glVertex2f(beginColumn.x, beginColumn.topY);
//            glVertex2f(endColumn.x, endColumn.topY);
//            glVertex2f(endColumn.x, endColumn.bottomY);
//            glEnd();
//        });
    }

//    private RenderColumnInfo captureVerticalLine(int x, int bottomY, int topY, GlColor color) {
//        if (topY < bottomY) {
//            int temp = bottomY;
//            bottomY = topY;
//            topY = temp;
//        } //swap y1 and y2
//
//        if (topY < 0 || bottomY >= Window.height || x < 0 || x >= Window.width) {
//            return null;
//        }//no single point of the line is on screen
//
//        if (bottomY < 0) {
//            bottomY = 0; //clip
//        }
//
//        if (topY >= Window.width)
//            topY = Window.height - 1; //clip
//
//        return new RenderColumnInfo(x, bottomY, topY, color);
//    }

    // Draws a vertical line on the screen ported from the lodev tutorial more or less verbatim
    // TODO understand this better and come up with my own solution better fitted fro Java
    private void renderVerticalLine(int x, int bottomY, int topY, GlColor color) {
        if (topY < bottomY) {
            int temp = bottomY;
            bottomY = topY;
            topY = temp;
        } //swap y1 and y2

        if (topY < 0 || bottomY >= Window.height || x < 0 || x >= Window.width) {
            return;
        }//no single point of the line is on screen

        // Clamp value within screnn
        bottomY = max(0, bottomY);
        topY = min(Window.height - 1, topY);

        glColor3f(color.getRed(), color.getGreen(), color.getBlue());
        glLineWidth(8);
        glBegin(GL_LINES);
        glVertex2f(x, bottomY);
        glVertex2f(x, topY);
        glEnd();
    }

    public void moveForward() {
        var x = position.x + direction.x * getSpeed();
        if (!TileType.WALL.equals(levelMap.getTileTypeAt((int) x, (int) position.y))) {
            position.x += direction.x * getSpeed();
        }

        var y = position.y + direction.y * getSpeed();
        if (!TileType.WALL.equals(levelMap.getTileTypeAt((int) position.x, (int) y))) {
            position.y += direction.y * getSpeed();
        }
    }

    public void moveBackward() {
        var x = position.x - direction.x * getSpeed();
        if (!TileType.WALL.equals(levelMap.getTileTypeAt((int) x, (int) position.y))) {
            position.x -= direction.x * getSpeed();
        }

        var y = position.y - direction.y * getSpeed();
        if (!TileType.WALL.equals(levelMap.getTileTypeAt((int) position.x, (int) y))) {
            position.y -= direction.y * getSpeed();
        }
    }

    @Override
    public void moveLeft() {
        var x = position.x - plane.x * getSpeed();
        if (!TileType.WALL.equals(levelMap.getTileTypeAt((int) x, (int) position.y))) {
            position.x -= plane.x * getSpeed();
        }

        var y = position.y - plane.y * getSpeed();
        if (!TileType.WALL.equals(levelMap.getTileTypeAt((int) position.x, (int) y))) {
            position.y -= plane.y * getSpeed();
        }
    }

    @Override
    public void moveRight() {
        var x = position.x + plane.x * getSpeed();
        if (!TileType.WALL.equals(levelMap.getTileTypeAt((int) x, (int) position.y))) {
            position.x += plane.x * getSpeed();
        }

        var y = position.y + plane.y * getSpeed();
        if (!TileType.WALL.equals(levelMap.getTileTypeAt((int) position.x, (int) y))) {
            position.y += plane.y * getSpeed();
        }
    }

    public void rotateLeft() {
        double oldDirectionX = direction.x;
        direction.x = direction.x * cos(getRotationSpeed()) - direction.y * sin(getRotationSpeed());
        direction.y = oldDirectionX * sin(getRotationSpeed()) + direction.y * cos(getRotationSpeed());

        double oldPlaneX = plane.x;
        plane.x = plane.x * cos(getRotationSpeed()) - plane.y * sin(getRotationSpeed());
        plane.y = oldPlaneX * sin(getRotationSpeed()) + plane.y * cos(getRotationSpeed());
    }

    public void rotateRight() {
        double oldDirectionX = direction.x;
        direction.x = direction.x * cos(-getRotationSpeed()) - direction.y * sin(-getRotationSpeed());
        direction.y = oldDirectionX * sin(-getRotationSpeed()) + direction.y * cos(-getRotationSpeed());

        double oldPlaneX = plane.x;
        plane.x = plane.x * cos(-getRotationSpeed()) - plane.y * sin(-getRotationSpeed());
        plane.y = oldPlaneX * sin(-getRotationSpeed()) + plane.y * cos(-getRotationSpeed());
    }

    @Override
    public Vector2i getMapPosition() {
        return new Vector2i((int) position.x, (int) position.y);
    }

    public void render() {
        cast();
    }
}
