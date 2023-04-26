package dev.vcsocial.arayengine.entity;

import dev.vcsocial.arayengine.common.Controllable;
import dev.vcsocial.arayengine.common.GlColorCompatible;
import dev.vcsocial.arayengine.common.Renderable;
import dev.vcsocial.arayengine.util.DistanceUtil;
import dev.vcsocial.arayengine.world.LevelMap;
import dev.vcsocial.arayengine.world.Tile;
import dev.vcsocial.arayengine.world.TileType;
import org.joml.Vector2d;

import static org.joml.Math.*;
import static org.lwjgl.opengl.GL11.*;

public class Player implements Renderable, Controllable {

    private static final double halfPi = Math.PI / 2;
    private static final double thirdPi = 3 * Math.PI / 2;
    private static final float degInRad = 0.0174533f;

    private static final GlColorCompatible DEFAULT_COLOR = new GlColorCompatible(255, 0, 255);
    private static final int DEFAULT_SIZE = 8;

    private Vector2d position;
    private Vector2d direction;
    private Vector2d plane;

    private float coordinateX;
    private float coordinateY;
    private float coordinateDeltaX;
    private float coordinateDeltaY;
    private float angle;

    private GlColorCompatible color = DEFAULT_COLOR;
    private int size = DEFAULT_SIZE;

    public Player(int coordinateX, int coordinateY) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.coordinateDeltaX = cos(angle) * 5;
        this.coordinateDeltaY = sin(angle) * 5;

        position = new Vector2d(coordinateX, coordinateY);
        direction = new Vector2d(-1, 0);
        plane = new Vector2d(0, 0.66);
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

    public void temp() {
        int r, mx, my, mp, dof;
        float rx, ry, ra, xo, yo, distT;
        ra = angle - (degInRad * 30);
        if (ra < 0) {
            ra += 2 * Math.PI;
        }
        if (ra > 2 * Math.PI) {
            ra -= 2 * Math.PI;
        }

        for (r = 0; r < 60; r++) {
            rx = 0;
            ry = 0;
            xo = 0;
            yo = 0;

            dof = 0;
            float distanceH = Float.MAX_VALUE;
            float hx = coordinateX;
            float hy = coordinateY;
            float aTan = -1 / tan(ra);
            if (ra > Math.PI) {
                ry = (((int) coordinateY >> 6) << 6) - 0.0001f;
                rx = (coordinateY - ry) * aTan + coordinateX;
                yo = -Tile.getTileSize();
                xo = -yo * aTan;
            }

            if (ra < Math.PI) {
                ry = (((int) coordinateY >> 6) << 6) + Tile.getTileSize();
                rx = (coordinateY - ry) * aTan + coordinateX;
                yo = Tile.getTileSize();
                xo = -yo * aTan;
            }

            if (ra == 0 || ra == Math.PI) {
                rx = coordinateX;
                ry = coordinateY;
                dof = 8;
            }

            while (dof < 8) {
                if (rx == 0 || ry == 0 || xo == 0 || yo == 0) {
                    continue;
                }

                mx = (int) (rx / 64.0);
                my = (int) (ry / 64.0);
                mp = my * LevelMap.globalWidth + mx;
                if (mp < LevelMap.globalWidth * LevelMap.globalHeight
                        && mp > -1
                        && TileType.WALL.equals(LevelMap.myself.getTile(mp).getTileType())) {
                    hx = rx;
                    hy = ry;
                    distanceH = DistanceUtil.distance(coordinateX, coordinateY, hx, hy, ra);
                    dof = 8;
                } else {
                    rx += xo;
                    ry += yo;
                    dof += 1;
                }
            }

            rx = 0;
            ry = 0;
            xo = 0;
            yo = 0;

            dof = 0;
            float distanceV = Float.MAX_VALUE;
            float vx = coordinateX;
            float vy = coordinateY;
            float nTan = -tan(ra);
            if (ra > halfPi && ra < thirdPi) {
                rx = (((int) coordinateX >> 6) << 6) - 0.0001f;
                ry = (coordinateX - rx) * nTan + coordinateY;
                xo = -Tile.getTileSize();
                yo = -xo * nTan;
            }

            if (ra < halfPi || ra > thirdPi) {
                rx = (((int) coordinateX >> 6) << 6) + Tile.getTileSize();
                ry = (coordinateX - rx) * nTan + coordinateY;
                xo = Tile.getTileSize();
                yo = -xo * nTan;
            }

            if (ra == 0 || ra == Math.PI) {
                rx = coordinateX;
                ry = coordinateY;
                dof = 8;
            }

            while (dof < 8) {
                if (rx == 0 || ry == 0 || xo == 0 || yo == 0) {
                    continue;
                }

                mx = (int) rx >> 6;
                my = (int) ry >> 6;
                mp = my * LevelMap.globalWidth + mx;
                if (mp < LevelMap.globalWidth * LevelMap.globalHeight
                        && mp > -1
                        && TileType.WALL.equals(LevelMap.myself.getTile(mp).getTileType())) {
                    vx = rx;
                    vy = ry;
                    distanceV = DistanceUtil.distance(coordinateX, coordinateY, vx, vy, ra);
                    dof = 8;
                } else {
                    rx += xo;
                    ry += yo;
                    dof += 1;
                }
            }

            if (distanceV < distanceH) {
                rx = vx;
                ry = vy;
                distT = distanceV;
                glColor3f(0.9f, 0, 0);
            } else {
                rx = hx;
                ry = hy;
                distT = distanceH;
                glColor3f(0.7f, 0, 0);
            }

            glColor3f(1, 0, 0);
            glLineWidth(3);
            glBegin(GL_LINES);
            glVertex2f(coordinateX, coordinateY);
            glVertex2f(rx, ry);
            glEnd();

            // Draw 3D --- :D
            float ca = angle - ra;
            if (ca < 0) {
                ca += 2 * Math.PI;
            }
            if (ca > 2 * Math.PI) {
                ca -= 2 * Math.PI;
            }
            distT = distT * cos(ca);

            float lineH = (LevelMap.globalWidth * LevelMap.globalWidth * 320) / distT;
            float lineO = 160 - lineH / 2;
            lineH = lineH > 320
                    ? 320
                    : lineH;

            glLineWidth(8);
            glBegin(GL_LINES);
            glVertex2f(r * 8 + 530, lineO);
            glVertex2f(r * 8 + 530, (lineH + lineO));
            glEnd();

            ra += degInRad;
            if (ra < 0) {
                ra += 2 * Math.PI;
            }
            if (ra > 2 * Math.PI) {
                ra -= 2 * Math.PI;
            }
        }
    }


    public void render() {
        glColor3f(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat());
        glPointSize(size);
        glBegin(GL_POINTS);
        glVertex2f(coordinateX, coordinateY);
        glEnd();

        glLineWidth(3);
        glBegin(GL_LINES);
        glVertex2f(coordinateX, coordinateY);
        glVertex2f(coordinateX + coordinateDeltaX * 5, coordinateY + coordinateDeltaY * 5);
        glEnd();

        temp();
    }
}
