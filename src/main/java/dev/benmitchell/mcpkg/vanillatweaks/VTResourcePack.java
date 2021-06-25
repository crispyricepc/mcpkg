package dev.benmitchell.mcpkg.vanillatweaks;

import java.nio.file.Path;

import org.json.simple.JSONObject;

import dev.benmitchell.mcpkg.packs.PackType;

public class VTResourcePack extends VTPack {
    public VTResourcePack(JSONObject jObject) {
        super(jObject, PackType.RESOURCEPACK);
    }

    @Override
    public void installTo(Path destination) {
        // TODO Auto-generated method stub

    }
}
