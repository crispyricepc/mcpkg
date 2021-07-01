package dev.benmitchell.mcpkg.vanillatweaks;

import org.json.simple.JSONObject;

import dev.benmitchell.mcpkg.Platform;
import dev.benmitchell.mcpkg.exceptions.InvalidDirectoryException;
import dev.benmitchell.mcpkg.packs.PackType;

public class VTResourcePack extends VTPack {
    public VTResourcePack(JSONObject jObject, String category) {
        super(jObject, PackType.RESOURCEPACK, category);
    }

    @Override
    public void install() throws InvalidDirectoryException {
        installTo(Platform.getResourcePacksDir());
    }
}
