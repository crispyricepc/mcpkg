package dev.benmitchell.mcpkg;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.SystemUtils;

import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;

public class Platform {
    public static final Path DOT_MINECRAFT_PATH;
    public static final Path DATA_PATH;
    public static final Path CONFIG_PATH;
    static {
        if (SystemUtils.IS_OS_WINDOWS)
            DOT_MINECRAFT_PATH = Paths.get(System.getenv("USERPROFILE"), ".minecraft");
        else if (SystemUtils.IS_OS_MAC)
            DOT_MINECRAFT_PATH = Paths.get(System.getenv("HOME"), "Library", "Application Support", "minecraft");
        else {
            DOT_MINECRAFT_PATH = Paths.get(System.getenv("HOME"), ".minecraft");
        }

        if (SystemUtils.IS_OS_WINDOWS)
            DATA_PATH = Paths.get(System.getenv("APPDATA"), "mcpkg");
        else if (SystemUtils.IS_OS_MAC)
            DATA_PATH = Paths.get(System.getenv("HOME"), "Library", "Application Support", "mcpkg");
        else {
            String partialDataPath = System.getenv("XDG_DATA_HOME");
            if (partialDataPath == null)
                DATA_PATH = Paths.get(System.getenv("HOME"), ".local", "share", "mcpkg");
            else
                DATA_PATH = Paths.get(partialDataPath, "mcpkg");
        }

        if (SystemUtils.IS_OS_WINDOWS)
            CONFIG_PATH = DATA_PATH;
        else if (SystemUtils.IS_OS_MAC)
            CONFIG_PATH = Paths.get(System.getenv("HOME"), "Library", "Preferences", "mcpkg");
        else {
            String partialConfigPath = System.getenv("XDG_CONFIG_HOME");
            if (partialConfigPath == null)
                CONFIG_PATH = Paths.get(System.getenv("HOME"), ".config", "mcpkg");
            else
                CONFIG_PATH = Paths.get(partialConfigPath, "mcpkg");
        }

        File dataFile = DATA_PATH.toFile();
        if (!dataFile.exists())
            dataFile.mkdirs();
        File configFile = CONFIG_PATH.toFile();
        if (!configFile.exists())
            configFile.mkdirs();
    };

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
