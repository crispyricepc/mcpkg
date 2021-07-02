package dev.benmitchell.mcpkg.sources;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import dev.benmitchell.mcpkg.MCPKGLogger;
import dev.benmitchell.mcpkg.Platform;
import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;
import dev.benmitchell.mcpkg.packs.Pack;

public class LocalSource extends PackSource {

    @Override
    public List<Pack> getPacks() throws IOException {
        List<Pack> dataPacks = new ArrayList<Pack>();
        try {
            for (File file : Platform.getDataPacksDir().toFile().listFiles()) {

            }
        } catch (InvalidDirectoryException ex) {
            MCPKGLogger.log(Level.WARNING, ex.getMessage());
        }
        return null;
    }

    @Override
    public List<Pack> getPacks(List<String> packIds) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
}
