package dev.vcsocial.arayengine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import dev.vcsocial.arayengine.common.GlColor;
import dev.vcsocial.arayengine.component.Camera;
import dev.vcsocial.arayengine.manager.window.WindowLifecycleManager;
import jakarta.inject.Singleton;
import org.joml.Vector2d;

import java.util.Comparator;

import static org.joml.Math.max;
import static org.joml.Math.min;
import static org.lwjgl.opengl.GL33.*;

@Singleton
public class CameraSystem extends EntitySystem implements EntitySystemOrListener {

    public CameraSystem() {
//        super(Family.all(Camera.class).get(), new ZComparator());
    }

    private void cast(Camera camera) {
        var position = camera.position();
        var direction = camera.direction();
        var plane = camera.plane();

        for (int x = 0; x < WindowLifecycleManager.width; x++) {
            double cameraX = 2 * x / ((double) WindowLifecycleManager.width) - 1;
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

//                if (TileType.WALL.equals(EntryPoint.LEVEL_MAP.getTileTypeAt(mapX, mapY))) {
//                    hit = 1;
//                }
                hit = 1;
            }

            double perpWallDist = side == 0
                    ? sideDistX - deltaDistanceX
                    : sideDistY - deltaDistanceY;

            // height of the walls
            int lineHeight = (int) (WindowLifecycleManager.height / perpWallDist);

            // calc top and bottom of a wall
            int drawStart = -lineHeight / 2 + WindowLifecycleManager.height / 2;
            if (drawStart < 0) {
                drawStart = 0;
            }

            int drawEnd = lineHeight / 2 + WindowLifecycleManager.height / 2;
            if (drawStart >= WindowLifecycleManager.height) {
                drawEnd = WindowLifecycleManager.height - 1;
            }

            GlColor color = null;

            if (side == 1) {
                color = color.shadeBy(0.5f);
            }
            renderVerticalLine(x, drawStart, drawEnd, color);
        }
    }


    private void renderVerticalLine(int x, int bottomY, int topY, GlColor color) {
        if (topY < bottomY) {
            int temp = bottomY;
            bottomY = topY;
            topY = temp;
        } //swap y1 and y2

        if (topY < 0 || bottomY >= WindowLifecycleManager.height || x < 0 || x >= WindowLifecycleManager.width) {
            return;
        }//no single point of the line is on screen

        // Clamp value within screnn
        bottomY = max(0, bottomY);
        topY = min(WindowLifecycleManager.height - 1, topY);

        glColor3f(color.getRed(), color.getGreen(), color.getBlue());
        glLineWidth(8);
        glBegin(GL_LINES);
        glVertex2f(x, bottomY);
        glVertex2f(x, topY);
        glEnd();
    }

//    @Override
//    protected void processEntity(Entity entity, float v) {
//
//    }

    static class ZComparator implements Comparator<Entity> {
        @Override
        public int compare(Entity entityA, Entity entityB) {
            return (int)Math.signum(Camera.COMPONENT_MAPPER.get(entityA).position().x
                    - Camera.COMPONENT_MAPPER.get(entityB).position().x);
        }
    }
}
