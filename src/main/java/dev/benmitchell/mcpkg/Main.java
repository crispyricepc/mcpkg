package dev.benmitchell.mcpkg;

import java.util.ArrayList;
import java.util.List;

import dev.benmitchell.mcpkg.packs.Pack;
import dev.benmitchell.mcpkg.sources.PackSource;
import dev.benmitchell.mcpkg.vanillatweaks.VTSource;

public class Main {
    public static void main(String[] args) {
        PackSource src = new VTSource();
        List<String> packsToGet = new ArrayList<String>();
        packsToGet.add("slime");
        List<Pack> packs = src.searchForPacks(packsToGet);
        src.downloadPacks(packs);
    }
}
