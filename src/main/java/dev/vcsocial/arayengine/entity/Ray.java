package dev.vcsocial.arayengine.entity;

import dev.vcsocial.arayengine.common.Renderable;
import dev.vcsocial.arayengine.util.AngleUtil;
import dev.vcsocial.arayengine.util.DistanceUtil;
import dev.vcsocial.arayengine.world.LevelMap;
import dev.vcsocial.arayengine.world.Tile;
import dev.vcsocial.arayengine.world.TileType;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.joml.Math.cos;
import static org.joml.Math.tan;
import static org.lwjgl.opengl.GL11.*;

public class Ray implements Renderable {
    private static final int DEPTH_LIMIT = 8;
    private static final double PI_TWO = Math.PI / 2;
    private static final double THREE_PI_TWO = 3 * Math.PI / 2;
    private static final float DEGREE_IN_RADIANS = 0.0174533f;

    /*
    [ cos(a) -sin(a) ]
    [ sin(a)  cos(a) ]
     */

    private final Entity eminator;
    private Vector2f rayDirection;
    private RayType rayType;
    private float rayAngle;
    private final LevelMap levelMap;
    private Vector2f rayPosition;
    private Vector2f rayOffset;
    private float rayDistanceToTarget;
    private int rayIndex = -1;

    public Ray(Entity eminator, float rayAngle, LevelMap levelMap) {
        this.eminator = eminator;
        this.rayAngle = rayAngle;
        this.levelMap = levelMap;
        rayPosition = new Vector2f(0, 0);
        rayOffset = new Vector2f(0, 0);

    }

    private static int shiftBitsSymmetricBy(float x, int shiftValue) {
        return (((int) x >> shiftValue) << shiftValue);
    }

    private Vector3f calculateHits(Vector2f axisVector) {
        int calculatedDepth = 0;
        float distance = Float.MAX_VALUE;

        while (calculatedDepth < DEPTH_LIMIT) {
            Vector2f mapPosition = new Vector2f((int) rayPosition.x >> 6, (int) rayPosition.y >> 6);
            if (TileType.WALL.equals(levelMap.getTileTypeAt((int) mapPosition.x, (int) mapPosition.y))) {
                axisVector = new Vector2f(rayPosition);
                distance = DistanceUtil.distance(new Vector2f(eminator.getMapPosition()), axisVector, rayAngle);
//                color = levelMap.getTile(mp).getTileColor();
                calculatedDepth = 8;
            } else {
                rayPosition.add(rayOffset);
                calculatedDepth++;
            }
        }
        return new Vector3f(axisVector.x, axisVector.y, distance);
    }

    private Vector3f calculateRayHorizontal() {
        if (rayAngle == 0 || rayAngle == Math.PI) {
            return new Vector3f(eminator.getMapPosition().x, eminator.getMapPosition().y, Float.MAX_VALUE);
        }

        var horizontalVector = new Vector2f(eminator.getMapPosition());
        float aTan = -1 / tan(rayAngle);
        if (rayAngle > Math.PI) {
            rayPosition.y = shiftBitsSymmetricBy(eminator.getMapPosition().x, 6) - 0.0001f;
            rayOffset.y = -Tile.getTileSize();
        } else if (rayAngle < Math.PI) {
            rayPosition.y = shiftBitsSymmetricBy(eminator.getMapPosition().x, 6) + Tile.getTileSize();
            rayOffset.y = Tile.getTileSize();
        }
        rayPosition.x = (horizontalVector.y - rayPosition.y) * aTan + horizontalVector.x;
        rayOffset.x = -rayOffset.y * aTan;

        if (rayIndex == 59) {
            glColor3f(1, 0, 0);
            glPointSize(16);
            glBegin(GL_POINTS);
            glVertex2f(rayPosition.x, rayPosition.y);
            glEnd();

            glColor3f(1, 1, 0);
            glPointSize(16);
            glBegin(GL_POINTS);
            glVertex2f(rayOffset.x, rayOffset.y);
            glEnd();
        }

        return calculateHits(horizontalVector);
    }

    private Vector3f calculateRayVertical() {
        if (rayAngle == 0 || rayAngle == Math.PI) {
            return new Vector3f(eminator.getMapPosition().x, eminator.getMapPosition().y, Float.MAX_VALUE);
        }

        var verticalVector = new Vector2f(eminator.getMapPosition());
        float nTan = -tan(rayAngle);
        if (rayAngle > PI_TWO && rayAngle < THREE_PI_TWO) {
            rayPosition.x = shiftBitsSymmetricBy(eminator.getMapPosition().x, 6) - 0.0001f;
            rayOffset.x = -Tile.getTileSize();
        } else if (rayAngle < PI_TWO || rayAngle > THREE_PI_TWO) {
            rayPosition.x = shiftBitsSymmetricBy(eminator.getMapPosition().x, 6) + Tile.getTileSize();
            rayOffset.x = Tile.getTileSize();
        }
        rayPosition.y = (verticalVector.x - rayPosition.x) * nTan + verticalVector.y;
        rayOffset.y = -rayOffset.x * nTan;

        if (rayIndex == 59) {
            glColor3f(0, 0, 1);
            glPointSize(16);
            glBegin(GL_POINTS);
            glVertex2f(rayPosition.x, rayPosition.y);
            glEnd();

            glColor3f(0, 1, 1);
            glPointSize(16);
            glBegin(GL_POINTS);
            glVertex2f(rayOffset.x, rayOffset.y);
            glEnd();
        }

        return calculateHits(verticalVector);
    }

    public float calculateRay(float rayAngle, int rayIndex) {
        this.rayAngle = rayAngle;
        this.rayIndex = rayIndex;
        var horizontalRayResult = calculateRayHorizontal();
        var verticalRayResult = calculateRayVertical();

        if (verticalRayResult.z < horizontalRayResult.z) {
            rayPosition = new Vector2f(verticalRayResult.x, verticalRayResult.y);
            rayDistanceToTarget = verticalRayResult.z;

        } else {
            rayPosition = new Vector2f(horizontalRayResult.x, horizontalRayResult.y);
            rayDistanceToTarget = horizontalRayResult.z;
        }

        glColor3f(0, 1, 0);
        glLineWidth(3);
        glBegin(GL_LINES);
        glVertex2f(eminator.getMapPosition().x, eminator.getMapPosition().y);
        glVertex2f(rayPosition.x, rayPosition.y);
        glEnd();

        return renderRay();
    }

    public float renderRay() {
        render();
        return AngleUtil.addLimitToCirlce(rayAngle, DEGREE_IN_RADIANS);
    }

    @Override
    public void render() {
        float ca = AngleUtil.addLimitToCirlce(eminator.getAngle(), -rayAngle);
        rayDistanceToTarget = rayDistanceToTarget * cos(ca);

        float lineH = (levelMap.getWidth() * levelMap.getWidth() * 320) / rayDistanceToTarget;
        float lineO = 160 - lineH / 2;
        lineH = lineH > 320
                ? 320
                : lineH;

        glColor3f(0, 0, 1);
        glLineWidth(8);
        glBegin(GL_LINES);
        glVertex2f(rayIndex * 8 + 530, lineO);
        glVertex2f(rayIndex * 8 + 530, (lineH + lineO));
        glEnd();
    }
}
