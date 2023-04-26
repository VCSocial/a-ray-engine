package dev.vcsocial.arayengine.entity;

import dev.vcsocial.arayengine.common.Controllable;
import dev.vcsocial.arayengine.common.GlColor;
import dev.vcsocial.arayengine.common.GlColorCompatible;
import dev.vcsocial.arayengine.common.Renderable;
import dev.vcsocial.arayengine.util.AngleUtil;
import dev.vcsocial.arayengine.window.Window;
import dev.vcsocial.arayengine.world.LevelMap;
import dev.vcsocial.arayengine.world.TileType;
import org.joml.Vector2d;
import org.joml.Vector2i;

import static org.joml.Math.cos;
import static org.joml.Math.sin;
import static org.lwjgl.opengl.GL11.*;

public class Player implements Renderable, Controllable, Entity {

    private static final double halfPi = Math.PI / 2;
    private static final double thirdPi = 3 * Math.PI / 2;
    private static final float degInRad = 0.0174533f;

    private static final GlColorCompatible DEFAULT_COLOR = new GlColorCompatible(255, 0, 255);
    private static final int DEFAULT_SIZE = 8;

    private final LevelMap levelMap;

    private Vector2d position;
    private Vector2d direction;
    private Vector2d plane;

    private double frameTime = 0;
    private double time = 0;
    private double oldTime = 0;

    private float coordinateX;
    private float coordinateY;
    private float coordinateDeltaX;
    private float coordinateDeltaY;
    private float angle;

    private GlColorCompatible color = DEFAULT_COLOR;
    private int size = DEFAULT_SIZE;

    public Player(int coordinateX, int coordinateY, LevelMap levelMap) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.coordinateDeltaX = cos(angle) * 5;
        this.coordinateDeltaY = sin(angle) * 5;
        this.levelMap = levelMap;

        position = new Vector2d(coordinateX, coordinateY);
        direction = new Vector2d(1, 0);
        plane = new Vector2d(0, -0.66);
    }

    public float getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(int coordinateX) {
        this.coordinateX = coordinateX;
    }

    public float getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(int coordinateY) {
        this.coordinateY = coordinateY;
    }

    public float getCoordinateDeltaX() {
        return coordinateDeltaX;
    }

    public void setCoordinateDeltaX(float coordinateDeltaX) {
        this.coordinateDeltaX = coordinateDeltaX;
    }

    public float getCoordinateDeltaY() {
        return coordinateDeltaY;
    }

    public void setCoordinateDeltaY(float coordinateDeltaY) {
        this.coordinateDeltaY = coordinateDeltaY;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public GlColorCompatible getColor() {
        return color;
    }

    public void setColor(GlColorCompatible color) {
        this.color = color;
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

    public void updateX() {
        coordinateX += coordinateDeltaX;
        coordinateY += coordinateDeltaY;
    }

    public void updateY() {
        coordinateX -= coordinateDeltaX;
        coordinateY -= coordinateDeltaY;
    }

    public void updateAngle(float a) {
        angle += a;
        if (angle < 0) {
            angle += (2 * Math.PI);
        } else if ((2 * Math.PI) < angle) {
            angle -= (2 * Math.PI);
        }

        coordinateDeltaX = cos(angle) * 5;
        coordinateDeltaY = sin(angle) * 5;
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

            verLine(x, drawStart, drawEnd, color);
        }
    }

    void verLine(int x, int y1, int y2, GlColor color) {
        if (y2 < y1) {
            y1 += y2;
            y2 = y1 - y2;
            y1 -= y2;
        } //swap y1 and y2

        if (y2 < 0 || y1 >= Window.height || x < 0 || x >= Window.width) {
            return;
        }//no single point of the line is on screen


        if (y1 < 0)
            y1 = 0; //clip

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

    private GlColorCompatible shaded(GlColorCompatible c, float shade) {
        var r = c.getRed() == 0
                ? 0
                : c.getRed() * shade;

        var g = c.getGreen() == 0
                ? 0
                : c.getGreen() * shade;

        var b = c.getBlue() == 0
                ? 0
                : c.getBlue() * shade;

        return new GlColorCompatible((int) r, (int) g, (int) b);
    }

    @Override
    public Vector2i getMapPosition() {
        return new Vector2i((int) coordinateX, (int) coordinateY);
    }

    public void temp2() {
        int castLimit = 60;
        float distanceTotal = 0;
        float rayAngleFromCaster = AngleUtil.addLimitToCirlce(angle, -(degInRad *30));

        for (int rayIndex = 0; rayIndex < castLimit; rayIndex++) {
            var ray = new Ray(this, rayAngleFromCaster, levelMap);
            rayAngleFromCaster = ray.calculateRay(rayAngleFromCaster, rayIndex);
        }
    }

//    public void temp() {
//        int r, mx, my, mp, dof;
//        float rx, ry, ra, xo, yo, distT;
//        ra = AngleUtil.addLimitToCirlce( angle, -(degInRad * 30));
//
//        for (r = 0; r < 60; r++) {
//            GlColorCompatible color = new GlColorCompatible(255, 0, 0);
//
//            rx = 0;
//            ry = 0;
//            xo = 0;
//            yo = 0;
//
//            dof = 0;
//            float distanceH = Float.MAX_VALUE;
//            float hx = coordinateX;
//            float hy = coordinateY;
//            float aTan = -1 / tan(ra);
//            if (ra > Math.PI) {
//                ry = (((int) coordinateY >> 6) << 6) - 0.0001f;
//                rx = (coordinateY - ry) * aTan + coordinateX;
//                yo = -Tile.getTileSize();
//                xo = -yo * aTan;
//            }
//
//            if (ra < Math.PI) {
//                ry = (((int) coordinateY >> 6) << 6) + Tile.getTileSize();
//                rx = (coordinateY - ry) * aTan + coordinateX;
//                yo = Tile.getTileSize();
//                xo = -yo * aTan;
//            }
//
//            if (ra == 0 || ra == Math.PI) {
//                rx = coordinateX;
//                ry = coordinateY;
//                dof = 8;
//            }
//
//            if (r== 59) {
//                glColor3f(1, 0, 0);
//                glPointSize(16);
//                glBegin(GL_POINTS);
//                glVertex2f(rx, ry);
//                glEnd();
//
//                glColor3f(1, 1, 0);
//                glPointSize(16);
//                glBegin(GL_POINTS);
//                glVertex2f(xo, yo);
//                glEnd();
//            }
//
//            while (dof < 8) {
//                if (rx == 0 || ry == 0 || xo == 0 || yo == 0) {
//                    continue;
//                }
//
//                mx = (int) rx >> 6;
//                my = (int) ry >> 6;
//                mp = my * levelMap.getWidth() + mx;
//                if (mp < levelMap.getWidth() * levelMap.getHeight()
//                        && mp > -1
//                        && TileType.WALL.equals(levelMap.getTileTypeAt(mp))) {
//                    hx = rx;
//                    hy = ry;
//                    distanceH = DistanceUtil.distance(coordinateX, coordinateY, hx, hy, ra);
////                    color = levelMap.getTile(mp).getTileColor();
//                    dof = 8;
//                } else {
//                    rx += xo;
//                    ry += yo;
//                    dof += 1;
//                }
//            }
//
//            rx = 0;
//            ry = 0;
//            xo = 0;
//            yo = 0;
//
//            dof = 0;
//            float distanceV = Float.MAX_VALUE;
//            float vx = coordinateX;
//            float vy = coordinateY;
//            float nTan = -tan(ra);
//            if (ra > halfPi && ra < thirdPi) {
//                rx = (((int) coordinateX >> 6) << 6) - 0.0001f;
//                ry = (coordinateX - rx) * nTan + coordinateY;
//                xo = -Tile.getTileSize();
//                yo = -xo * nTan;
//            }
//
//            if (ra < halfPi || ra > thirdPi) {
//                rx = (((int) coordinateX >> 6) << 6) + Tile.getTileSize();
//                ry = (coordinateX - rx) * nTan + coordinateY;
//                xo = Tile.getTileSize();
//                yo = -xo * nTan;
//            }
//
//            if (ra == 0 || ra == Math.PI) {
//                rx = coordinateX;
//                ry = coordinateY;
//                dof = 8;
//            }
//
//            if (r == 59) {
//                glColor3f(0, 0, 1);
//                glPointSize(16);
//                glBegin(GL_POINTS);
//                glVertex2f(rx, ry);
//                glEnd();
//
//                glColor3f(0, 1, 1);
//                glPointSize(16);
//                glBegin(GL_POINTS);
//                glVertex2f(xo, yo);
//                glEnd();
//            }
//
//            while (dof < 8) {
//                if (rx == 0 || ry == 0 || xo == 0 || yo == 0) {
//                    continue;
//                }
//
//                mx = (int) rx >> 6;
//                my = (int) ry >> 6;
//                mp = my * levelMap.getWidth() + mx;
//                if (mp < levelMap.getWidth() * levelMap.getHeight()
//                        && mp > -1
//                        && TileType.WALL.equals(levelMap.getTileTypeAt(mp))) {
//                    vx = rx;
//                    vy = ry;
//                    distanceV = DistanceUtil.distance(coordinateX, coordinateY, vx, vy, ra);
//                    dof = 8;
////                    color = levelMap.getTile(mp).getTileColor();
//                } else {
//                    rx += xo;
//                    ry += yo;
//                    dof += 1;
//                }
//            }
//
//            if (distanceV < distanceH) {
//                rx = vx;
//                ry = vy;
//                distT = distanceV;
//            } else {
//                rx = hx;
//                ry = hy;
//                distT = distanceH;
//            }
//
//            glColor3f(1, 0, 0);
//            glLineWidth(3);
//            glBegin(GL_LINES);
//            glVertex2f(coordinateX, coordinateY);
//            glVertex2f(rx, ry);
//            glEnd();
//
//            if (distanceV < distanceH) {
//                color = shaded(color, 0.9f);
//            } else {
//                color = shaded(color, 0.7f);
//            }
//            glColor3f(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat());
//
//            // Draw 3D --- :D
//            float ca = AngleUtil.addLimitToCirlce( angle, -ra);
//            distT = distT * cos(ca);
//
//            float lineH = (levelMap.getWidth() * levelMap.getWidth() * 320) / distT;
//            float lineO = 160 - lineH / 2;
//            lineH = lineH > 320
//                    ? 320
//                    : lineH;
//
//            glLineWidth(8);
//            glBegin(GL_LINES);
//            glVertex2f(r * 8 + 530, lineO);
//            glVertex2f(r * 8 + 530, (lineH + lineO));
//            glEnd();
//
//            ra = AngleUtil.addLimitToCirlce(ra, degInRad);
//        }
//    }




    public void render() {
        cast();

//        glColor3f(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat());
//        glPointSize(size);
//        glBegin(GL_POINTS);
//        glVertex2f(coordinateX, coordinateY);
//        glEnd();
//
//        glLineWidth(30);
//        glBegin(GL_LINES);
//        glVertex2f(coordinateX, coordinateY);
//        glVertex2f(coordinateX + coordinateDeltaX * 75, coordinateY + coordinateDeltaY * 75);
//        glEnd();

    }
}
