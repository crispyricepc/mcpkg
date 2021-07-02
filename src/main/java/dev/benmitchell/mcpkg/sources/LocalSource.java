package dev.benmitchell.mcpkg.sources;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import dev.benmitchell.mcpkg.MCPKGLogger;
import dev.benmitchell.mcpkg.Platform;
import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;
import dev.benmitchell.mcpkg.packs.LocalPack;
import dev.benmitchell.mcpkg.packs.Pack;

public class LocalSource extends PackSource {
    @Override
    public List<Pack> getPacks() throws IOException {
        List<Pack> packs = new ArrayList<Pack>();
        List<File> files = new ArrayList<File>();

        // Get data packs
        try {
            files.addAll(Arrays.asList(Platform.getDataPacksDir().toFile().listFiles()));
        } catch (InvalidDirectoryException ex) {
            MCPKGLogger.log(Level.WARNING, ex.getMessage());
        }
        // Get resource packs
        files.addAll(Arrays.asList(Platform.getResourcePacksDir().toFile().listFiles()));

        for (File file : files) {
            // Ignore any non .zip files
            if (!FilenameUtils.getExtension(file.getName()).equals("zip"))
                continue;
            packs.add(LocalPack.fromFile(file));
        }
        return packs;
    }
}
