package dev.benmitchell.mcpkg;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.SystemUtils;

public class Platform {
    private static final Path DOT_MINECRAFT_PATH;
    static {
        if (SystemUtils.IS_OS_WINDOWS)
            DOT_MINECRAFT_PATH = Paths.get(System.getenv("USERPROFILE"), ".minecraft");
        else if (SystemUtils.IS_OS_MAC)
            DOT_MINECRAFT_PATH = Paths.get(System.getenv("HOME"), "Library", "Application Support", "minecraft");
        else {
            DOT_MINECRAFT_PATH = Paths.get(System.getenv("HOME"), ".minecraft");
        }
    };

    public static File getDataPath() {
        Path dataPath;

        if (SystemUtils.IS_OS_WINDOWS)
            dataPath = Paths.get(System.getenv("APPDATA"), "mcpkg");
        else if (SystemUtils.IS_OS_MAC)
            dataPath = Paths.get(System.getenv("HOME"), "Library", "Application Support", "mcpkg");
        else if (SystemUtils.IS_OS_LINUX) {
            String partialDataPath = System.getenv("XDG_DATA_HOME");
            if (partialDataPath == null)
                dataPath = Paths.get(System.getenv("HOME"), ".local", "share", "mcpkg");
            else
                dataPath = Paths.get(partialDataPath, "mcpkg");
        } else
            throw new RuntimeException("Operating system is not supported");

        File f = new File(dataPath.toString());
        if (!f.exists())
            f.mkdirs();

        return f;
    }

    public static Path getResourcePacksDir() {
        return getDotMinecraftPath().resolve("resourcepacks");
    }
}
