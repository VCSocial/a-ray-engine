package dev.vcsocial.arayengine;

import dev.vcsocial.arayengine.common.MouseInput;
import dev.vcsocial.arayengine.common.TextureManager;
import dev.vcsocial.arayengine.entity.Player;
import dev.vcsocial.arayengine.window.Window;
import dev.vcsocial.arayengine.world.LevelMap;
import org.eclipse.collections.api.factory.Lists;


public class EntryPoint {

    public static final MouseInput MOUSE_INPUT = new MouseInput();
    public static final LevelMap LEVEL_MAP = LevelMap.getDefaultLevelMap();
    public static final Player PLAYER = new Player(4, 4, LEVEL_MAP);
    private static final Window WINDOW = new Window(
            Lists.immutable.of(PLAYER::initControls, MOUSE_INPUT::init),
            Lists.immutable.of(PLAYER::render, LEVEL_MAP::render, MOUSE_INPUT::input, TextureManager::new)
    );

    public static void main(String[] args) {
        WINDOW.run();
    }
}