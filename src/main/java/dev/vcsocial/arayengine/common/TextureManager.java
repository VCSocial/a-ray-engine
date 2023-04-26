package dev.vcsocial.arayengine.common;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TextureManager {
    private static final int BYTES_PER_PIXEL = 4;//3 for RGB, 4 for RGBA
    private static final Set<String> TEXTURE_PATHS = Set.of(
            "pics/barrel.png",
            "pics/bluestone.png",
            "pics/eagle.png",
            "pics/greenlight.png",
            "pics/redbrick.png",
            "pics/colorstone.png"
    );
    public static List<Texture> textures;

    public TextureManager() {
        try {
            loadAllTextures();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load textures");
        }
    }

    public void loadAllTextures() throws IOException {
        textures = TEXTURE_PATHS.stream()
                .map(Texture::new)
                .collect(Collectors.toList());
    }

    // https://stackoverflow.com/questions/10801016/lwjgl-textures-and-strings
}
