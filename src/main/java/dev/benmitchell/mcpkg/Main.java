package dev.benmitchell.mcpkg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.benmitchell.mcpkg.exceptions.MCPKGException;
import dev.benmitchell.mcpkg.packs.Pack;
import dev.benmitchell.mcpkg.sources.PackSource;
import dev.benmitchell.mcpkg.vanillatweaks.VTSource;

public class Main {
    public static void main(String[] args) {
        try {
            PackSource src = new VTSource();
            List<String> packsToGet = new ArrayList<String>();
            packsToGet.add("back");
            List<Pack> packs = src.searchForPacks(packsToGet);
            packs = src.downloadPacks(packs);
            for (Pack pack : packs)
                pack.install();
        } catch (IOException ex) {
            MCPKGLogger.err(ex);
        } catch (MCPKGException ex) {
            MCPKGLogger.err(ex);
        }
    }
}
