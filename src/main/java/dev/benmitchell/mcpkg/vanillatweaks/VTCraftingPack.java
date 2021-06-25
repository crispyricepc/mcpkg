package dev.benmitchell.mcpkg.vanillatweaks;

import java.nio.file.Path;

import org.json.simple.JSONObject;

import dev.benmitchell.mcpkg.packs.PackType;

public class VTCraftingPack extends VTPack {
    public VTCraftingPack(JSONObject jObject) {
        super(jObject, PackType.CRAFTINGTWEAK);
    }

    @Override
    public void installTo(Path destination) {
        // TODO Auto-generated method stub

    }
}
