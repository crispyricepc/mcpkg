package dev.benmitchell.mcpkg.vanillatweaks;

import java.util.ArrayList;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import dev.benmitchell.mcpkg.packs.Pack;
import dev.benmitchell.mcpkg.packs.PackType;

public abstract class VTPack extends Pack {
    // Vanilla tweaks remote data
    private String name;
    private String category;

    public VTPack(JSONObject jObject, PackType pType, String category) {
        super("VanillaTweaks." + (String) jObject.get("name"), (String) jObject.get("display"),
                (String) jObject.get("description"), new Version((String) jObject.get("version")),
                new ArrayList<String>(), new ArrayList<String>(), pType, Optional.empty());
        name = (String) jObject.get("name");
        for (Object item : (JSONArray) jObject.get("incompatible")) {
            incompatibilities.add((String) item);
        }

        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }
}
