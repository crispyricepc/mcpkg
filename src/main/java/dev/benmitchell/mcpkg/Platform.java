package dev.benmitchell.mcpkg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.System.Logger.Level;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.SystemUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;

public class Platform {
    private static final Path DOT_MINECRAFT_PATH;
    private static final Path DATA_PATH;
    private static final Path CONFIG_PATH;
    static {
        if (SystemUtils.IS_OS_WINDOWS) {
            DOT_MINECRAFT_PATH = Paths.get(System.getenv("APPDATA"), ".minecraft");
            DATA_PATH = Paths.get(System.getenv("APPDATA"), "mcpkg");
            CONFIG_PATH = DATA_PATH;
        } else if (SystemUtils.IS_OS_MAC) {
            DOT_MINECRAFT_PATH = Paths.get(System.getenv("HOME"), "Library", "Application Support", "minecraft");
            DATA_PATH = Paths.get(System.getenv("HOME"), "Library", "Application Support", "mcpkg");
            CONFIG_PATH = Paths.get(System.getenv("HOME"), "Library", "Preferences", "mcpkg");
        } else {
            DOT_MINECRAFT_PATH = Paths.get(System.getenv("HOME"), ".minecraft");
            String partialDataPath = System.getenv("XDG_DATA_HOME");
            if (partialDataPath == null)
                DATA_PATH = Paths.get(System.getenv("HOME"), ".local", "share", "mcpkg");
            else
                DATA_PATH = Paths.get(partialDataPath, "mcpkg");
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

    private static JSONObject configFile;
    static {
        try {
            JSONParser parser = new JSONParser();
            File cfgFile = configPath().resolve("config.json").toFile();
            if (!cfgFile.exists())
                try (FileWriter writer = new FileWriter(cfgFile)) {
                    writer.write("{\n}\n");
                }
            try (Reader reader = new BufferedReader(new FileReader(cfgFile))) {
                configFile = (JSONObject) parser.parse(reader);
            } catch (FileNotFoundException ex) {
                MCPKGLogger.err(ex);
                MCPKGLogger.log(Level.ERROR, "If you've reached this code, file a bug report");
                System.exit(-1);
            } catch (ParseException ex) {
                MCPKGLogger.err(ex);
                System.exit(-1);
            }
        } catch (IOException ex) {
            MCPKGLogger.err(ex);
            System.exit(-1);
        }
    }

    public static Path dotMinecraftPath() {
        return (Path) configFile.getOrDefault("dotMinecraftPath", DOT_MINECRAFT_PATH);
    }

    public static Path dataPath() {
        return (Path) configFile.getOrDefault("dataPath", DATA_PATH);
    }

    public static Path configPath() {
        return CONFIG_PATH;
    }

    public static Path getResourcePacksDir() {
        return dotMinecraftPath().resolve("resourcepacks");
    }

    public static boolean isADataPacksDir(Path directory) {
        return directory // .minecraft/saves/some_save_folder/datapacks
                .getParent() // .minecraft/saves/some_save_folder
                .getParent() // .minecraft/saves
                .equals(dotMinecraftPath().resolve("saves")) && directory.getFileName().equals(Paths.get("datapacks"));
    }

    public static Path getDataPacksDir() throws InvalidDirectoryException {
        Path cwd = Paths.get(SystemUtils.USER_DIR);

        // If we're in a datapacks directory inside .minecraft
        if (isADataPacksDir(cwd))
            return cwd;
        // If we're in a worlds directory inside .minecraft
        if (cwd // .minecraft/saves/some_save_folder
                .getParent() // .minecraft/saves
                .equals(dotMinecraftPath().resolve("saves")))
            return cwd.resolve("datapacks");

        throw new InvalidDirectoryException(cwd, "A unique data pack directory couldn't be found");
    }
}
