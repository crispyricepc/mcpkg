package dev.benmitchell.mcpkg.vanillatweaks;

import java.io.IOException;

import org.json.simple.JSONObject;

import dev.benmitchell.mcpkg.Platform;
import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;
import dev.benmitchell.mcpkg.exceptions.PackNotDownloadedException;
import dev.benmitchell.mcpkg.packs.PackType;

public class VTCraftingPack extends VTPack {
    public VTCraftingPack(JSONObject jObject, String category) {
        super(jObject, PackType.CRAFTINGTWEAK, category);
    }

    @Override
    public void install() throws IOException, InvalidDirectoryException, PackNotDownloadedException {
        installTo(Platform.getDataPacksDir());
    }
}
