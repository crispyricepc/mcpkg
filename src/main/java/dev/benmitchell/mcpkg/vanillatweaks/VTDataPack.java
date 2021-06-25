package dev.benmitchell.mcpkg.vanillatweaks;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.json.simple.JSONObject;

import dev.benmitchell.mcpkg.packs.PackType;

public class VTDataPack extends VTPack {
    public VTDataPack(JSONObject jObject) {
        super(jObject, PackType.CRAFTINGTWEAK);
    }

    @Override
    public void installTo(Path destination) {
        // TODO Auto-generated method stub

    }
}
