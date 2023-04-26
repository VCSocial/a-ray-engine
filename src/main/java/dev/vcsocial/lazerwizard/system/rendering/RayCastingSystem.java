package dev.vcsocial.lazerwizard.system.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import dev.vcsocial.lazerwizard.component.*;
import dev.vcsocial.lazerwizard.core.manager.tile.TileMapManager;
import dev.vcsocial.lazerwizard.core.manager.window.WindowManager;
import dev.vcsocial.lazerwizard.system.EntitySystemOrListener;
import jakarta.inject.Singleton;
import org.eclipse.collections.api.factory.Lists;
import org.joml.Vector2f;

import java.util.List;

import static org.joml.Math.max;

@Singleton
public class RayCastingSystem extends IteratingSystem implements EntitySystemOrListener {

    // tile[position.x / TILE_WIDTH][position.y / TILE_HEIGHT]

    // TODO allow resizing
    private final TileMapManager tileMapManager;

    private final int windowWidth;
    private final int windowHeight;

    public RayCastingSystem(WindowManager windowManager, TileMapManager tileMapManager) {
        super(Family.all(CameraComponent.class, PositionComponent.class, LineMeshGroup.class).get(), 2);

        this.tileMapManager = tileMapManager;
        windowWidth = windowManager.getWidth();
        windowHeight = windowManager.getHeight();
    }

    private float toNdc(float f, boolean isX) {
        if (isX) {
            float ndcWidth = windowWidth / 2f;
            return (f - ndcWidth) / ndcWidth;
        } else {
            float ndcHeight = windowHeight / 2f;
            return (f - ndcHeight) / ndcHeight;
        }
    }

    private LineMesh computeVertical(int x, int bottomY, int topY, GlColor color) {
        //swap top and bottom
        if (topY < bottomY) {
            int temp = bottomY;
            bottomY = topY;
            topY = temp;
        }

        //no single point of the line is on screen
        if (topY < 0 || bottomY >= windowHeight || x < 0 || x >= windowWidth) {
            return null;
        }

        bottomY = max(0, bottomY);
        if (topY >= windowWidth) {
            topY = windowHeight - 1;
        }
        return new LineMesh(toNdc(x, true), toNdc(bottomY, false), toNdc(topY, false), color);
    }

    private List<LineMesh> computeRays(PositionComponent position, Vector2f direction, Vector2f plane) {
        List<LineMesh> lineMeshList = Lists.mutable.empty();

        for (int x = 0; x < windowWidth; x++) {
            float cameraX = 2.0f * x / windowWidth - 1;

            float rayDirectionX = direction.x + plane.x * cameraX;
            float rayDirectionY = direction.y + plane.y * cameraX;

            int mapX = (int) position.x;
            int mapY = (int) position.y;

            float deltaDistanceX = (float) Math.abs(1.0 / rayDirectionX);
            float deltaDistanceY = (float) Math.abs(1.0 / rayDirectionY);

            int stepX = rayDirectionX < 0 ? -1 : 1;
            int stepY = rayDirectionY < 0 ? -1 : 1;

            float sideDistanceX = rayDirectionX < 0
                    ? (position.x - mapX) * deltaDistanceX
                    : (mapX + 1 - position.x) * deltaDistanceX;

            float sideDistanceY = rayDirectionY < 0
                    ? (position.y - mapY) * deltaDistanceY
                    : (mapY + 1 - position.y) * deltaDistanceY;

            int hit = 0;
            int side;

            do {
                if (sideDistanceX < sideDistanceY) {
                    sideDistanceX += deltaDistanceX;
                    mapX += stepX;
                    side = 0;
                } else {
                    sideDistanceY += deltaDistanceY;
                    mapY += stepY;
                    side = 1;
                }

                var tileType = tileMapManager.getTileTypeAt(mapX, mapY);
                if (TileType.WALL.equals(tileType)) { // TODO get wall here!
                    hit = 1;
                }

            } while (hit == 0);

            double perpendicularWallDistance = side == 0
                    ? sideDistanceX - deltaDistanceX
                    : sideDistanceY - deltaDistanceY;

            // height of the walls
            int lineHeight = (int) (windowHeight / perpendicularWallDistance);

            // calc top and bottom of a wall
            int drawStart = -lineHeight / 2 + windowHeight / 2;
            if (drawStart < 0) {
                drawStart = 0;
            }

            int drawEnd = lineHeight / 2 + windowHeight / 2;
            if (drawStart >= windowHeight) {
                drawEnd = windowHeight - 1;
            }

            GlColor color = tileMapManager.getTile(mapX, mapY).getTileColor();
            if (side == 1) {
                color = new GlColor(color.r / 2, color.g / 2, color.b / 2, color.alpha);
            }
            var lineMeshWall = computeVertical(x, drawStart, drawEnd, color);
            var lineMeshFloor = computeVertical(x, 0, drawStart, new GlColor(0.25f,0.25f, 0.25f, 1 ));
            var lineMeshCeiling = computeVertical(x, drawEnd, windowHeight, new GlColor(0.75f, 0.75f, 0.75f, 1));

            if (lineMeshWall != null) {
                lineMeshList.add(lineMeshWall);
            }
            if (lineMeshFloor != null) {
                lineMeshList.add(lineMeshFloor);
            }
            if (lineMeshCeiling != null) {
                lineMeshList.add(lineMeshCeiling);
            }
//            lineMeshList.add(new LineMesh(x, toNdc(0f, false), drawStart, GlColor.BLACK)); //  TODO Giant black line in middle of screen :D
//            lineMeshList.add(computeVertical());
        }
        return lineMeshList;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var camera = CameraComponent.COMPONENT_MAPPER.get(entity);
        var position = PositionComponent.COMPONENT_MAPPER.get(entity);

        var group = new LineMeshGroup();
        group.lineMeshList = computeRays(position, camera.direction, camera.plane);

        entity.remove(LineMeshGroup.class);
        entity.add(group);
    }
}
