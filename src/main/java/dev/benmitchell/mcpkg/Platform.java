package dev.benmitchell.mcpkg;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.SystemUtils;

import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;

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
        return DOT_MINECRAFT_PATH.resolve("resourcepacks");
    }

    public static boolean isADataPacksDir(Path directory) {
        return directory // .minecraft/saves/some_save_folder/datapacks
                .getParent() // .minecraft/saves/some_save_folder
                .getParent() // .minecraft/saves
                .equals(DOT_MINECRAFT_PATH.resolve("saves")) && directory.getFileName().equals(Paths.get("datapacks"));
    }

    public static Path getDataPacksDir() throws InvalidDirectoryException {
        Path cwd = Paths.get(SystemUtils.USER_DIR);

        // If we're in a datapacks directory inside .minecraft
        if (isADataPacksDir(cwd))
            return cwd;
        // If we're in a worlds directory inside .minecraft
        if (cwd // .minecraft/saves/some_save_folder
                .getParent() // .minecraft/saves
                .equals(DOT_MINECRAFT_PATH.resolve("saves")))
            return cwd.resolve("datapacks");

        throw new InvalidDirectoryException(cwd, "A unique data pack directory couldn't be found");
    }
}
