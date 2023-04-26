package dev.vcsocial.arayengine;

import dev.vcsocial.arayengine.entity.Player;
import dev.vcsocial.arayengine.window.Window;
import dev.vcsocial.arayengine.world.LevelMap;
import org.eclipse.collections.api.factory.Lists;

public class EntryPoint {

    private static final LevelMap LEVEL_MAP = LevelMap.getDefaultLevelMap();
    private static final Player PLAYER = new Player(275, 275, LEVEL_MAP);
    private static final Window WINDOW = new Window(
            Lists.immutable.of(PLAYER::initControls),
            Lists.immutable.of(LEVEL_MAP::render, PLAYER::render)
    );

    public static void main(String[] args) {
        WINDOW.run();
    }
}