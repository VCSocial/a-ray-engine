package dev.vcsocial.arayengine.entity;

import dev.vcsocial.arayengine.common.Controllable;
import dev.vcsocial.arayengine.common.GlColor;
import dev.vcsocial.arayengine.common.Renderable;
import dev.vcsocial.arayengine.window.Window;
import dev.vcsocial.arayengine.world.LevelMap;
import dev.vcsocial.arayengine.world.TileType;
import org.joml.Vector2d;
import org.joml.Vector2i;

import static org.joml.Math.cos;
import static org.joml.Math.sin;
import static org.lwjgl.opengl.GL11.*;

public class Player implements Renderable, Controllable, Entity {

    private static final float degInRad = 0.0174533f;

    private static final GlColor DEFAULT_COLOR = new GlColor(255, 0, 255);
    private static final int DEFAULT_SIZE = 8;

    private final LevelMap levelMap;

    private Vector2d position;
    private Vector2d direction;
    private Vector2d plane;
    private boolean toggleRenderEnabled = false;

    private final GlColor color;
    private int size = DEFAULT_SIZE;

    public Player(int coordinateX, int coordinateY, LevelMap levelMap) {
        this.levelMap = levelMap;
        position = new Vector2d(coordinateX, coordinateY);
        direction = new Vector2d(-1, 0);
        plane = new Vector2d(0, 0.66);
        color = GlColor.GREEN;
    }

    public GlColor getColor() {
        return color;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Vector2d getPosition() {
        return position;
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public Vector2d getDirection() {
        return direction;
    }

    public void setDirection(Vector2d direction) {
        this.direction = direction;
    }

    public Vector2d getPlane() {
        return plane;
    }

    public void setPlane(Vector2d plane) {
        this.plane = plane;
    }

    // https://lodev.org/cgtutor/raycasting.html#The_Basic_Idea_
    // w is screenWidth
    // h is screenHeight
    public void cast() {
        for (int x = 0; x < Window.width ; x++) {
            double cameraX = 2 * x / ((double) Window.width) - 1;
            var ray = new Ray(this, levelMap);
            ray.setRayDirectionX(direction.x + plane.x * cameraX);
            ray.setRayDirectionY(direction.y + plane.y * cameraX);

            int mapX = (int) position.x;
            int mapY = (int) position.y;

            double deltaDistanceX = Math.abs(1.0 / ray.getRayDirection().x);
            double deltaDistanceY = Math.abs(1.0 / ray.getRayDirection().y);

            double sideDistX;
            double sideDistY;

            int stepX;
            int stepY;

            if (ray.getRayDirection().x < 0) {
                stepX = -1;
                sideDistX = (position.x - mapX) * deltaDistanceX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1.0 - position.x) * deltaDistanceX;
            }

            if (ray.getRayDirection().y < 0) {
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
                color.shadeBy(0.5f);
            }
            renderVerticalLine(x, drawStart, drawEnd, color);
        }
    }

    // Draws a vertical line on the screen ported from the lodev tutorial more or less verbatim
    // TODO understand this better and come up with my own solution better fitted fro Java
    private void renderVerticalLine(int x, int y1, int y2, GlColor color) {
        if (y2 < y1) {
            y1 += y2;
            y2 = y1 - y2;
            y1 -= y2;
        } //swap y1 and y2

        if (y2 < 0 || y1 >= Window.height || x < 0 || x >= Window.width) {
            return;
        }//no single point of the line is on screen

        if (y1 < 0) {
            y1 = 0; //clip
        }

        if (y2 >= Window.width)
            y2 = Window.height - 1; //clip

        if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
            glColor3f(1, 1, 0);
        } else {
            glColor3f(color.getRed(), color.getGreen(), color.getBlue());
        }

        glLineWidth(8);
        glBegin(GL_LINES);
        glVertex2f(x, y1);
        glVertex2f(x, y2);
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

    private GlColor shaded(GlColor c, float shade) {
        var r = c.getRed() == 0
                ? 0
                : c.getRed() * shade;

        var g = c.getGreen() == 0
                ? 0
                : c.getGreen() * shade;

        var b = c.getBlue() == 0
                ? 0
                : c.getBlue() * shade;

        return new GlColor((int) r, (int) g, (int) b);
    }

    public void toggleRendering() {
        toggleRenderEnabled = !toggleRenderEnabled;
    }

    @Override
    public Vector2i getMapPosition() {
        return new Vector2i((int) position.x, (int) position.y);
    }

    public void render() {
        cast();
    }
}
